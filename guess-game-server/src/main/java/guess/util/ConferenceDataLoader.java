package guess.util;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.Identifier;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.image.UrlFilename;
import guess.domain.source.load.LoadResult;
import guess.domain.source.load.LoadSettings;
import guess.domain.source.load.SpeakerLoadMaps;
import guess.domain.source.load.SpeakerLoadResult;
import guess.util.yaml.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Conference data loader.
 */
public class ConferenceDataLoader {
    private static final Logger log = LoggerFactory.getLogger(ConferenceDataLoader.class);

    /**
     * Loads all conference event types.
     *
     * @throws IOException                if file creation error occurs
     * @throws SpeakerDuplicatedException if speaker duplicated
     * @throws IOException                if file creation error occurs
     * @throws SpeakerDuplicatedException if speaker duplicated
     * @throws NoSuchFieldException       if field name is invalid
     */
    static void loadEventTypes() throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
        // Read event types from resource files
        SourceInformation resourceSourceInformation = YamlUtils.readSourceInformation();
        List<EventType> resourceEventTypes = getConferences(resourceSourceInformation.getEventTypes());
        log.info("Event types (in resource files): {}", resourceEventTypes.size());

        // Read event types from Contentful
        List<EventType> contentfulEventTypes = getConferences(ContentfulUtils.getEventTypes());
        log.info("Event types (in Contentful): {}", contentfulEventTypes.size());

        // Find event types
        Map<Conference, EventType> resourceEventTypeMap = getResourceEventTypeMap(resourceEventTypes);
        AtomicLong lastEventTypeId = new AtomicLong(getLastId(resourceSourceInformation.getEventTypes()));
        LoadResult<List<EventType>> loadResult = getEventTypeLoadResult(contentfulEventTypes, resourceEventTypeMap, lastEventTypeId);

        // Save files
        saveEventTypes(loadResult);
    }

    /**
     * Gets conferences.
     *
     * @param eventTypes event types
     * @return conferences
     */
    static List<EventType> getConferences(List<EventType> eventTypes) {
        return eventTypes.stream()
                .filter(et -> et.getConference() != null)
                .collect(Collectors.toList());
    }

    /**
     * Creates conference map.
     *
     * @param eventTypes event types
     * @return conference map
     */
    static Map<Conference, EventType> getResourceEventTypeMap(List<EventType> eventTypes) {
        return eventTypes.stream()
                .collect(Collectors.toMap(
                        EventType::getConference,
                        et -> et));
    }

    /**
     * Gets last identifier.
     *
     * @param entities entities
     * @return identifier
     */
    static <T extends Identifier> long getLastId(List<T> entities) {
        return entities.stream()
                .map(Identifier::getId)
                .max(Long::compare)
                .orElse(-1L);
    }

    /**
     * Gets load result for event types.
     *
     * @param eventTypes      event types
     * @param eventTypeMap    event type map
     * @param lastEventTypeId identifier of last event type
     * @return load result for event types
     */
    static LoadResult<List<EventType>> getEventTypeLoadResult(List<EventType> eventTypes, Map<Conference, EventType> eventTypeMap,
                                                              AtomicLong lastEventTypeId) {
        List<EventType> eventTypesToAppend = new ArrayList<>();
        List<EventType> eventTypesToUpdate = new ArrayList<>();

        eventTypes.forEach(
                et -> {
                    EventType resourceEventType = eventTypeMap.get(et.getConference());

                    if (resourceEventType == null) {
                        // Event type not exists
                        et.setId(lastEventTypeId.incrementAndGet());

                        eventTypesToAppend.add(et);
                    } else {
                        // Event type exists
                        et.setId(resourceEventType.getId());
                        et.setShortDescription(resourceEventType.getShortDescription());
                        et.setLogoFileName(resourceEventType.getLogoFileName());

                        if (ContentfulUtils.needUpdate(resourceEventType, et)) {
                            // Event type need to update
                            eventTypesToUpdate.add(et);
                        }
                    }
                }
        );

        return new LoadResult<>(
                Collections.emptyList(),
                eventTypesToAppend,
                eventTypesToUpdate);
    }

    /**
     * Saves event types.
     *
     * @param loadResult load result
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void saveEventTypes(LoadResult<List<EventType>> loadResult) throws IOException, NoSuchFieldException {
        List<EventType> eventTypesToAppend = loadResult.getItemToAppend();
        List<EventType> eventTypesToUpdate = loadResult.getItemToUpdate();

        if (eventTypesToAppend.isEmpty() && eventTypesToUpdate.isEmpty()) {
            log.info("All event types are up-to-date");
        } else {
            YamlUtils.clearDumpDirectory();

            if (!eventTypesToAppend.isEmpty()) {
                logAndDumpEventTypes(eventTypesToAppend, "Event types (to append resource file): {}", "event-types-to-append.yml");
            }

            if (!eventTypesToUpdate.isEmpty()) {
                eventTypesToUpdate.sort(Comparator.comparing(EventType::getId));
                logAndDumpEventTypes(eventTypesToUpdate, "Event types (to update resource file): {}", "event-types-to-update.yml");
            }
        }
    }

    /**
     * Loads talks, speakers, event information.
     *
     * @param conference     conference
     * @param startDate      start date
     * @param conferenceCode conference code
     * @param loadSettings   load settings
     * @throws IOException                if resource files could not be opened
     * @throws SpeakerDuplicatedException if speakers duplicated
     * @throws NoSuchFieldException       if field name is invalid
     */
    static void loadTalksSpeakersEvent(Conference conference, LocalDate startDate, String conferenceCode,
                                       LoadSettings loadSettings) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
        log.info("{} {} {}", conference, startDate, conferenceCode);

        // Read event types, places, events, companies, speakers, talks from resource files
        SourceInformation resourceSourceInformation = YamlUtils.readSourceInformation();
        Optional<EventType> resourceOptionalEventType = resourceSourceInformation.getEventTypes().stream()
                .filter(et -> et.getConference().equals(conference))
                .findFirst();
        EventType resourceEventType = resourceOptionalEventType
                .orElseThrow(() -> new IllegalStateException(String.format("No event type found for conference %s (in resource files)", conference)));
        log.info("Event type (in resource files): nameEn: {}, nameRu: {}",
                LocalizationUtils.getString(resourceEventType.getName(), Language.ENGLISH),
                LocalizationUtils.getString(resourceEventType.getName(), Language.RUSSIAN));

        Event resourceEvent = resourceOptionalEventType
                .flatMap(et -> et.getEvents().stream()
                        .filter(e -> e.getStartDate().equals(startDate))
                        .findFirst())
                .orElse(null);
        if (resourceEvent == null) {
            log.info("Event (in resource files) not found");
        } else {
            log.info("Event (in resource files): nameEn: {}, nameRu: {}, startDate: {}, endDate: {}",
                    LocalizationUtils.getString(resourceEvent.getName(), Language.ENGLISH),
                    LocalizationUtils.getString(resourceEvent.getName(), Language.RUSSIAN),
                    resourceEvent.getStartDate(), resourceEvent.getEndDate());
        }

        // Read event from Contentful
        Event contentfulEvent = ContentfulUtils.getEvent(conference, startDate);
        log.info("Event (in Contentful): nameEn: {}, nameRu: {}, startDate: {}, endDate: {}",
                LocalizationUtils.getString(contentfulEvent.getName(), Language.ENGLISH),
                LocalizationUtils.getString(contentfulEvent.getName(), Language.RUSSIAN),
                contentfulEvent.getStartDate(), contentfulEvent.getEndDate());

        // Read talks from Contentful
        List<Talk> contentfulTalks = ContentfulUtils.getTalks(conference, conferenceCode, loadSettings.isIgnoreDemoStage());
        log.info("Talks (in Contentful): {}", contentfulTalks.size());
        contentfulTalks.forEach(
                t -> log.trace("Talk: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(t.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(t.getName(), Language.RUSSIAN))
        );

        // Delete invalid talks
        contentfulTalks = deleteInvalidTalks(contentfulTalks, loadSettings.getInvalidTalksSet());

        // Delete opening and closing talks
        contentfulTalks = deleteOpeningAndClosingTalks(contentfulTalks);

        // Delete talk duplicates
        contentfulTalks = deleteTalkDuplicates(contentfulTalks);

        // Order speakers with talk order
        List<Speaker> contentfulSpeakers = getTalkSpeakers(contentfulTalks);
        log.info("Speakers (in Contentful): {}", contentfulSpeakers.size());
        contentfulSpeakers.forEach(
                s -> log.trace("Speaker: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(s.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(s.getName(), Language.RUSSIAN))
        );

        // Order company with talk order
        List<Company> contentfulCompanies = getSpeakerCompanies(contentfulSpeakers);
        log.info("Companies (in Contentful): {}", contentfulCompanies.size());
        contentfulCompanies.forEach(
                c -> log.trace("Company: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(c.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(c.getName(), Language.RUSSIAN))
        );

        // Find companies
        Map<String, Company> resourceCompanyMap = getResourceLowerNameCompanyMap(resourceSourceInformation.getCompanies());
        addLowerSynonymsToCompanyMap(resourceSourceInformation.getCompanySynonyms(), resourceCompanyMap);

        AtomicLong lastCompanyId = new AtomicLong(getLastId(resourceSourceInformation.getCompanies()));
        LoadResult<List<Company>> companyLoadResult = getCompanyLoadResult(
                contentfulCompanies,
                resourceCompanyMap,
                lastCompanyId);

        // Find speakers
        fillCompanyIds(contentfulSpeakers);

        Map<Long, Speaker> resourceSpeakerIdsMap = resourceSourceInformation.getSpeakers().stream()
                .collect(Collectors.toMap(
                        Speaker::getId,
                        s -> s
                ));
        Map<NameCompany, Speaker> resourceNameCompanySpeakers = getResourceNameCompanySpeakerMap(resourceSourceInformation.getSpeakers());
        Map<String, Set<Speaker>> resourceNameSpeakers = getResourceNameSpeakersMap(resourceSourceInformation.getSpeakers());
        AtomicLong lastSpeakerId = new AtomicLong(getLastId(resourceSourceInformation.getSpeakers()));
        SpeakerLoadResult speakerLoadResult = getSpeakerLoadResult(
                contentfulSpeakers,
                new SpeakerLoadMaps(
                        loadSettings.getKnownSpeakerIdsMap(),
                        resourceSpeakerIdsMap,
                        resourceNameCompanySpeakers,
                        resourceNameSpeakers),
                lastSpeakerId);

        // Find talks
        fillSpeakerIds(contentfulTalks);

        AtomicLong lastTalksId = new AtomicLong(getLastId(resourceSourceInformation.getTalks()));
        LoadResult<List<Talk>> talkLoadResult = getTalkLoadResult(
                contentfulTalks,
                resourceEvent,
                resourceSourceInformation.getEvents(),
                lastTalksId);

        // Find place
        Map<CityVenueAddress, Place> resourceRuCityVenueAddressPlaces = resourceSourceInformation.getPlaces().stream()
                .collect(Collectors.toMap(
                        p -> new CityVenueAddress(
                                LocalizationUtils.getString(p.getCity(), Language.RUSSIAN).trim(),
                                LocalizationUtils.getString(p.getVenueAddress(), Language.RUSSIAN).trim()),
                        p -> p
                ));
        Map<CityVenueAddress, Place> resourceEnCityVenueAddressPlaces = resourceSourceInformation.getPlaces().stream()
                .collect(Collectors.toMap(
                        p -> new CityVenueAddress(
                                LocalizationUtils.getString(p.getCity(), Language.ENGLISH).trim(),
                                LocalizationUtils.getString(p.getVenueAddress(), Language.ENGLISH).trim()),
                        p -> p
                ));
        Place contentfulPlace = contentfulEvent.getPlace();
        contentfulPlace.setVenueAddress(fixVenueAddress(contentfulPlace));
        Place resourcePlace = findResourcePlace(contentfulPlace, resourceRuCityVenueAddressPlaces, resourceEnCityVenueAddressPlaces);
        AtomicLong lastPlaceId = new AtomicLong(getLastId(resourceSourceInformation.getPlaces()));
        LoadResult<Place> placeLoadResult = getPlaceLoadResult(contentfulPlace, resourcePlace, lastPlaceId);

        contentfulEvent.setPlaceId(contentfulPlace.getId());

        // Find event
        contentfulEvent.setEventType(resourceEventType);
        contentfulEvent.setEventTypeId(resourceEventType.getId());
        contentfulEvent.setTalks(contentfulTalks);
        contentfulEvent.setTalkIds(contentfulTalks.stream()
                .map(Talk::getId)
                .collect(Collectors.toList()));

        LoadResult<Event> eventLoadResult = getEventLoadResult(contentfulEvent, resourceEvent);

        // Save files
        saveFiles(companyLoadResult, speakerLoadResult, talkLoadResult, placeLoadResult, eventLoadResult);
    }

    /**
     * Loads talks, speakers, event information.
     *
     * @param conference     conference
     * @param startDate      start date
     * @param conferenceCode conference code
     * @throws IOException                if resource files could not be opened
     * @throws SpeakerDuplicatedException if speakers duplicated
     * @throws NoSuchFieldException       if field name is invalid
     */
    static void loadTalksSpeakersEvent(Conference conference, LocalDate startDate, String conferenceCode)
            throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
        loadTalksSpeakersEvent(conference, startDate, conferenceCode, LoadSettings.defaultSettings());
    }

    /**
     * Deletes invalid talks.
     *
     * @param talks           talks
     * @param invalidTalksSet invalid talk name set
     * @return talks without invalid talks
     */
    static List<Talk> deleteInvalidTalks(List<Talk> talks, Set<String> invalidTalksSet) {
        if (invalidTalksSet.isEmpty()) {
            return talks;
        } else {
            List<Talk> fixedTalks = talks.stream()
                    .filter(t -> !invalidTalksSet.contains(LocalizationUtils.getString(t.getName(), Language.RUSSIAN)))
                    .collect(Collectors.toList());

            log.info("Fixed talks (in Contentful): {}", fixedTalks.size());
            fixedTalks.forEach(
                    t -> log.trace("Fixed talk: nameEn: '{}', name: '{}'",
                            LocalizationUtils.getString(t.getName(), Language.ENGLISH),
                            LocalizationUtils.getString(t.getName(), Language.RUSSIAN))
            );

            return fixedTalks;
        }
    }

    /**
     * Deletes opening and closing talks.
     *
     * @param talks talks
     * @return talks without opening and closing
     */
    static List<Talk> deleteOpeningAndClosingTalks(List<Talk> talks) {
        Set<String> deletedTalks = Set.of("Conference opening", "Conference closing", "School opening", "School closing",
                "Открытие", "Закрытие");

        return talks.stream()
                .filter(t -> {
                    String enName = LocalizationUtils.getString(t.getName(), Language.ENGLISH).trim();
                    String ruName = LocalizationUtils.getString(t.getName(), Language.RUSSIAN).trim();

                    if (deletedTalks.contains(enName)) {
                        log.warn("Conference opening or closing talk is deleted, name: '{}', '{}', talkDay: {}, trackTime: {}, track: {}, language: {}",
                                enName, ruName, t.getTalkDay(), t.getTrackTime(), t.getTrack(), t.getLanguage());

                        return false;
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Deletes talk duplicates (with more talk day, track, track time).
     *
     * @param talks talks
     * @return talks without duplicates
     */
    static List<Talk> deleteTalkDuplicates(List<Talk> talks) {
        Map<String, Talk> ruNameMap = new HashMap<>();

        for (Talk talk : talks) {
            String ruName = LocalizationUtils.getString(talk.getName(), Language.RUSSIAN);
            Talk existingTalk = ruNameMap.get(ruName);

            if (existingTalk == null) {
                ruNameMap.put(ruName, talk);
            } else {
                long newTalkDay = Optional.ofNullable(talk.getTalkDay()).orElse(0L);
                long existingTalkDay = Optional.ofNullable(existingTalk.getTalkDay()).orElse(0L);
                long newTalkTrack = Optional.ofNullable(talk.getTrack()).orElse(0L);
                long existingTalkTrack = Optional.ofNullable(existingTalk.getTrack()).orElse(0L);
                LocalTime newTalkTrackTime = Optional.ofNullable(talk.getTrackTime()).orElse(LocalTime.of(0, 0));
                LocalTime existingTalkTrackTime = Optional.ofNullable(existingTalk.getTrackTime()).orElse(LocalTime.of(0, 0));

                if ((newTalkDay < existingTalkDay) ||
                        ((newTalkDay == existingTalkDay) &&
                                ((newTalkTrack < existingTalkTrack) ||
                                        ((newTalkTrack == existingTalkTrack) && newTalkTrackTime.isBefore(existingTalkTrackTime))))) {
                    // (Less day) or
                    // (Equal day) and ((Less track) or ((Equal track) and (Less track time)))
                    ruNameMap.put(ruName, talk);
                }
            }
        }

        return talks.stream()
                .filter(t -> t.equals(ruNameMap.get(LocalizationUtils.getString(t.getName(), Language.RUSSIAN))))
                .collect(Collectors.toList());
    }

    /**
     * Gets talk speakers.
     *
     * @param talks talks
     * @return talk speakers
     */
    static List<Speaker> getTalkSpeakers(List<Talk> talks) {
        return talks.stream()
                .flatMap(t -> t.getSpeakers().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Gets speaker companies.
     *
     * @param speakers speakers
     * @return speaker companies
     */
    static List<Company> getSpeakerCompanies(List<Speaker> speakers) {
        return speakers.stream()
                .flatMap(s -> s.getCompanies().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Gets resource lower name/company map.
     *
     * @param companies companies
     * @return lower name/company map
     */
    static Map<String, Company> getResourceLowerNameCompanyMap(List<Company> companies) {
        Map<String, Company> map = new HashMap<>();

        for (Company company : companies) {
            for (LocaleItem localItem : company.getName()) {
                map.put(localItem.getText().toLowerCase(), company);
            }
        }

        return map;
    }

    /**
     * Adds lower synonyms to company map.
     *
     * @param companySynonymsList company synonyms list
     * @param companyMap          company map
     */
    static void addLowerSynonymsToCompanyMap(List<CompanySynonyms> companySynonymsList, Map<String, Company> companyMap) {
        for (CompanySynonyms companySynonyms : companySynonymsList) {
            String lowerName = companySynonyms.getName().toLowerCase();
            Company company = companyMap.get(lowerName);

            Objects.requireNonNull(company,
                    () -> String.format("Resource company with lower name '%s' not found (change name '%s' of synonyms in company-synonyms.yml file and rerun loading)",
                            lowerName,
                            companySynonyms.getName()));

            for (String synonym : companySynonyms.getSynonyms()) {
                if (companySynonyms.getName().equals(synonym)) {
                    throw new IllegalArgumentException(String.format(
                            "Company name matches the synonym '%s' (change synonym '%s' for name '%s' in company-synonyms.yml file and rerun loading)",
                            synonym, synonym, companySynonyms.getName()));
                }

                companyMap.put(synonym.toLowerCase(), company);
            }
        }
    }

    /**
     * Gets load result for companies.
     *
     * @param companies          companies
     * @param resourceCompanyMap resource company map
     * @param lastCompanyId      identifier of last company
     * @return load result for companies
     */
    static LoadResult<List<Company>> getCompanyLoadResult(List<Company> companies, Map<String, Company> resourceCompanyMap,
                                                          AtomicLong lastCompanyId) {
        List<Company> companiesToAppend = new ArrayList<>();

        for (Company c : companies) {
            if (!c.getName().isEmpty()) {
                Company resourceCompany = findResourceCompany(c, resourceCompanyMap);

                if (resourceCompany == null) {
                    // Company not exists
                    c.setId(lastCompanyId.incrementAndGet());

                    companiesToAppend.add(c);
                } else {
                    // Company exists
                    c.setId(resourceCompany.getId());
                }
            }
        }

        return new LoadResult<>(
                Collections.emptyList(),
                companiesToAppend,
                Collections.emptyList());
    }

    /**
     * Fills company identifiers in speakers.
     *
     * @param speakers speakers
     */
    static void fillCompanyIds(List<Speaker> speakers) {
        speakers.forEach(
                s -> s.setCompanyIds(s.getCompanies().stream()
                        .map(Company::getId)
                        .collect(Collectors.toList())
                )
        );
    }

    /**
     * Gets resource name, company/speaker map.
     *
     * @param speakers speakers
     * @return name, company/speaker map
     */
    static Map<NameCompany, Speaker> getResourceNameCompanySpeakerMap(List<Speaker> speakers) {
        Map<NameCompany, Speaker> map = new HashMap<>();

        for (Speaker speaker : speakers) {
            for (LocaleItem localeItem : speaker.getName()) {
                for (Company company : speaker.getCompanies()) {
                    map.put(new NameCompany(localeItem.getText(), company), speaker);
                }
            }
        }

        return map;
    }

    /**
     * Gets resource name/speakers map.
     *
     * @param speakers speakers
     * @return name/speakers map
     */
    static Map<String, Set<Speaker>> getResourceNameSpeakersMap(List<Speaker> speakers) {
        Map<String, Set<Speaker>> map = new HashMap<>();

        for (Speaker speaker : speakers) {
            for (LocaleItem localeItem : speaker.getName()) {
                map.computeIfAbsent(localeItem.getText(), k -> new HashSet<>());
                map.get(localeItem.getText()).add(speaker);
            }
        }

        return map;
    }

    /**
     * Gets load result for speakers.
     *
     * @param speakers        speakers
     * @param speakerLoadMaps speaker load maps
     * @param lastSpeakerId   identifier of last speaker
     * @return load result for speakers
     * @throws IOException if read error occurs
     */
    static SpeakerLoadResult getSpeakerLoadResult(List<Speaker> speakers, SpeakerLoadMaps speakerLoadMaps,
                                                  AtomicLong lastSpeakerId) throws IOException {
        List<Speaker> speakersToAppend = new ArrayList<>();
        List<Speaker> speakersToUpdate = new ArrayList<>();
        List<UrlFilename> urlFilenamesToAppend = new ArrayList<>();
        List<UrlFilename> urlFilenamesToUpdate = new ArrayList<>();

        for (Speaker speaker : speakers) {
            Speaker resourceSpeaker = findResourceSpeaker(speaker, speakerLoadMaps);

            if (resourceSpeaker == null) {
                // Speaker not exists
                long id = lastSpeakerId.incrementAndGet();
                String sourceUrl = speaker.getPhotoFileName();
                String destinationFileName = String.format("%04d.jpg", id);

                speaker.setId(id);

                urlFilenamesToAppend.add(new UrlFilename(sourceUrl, destinationFileName));
                speaker.setPhotoFileName(destinationFileName);

                speakersToAppend.add(speaker);
            } else {
                // Speaker exists
                speaker.setId(resourceSpeaker.getId());
                String targetPhotoUrl = speaker.getPhotoFileName();
                String resourcePhotoFileName = resourceSpeaker.getPhotoFileName();
                speaker.setPhotoFileName(resourcePhotoFileName);

                fillSpeakerTwitter(speaker, resourceSpeaker);
                fillSpeakerGitHub(speaker, resourceSpeaker);
                fillSpeakerJavaChampion(speaker, resourceSpeaker);
                fillSpeakerMvp(speaker, resourceSpeaker);

                // Update speaker photo
                if (ContentfulUtils.needPhotoUpdate(speaker.getPhotoUpdatedAt(), resourceSpeaker.getPhotoUpdatedAt(), targetPhotoUrl, resourcePhotoFileName)) {
                    urlFilenamesToUpdate.add(new UrlFilename(targetPhotoUrl, resourcePhotoFileName));
                }

                fillUpdatedAt(speaker, resourceSpeaker);

                // Update speaker
                if (ContentfulUtils.needUpdate(resourceSpeaker, speaker)) {
                    speakersToUpdate.add(speaker);
                }
            }
        }

        return new SpeakerLoadResult(
                new LoadResult<>(
                        Collections.emptyList(),
                        speakersToAppend,
                        speakersToUpdate),
                new LoadResult<>(
                        Collections.emptyList(),
                        urlFilenamesToAppend,
                        urlFilenamesToUpdate));
    }

    /**
     * Fills speaker Twitter.
     *
     * @param targetSpeaker   target speaker
     * @param resourceSpeaker resource speaker
     */
    static void fillSpeakerTwitter(Speaker targetSpeaker, Speaker resourceSpeaker) {
        if ((resourceSpeaker.getTwitter() != null) && !resourceSpeaker.getTwitter().isEmpty() &&
                ((targetSpeaker.getTwitter() == null) || targetSpeaker.getTwitter().isEmpty())) {
            targetSpeaker.setTwitter(resourceSpeaker.getTwitter());
        }
    }

    /**
     * Fills speaker GitHub.
     *
     * @param targetSpeaker   target speaker
     * @param resourceSpeaker resource speaker
     */
    static void fillSpeakerGitHub(Speaker targetSpeaker, Speaker resourceSpeaker) {
        if ((resourceSpeaker.getGitHub() != null) && !resourceSpeaker.getGitHub().isEmpty() &&
                ((targetSpeaker.getGitHub() == null) || targetSpeaker.getGitHub().isEmpty())) {
            targetSpeaker.setGitHub(resourceSpeaker.getGitHub());
        }
    }

    /**
     * Fills speaker Java Champion.
     *
     * @param targetSpeaker   target speaker
     * @param resourceSpeaker resource speaker
     */
    static void fillSpeakerJavaChampion(Speaker targetSpeaker, Speaker resourceSpeaker) {
        if (resourceSpeaker.isJavaChampion() && !targetSpeaker.isJavaChampion()) {
            targetSpeaker.setJavaChampion(true);
        }
    }

    /**
     * Fills speaker MVP.
     *
     * @param targetSpeaker   target speaker
     * @param resourceSpeaker resource speaker
     */
    static void fillSpeakerMvp(Speaker targetSpeaker, Speaker resourceSpeaker) {
        if (targetSpeaker.isMvpReconnect()) {
            targetSpeaker.setMvp(false);
        } else {
            if (targetSpeaker.isMvp()) {
                targetSpeaker.setMvpReconnect(false);
            } else {
                // Neither "MVP" nor "MVP Reconnect" in Contentful
                if (resourceSpeaker.isMvpReconnect()) {
                    targetSpeaker.setMvpReconnect(true);
                    targetSpeaker.setMvp(false);
                } else if (resourceSpeaker.isMvp()) {
                    targetSpeaker.setMvp(true);
                    targetSpeaker.setMvpReconnect(false);
                }
            }
        }
    }

    /**
     * Fills speaker updated datetime.
     *
     * @param targetSpeaker   target speaker
     * @param resourceSpeaker resource speaker
     */
    static void fillUpdatedAt(Speaker targetSpeaker, Speaker resourceSpeaker) {
        ZonedDateTime targetPhotoUpdatedAt = targetSpeaker.getPhotoUpdatedAt();
        ZonedDateTime resourcePhotoUpdatedAt = resourceSpeaker.getPhotoUpdatedAt();

        if ((targetPhotoUpdatedAt != null) && (resourcePhotoUpdatedAt != null) && (resourcePhotoUpdatedAt.isAfter(targetPhotoUpdatedAt))) {
            targetSpeaker.setPhotoUpdatedAt(resourcePhotoUpdatedAt);
        }
    }

    /**
     * Fills speaker identifiers in talks.
     *
     * @param talks talks
     */
    static void fillSpeakerIds(List<Talk> talks) {
        talks.forEach(
                t -> t.setSpeakerIds(t.getSpeakers().stream()
                        .map(Speaker::getId)
                        .collect(Collectors.toList())
                )
        );
    }

    /**
     * Gets talk load result.
     *
     * @param talks          talks
     * @param resourceEvent  resource event of talks
     * @param resourceEvents all resource events
     * @param lastTalksId    identifier of last talk
     * @return talk load result
     */
    static LoadResult<List<Talk>> getTalkLoadResult(List<Talk> talks, Event resourceEvent, List<Event> resourceEvents,
                                                    AtomicLong lastTalksId) {
        List<Talk> talksToDelete = new ArrayList<>();
        List<Talk> talksToAppend = new ArrayList<>();
        List<Talk> talksToUpdate = new ArrayList<>();

        if (resourceEvent == null) {
            // Event not exists
            talks.forEach(
                    t -> {
                        t.setId(lastTalksId.incrementAndGet());
                        talksToAppend.add(t);
                    }
            );
        } else {
            // Event exists
            Map<String, Set<Talk>> resourceRuNameTalks = resourceEvent.getTalks().stream()
                    .collect(Collectors.groupingBy(
                            t -> LocalizationUtils.getString(t.getName(), Language.RUSSIAN).trim(),
                            Collectors.toSet()
                    ));
            Map<String, Set<Talk>> resourceEnNameTalks = resourceEvent.getTalks().stream()
                    .collect(Collectors.groupingBy(
                            t -> LocalizationUtils.getString(t.getName(), Language.ENGLISH).trim(),
                            Collectors.toSet()
                    ));
            talks.forEach(
                    t -> {
                        Talk resourceTalk = findResourceTalk(t, resourceRuNameTalks, resourceEnNameTalks);

                        if (resourceTalk == null) {
                            // Talk not exists
                            t.setId(lastTalksId.incrementAndGet());
                            talksToAppend.add(t);
                        } else {
                            // Talk exists
                            t.setId(resourceTalk.getId());

                            if (ContentfulUtils.needUpdate(resourceTalk, t)) {
                                talksToUpdate.add(t);
                            }
                        }
                    }
            );

            talksToDelete = resourceEvent.getTalks().stream()
                    .filter(dt -> needDeleteTalk(talks, dt, resourceEvents, resourceEvent))
                    .collect(Collectors.toList());
        }

        return new LoadResult<>(
                talksToDelete,
                talksToAppend,
                talksToUpdate);
    }

    /**
     * Indicates the need to delete talk.
     *
     * @param talks          talks
     * @param resourceTalk   resource talk
     * @param resourceEvents resource events
     * @param resourceEvent  resource event
     * @return need to delete talk
     */
    static boolean needDeleteTalk(List<Talk> talks, Talk resourceTalk, List<Event> resourceEvents, Event resourceEvent) {
        if (talks.contains(resourceTalk)) {
            return false;
        } else {
            boolean talkExistsInAnyOtherEvent = resourceEvents.stream()
                    .filter(e -> !e.equals(resourceEvent))
                    .flatMap(e -> e.getTalks().stream())
                    .anyMatch(rt -> rt.equals(resourceTalk));

            if (talkExistsInAnyOtherEvent) {
                log.warn("Deleting '{}' talk exists in other events and can't be deleted",
                        LocalizationUtils.getString(resourceTalk.getName(), Language.ENGLISH));
            }

            return !talkExistsInAnyOtherEvent;
        }
    }

    /**
     * Gets place load result.
     *
     * @param place         place
     * @param resourcePlace resource place
     * @param lastPlaceId   identifier of last place
     * @return place load result
     */
    static LoadResult<Place> getPlaceLoadResult(Place place, Place resourcePlace, AtomicLong lastPlaceId) {
        Place placeToAppend = null;
        Place placeToUpdate = null;

        if (resourcePlace == null) {
            // Place not exists
            place.setId(lastPlaceId.incrementAndGet());
            placeToAppend = place;
        } else {
            // Place exists
            place.setId(resourcePlace.getId());

            if (ContentfulUtils.needUpdate(resourcePlace, place)) {
                placeToUpdate = place;
            }
        }

        return new LoadResult<>(
                null,
                placeToAppend,
                placeToUpdate);
    }

    /**
     * Gets event load result.
     *
     * @param event         event
     * @param resourceEvent resource event
     * @return event load result
     */
    static LoadResult<Event> getEventLoadResult(Event event, Event resourceEvent) {
        Event eventToAppend = null;
        Event eventToUpdate = null;

        if (resourceEvent == null) {
            eventToAppend = event;
        } else {
            if (ContentfulUtils.needUpdate(resourceEvent, event)) {
                eventToUpdate = event;
            }
        }

        return new LoadResult<>(
                null,
                eventToAppend,
                eventToUpdate);
    }

    /**
     * Saves files.
     *
     * @param companyLoadResult company load result
     * @param speakerLoadResult speaker load result
     * @param talkLoadResult    talk load result
     * @param placeLoadResult   place load result
     * @param eventLoadResult   event load result
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void saveFiles(LoadResult<List<Company>> companyLoadResult, SpeakerLoadResult speakerLoadResult, LoadResult<List<Talk>> talkLoadResult,
                          LoadResult<Place> placeLoadResult, LoadResult<Event> eventLoadResult) throws IOException, NoSuchFieldException {
        List<Company> companiesToAppend = companyLoadResult.getItemToAppend();

        List<Speaker> speakersToAppend = speakerLoadResult.getSpeakers().getItemToAppend();
        List<Speaker> speakersToUpdate = speakerLoadResult.getSpeakers().getItemToUpdate();

        List<UrlFilename> urlFilenamesToAppend = speakerLoadResult.getUrlFilenames().getItemToAppend();
        List<UrlFilename> urlFilenamesToUpdate = speakerLoadResult.getUrlFilenames().getItemToUpdate();

        List<Talk> talksToDelete = talkLoadResult.getItemToDelete();
        List<Talk> talksToAppend = talkLoadResult.getItemToAppend();
        List<Talk> talksToUpdate = talkLoadResult.getItemToUpdate();

        Place placeToAppend = placeLoadResult.getItemToAppend();
        Place placeToUpdate = placeLoadResult.getItemToUpdate();

        Event eventToAppend = eventLoadResult.getItemToAppend();
        Event eventToUpdate = eventLoadResult.getItemToUpdate();

        if (companiesToAppend.isEmpty() &&
                urlFilenamesToAppend.isEmpty() && urlFilenamesToUpdate.isEmpty() &&
                speakersToAppend.isEmpty() && speakersToUpdate.isEmpty() &&
                talksToDelete.isEmpty() && talksToAppend.isEmpty() && talksToUpdate.isEmpty() &&
                (eventToAppend == null) && (eventToUpdate == null) &&
                (placeToAppend == null) && (placeToUpdate == null)) {
            log.info("All companies, speakers, talks, place and event are up-to-date");
        } else {
            YamlUtils.clearDumpDirectory();

            saveCompanies(companyLoadResult);
            saveImages(speakerLoadResult);
            saveSpeakers(speakerLoadResult);
            saveTalks(talkLoadResult);
            savePlaces(placeLoadResult);
            saveEvents(eventLoadResult);
        }
    }

    /**
     * Saves companies.
     *
     * @param companyLoadResult company load result
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void saveCompanies(LoadResult<List<Company>> companyLoadResult) throws IOException, NoSuchFieldException {
        List<Company> companiesToAppend = companyLoadResult.getItemToAppend();

        if (!companiesToAppend.isEmpty()) {
            logAndDumpCompanies(companiesToAppend, "Companies (to append resource file): {}", "companies-to-append.yml");
        }
    }

    /**
     * Saves images.
     *
     * @param speakerLoadResult speaker load result
     * @throws IOException if file creation error occurs
     */
    static void saveImages(SpeakerLoadResult speakerLoadResult) throws IOException {
        List<UrlFilename> urlFilenamesToAppend = speakerLoadResult.getUrlFilenames().getItemToAppend();
        List<UrlFilename> urlFilenamesToUpdate = speakerLoadResult.getUrlFilenames().getItemToUpdate();

        if (!urlFilenamesToAppend.isEmpty()) {
            logAndCreateSpeakerImages(urlFilenamesToAppend, "Speaker images (to append): {}");
        }

        if (!urlFilenamesToUpdate.isEmpty()) {
            logAndCreateSpeakerImages(urlFilenamesToUpdate, "Speaker images (to update): {}");
        }
    }

    /**
     * Saves speakers.
     *
     * @param speakerLoadResult speaker load result
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void saveSpeakers(SpeakerLoadResult speakerLoadResult) throws IOException, NoSuchFieldException {
        List<Speaker> speakersToAppend = speakerLoadResult.getSpeakers().getItemToAppend();
        List<Speaker> speakersToUpdate = speakerLoadResult.getSpeakers().getItemToUpdate();

        if (!speakersToAppend.isEmpty()) {
            logAndDumpSpeakers(speakersToAppend, "Speakers (to append resource file): {}", "speakers-to-append.yml");
        }

        if (!speakersToUpdate.isEmpty()) {
            speakersToUpdate.sort(Comparator.comparing(Speaker::getId));
            logAndDumpSpeakers(speakersToUpdate, "Speakers (to update resource file): {}", "speakers-to-update.yml");
        }
    }

    /**
     * Saves talks.
     *
     * @param talkLoadResult talk load result
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void saveTalks(LoadResult<List<Talk>> talkLoadResult) throws IOException, NoSuchFieldException {
        List<Talk> talksToDelete = talkLoadResult.getItemToDelete();
        List<Talk> talksToAppend = talkLoadResult.getItemToAppend();
        List<Talk> talksToUpdate = talkLoadResult.getItemToUpdate();

        if (!talksToDelete.isEmpty()) {
            talksToDelete.sort(Comparator.comparing(Talk::getId));
            logAndDumpTalks(talksToDelete, "Talks (to delete in resource file): {}", "talks-to-delete.yml");
        }

        if (!talksToAppend.isEmpty()) {
            logAndDumpTalks(talksToAppend, "Talks (to append resource file): {}", "talks-to-append.yml");
        }

        if (!talksToUpdate.isEmpty()) {
            talksToUpdate.sort(Comparator.comparing(Talk::getId));
            logAndDumpTalks(talksToUpdate, "Talks (to update resource file): {}", "talks-to-update.yml");
        }
    }

    /**
     * Saves places.
     *
     * @param placeLoadResult place load result
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void savePlaces(LoadResult<Place> placeLoadResult) throws IOException, NoSuchFieldException {
        Place placeToAppend = placeLoadResult.getItemToAppend();
        Place placeToUpdate = placeLoadResult.getItemToUpdate();

        if (placeToAppend != null) {
            dumpPlace(placeToAppend, "place-to-append.yml");
        }

        if (placeToUpdate != null) {
            dumpPlace(placeToUpdate, "place-to-update.yml");
        }
    }

    /**
     * Saves event.
     *
     * @param eventLoadResult event load result
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void saveEvents(LoadResult<Event> eventLoadResult) throws IOException, NoSuchFieldException {
        Event eventToAppend = eventLoadResult.getItemToAppend();
        Event eventToUpdate = eventLoadResult.getItemToUpdate();

        if (eventToAppend != null) {
            dumpEvent(eventToAppend, "event-to-append.yml");
        }

        if (eventToUpdate != null) {
            dumpEvent(eventToUpdate, "event-to-update.yml");
        }
    }

    /**
     * Logs and dumps event types.
     *
     * @param eventTypes event types
     * @param logMessage log message
     * @param filename   filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void logAndDumpEventTypes(List<EventType> eventTypes, String logMessage, String filename) throws IOException, NoSuchFieldException {
        log.info(logMessage, eventTypes.size());
        eventTypes.forEach(
                et -> log.debug("Event type: id: {}, conference: {}, nameEn: {}, nameRu: {}",
                        et.getId(),
                        et.getConference(),
                        LocalizationUtils.getString(et.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(et.getName(), Language.RUSSIAN)
                )
        );

        YamlUtils.dump(new EventTypeList(eventTypes), filename);
    }

    /**
     * Logs and dumps companies.
     *
     * @param companies  companies
     * @param logMessage log message
     * @param filename   filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void logAndDumpCompanies(List<Company> companies, String logMessage, String filename) throws IOException, NoSuchFieldException {
        log.info(logMessage, companies.size());
        companies.forEach(
                c -> log.trace("Company: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(c.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(c.getName(), Language.RUSSIAN))
        );

        YamlUtils.dump(new CompanyList(companies), filename);
    }

    /**
     * Logs and creates speaker images.
     *
     * @param urlFilenames url, filenames pairs
     * @param logMessage   log message
     * @throws IOException if file creation error occurs
     */
    static void logAndCreateSpeakerImages(List<UrlFilename> urlFilenames, String logMessage) throws IOException {
        log.info(logMessage, urlFilenames.size());
        for (UrlFilename urlFilename : urlFilenames) {
            ImageUtils.create(urlFilename.getUrl(), urlFilename.getFilename());
        }
    }

    /**
     * Logs and dumps speakers.
     *
     * @param speakers   speakers
     * @param logMessage log message
     * @param filename   filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void logAndDumpSpeakers(List<Speaker> speakers, String logMessage, String filename) throws IOException, NoSuchFieldException {
        log.info(logMessage, speakers.size());
        speakers.forEach(
                s -> log.trace("Speaker: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(s.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(s.getName(), Language.RUSSIAN))
        );

        YamlUtils.dump(new SpeakerList(speakers), filename);
    }

    /**
     * Logs and dumps talks.
     *
     * @param talks      talks
     * @param logMessage log message
     * @param filename   filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void logAndDumpTalks(List<Talk> talks, String logMessage, String filename) throws IOException, NoSuchFieldException {
        log.info(logMessage, talks.size());
        talks.forEach(
                t -> log.trace("Talk: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(t.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(t.getName(), Language.RUSSIAN))
        );

        YamlUtils.dump(new TalkList(talks), filename);
    }

    /**
     * Dumps place to file.
     *
     * @param place    place
     * @param filename filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void dumpPlace(Place place, String filename) throws IOException, NoSuchFieldException {
        YamlUtils.dump(new PlaceList(Collections.singletonList(place)), filename);
    }

    /**
     * Dumps event to file.
     *
     * @param event    event
     * @param filename filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void dumpEvent(Event event, String filename) throws IOException, NoSuchFieldException {
        YamlUtils.dump(new EventList(Collections.singletonList(event)), filename);
    }

    /**
     * Finds resource company.
     *
     * @param company            company
     * @param resourceCompanyMap resource company map
     * @return resource company
     */
    static Company findResourceCompany(Company company, Map<String, Company> resourceCompanyMap) {
        for (LocaleItem localItem : company.getName()) {
            Company resourceCompany = resourceCompanyMap.get(localItem.getText().toLowerCase());

            if (resourceCompany != null) {
                return resourceCompany;
            }
        }

        return null;
    }

    /**
     * Finds resource speaker.
     *
     * @param speaker         speaker
     * @param speakerLoadMaps speaker load maps
     * @return resource speaker
     */
    static Speaker findResourceSpeaker(Speaker speaker, SpeakerLoadMaps speakerLoadMaps) {
        // Find in known speakers by (name, company) pair because
        // - speaker could change his/her last name (for example, woman got married);
        // - speaker (with non-unique pair of name, company) could change his/her company.
        Long resourceSpeakerId = null;
        Company speakerCompany = null;

        for (Company company : speaker.getCompanies()) {
            resourceSpeakerId = speakerLoadMaps.getKnownSpeakerIdsMap().get(
                    new NameCompany(
                            LocalizationUtils.getString(speaker.getName(), Language.RUSSIAN),
                            company));

            if (resourceSpeakerId != null) {
                speakerCompany = company;
                break;
            }
        }

        if (resourceSpeakerId != null) {
            Speaker resourceSpeaker = speakerLoadMaps.getResourceSpeakerIdsMap().get(resourceSpeakerId);

            Long finalResourceSpeakerId = resourceSpeakerId;
            Company finalSpeakerCompany = speakerCompany;

            return Objects.requireNonNull(resourceSpeaker,
                    () -> String.format("Resource speaker id %d not found (change id of known speaker '%s' and company '%s' in method parameters and rerun loading)",
                            finalResourceSpeakerId,
                            LocalizationUtils.getString(speaker.getName(), Language.RUSSIAN),
                            (finalSpeakerCompany != null) ? LocalizationUtils.getString(finalSpeakerCompany.getName(), Language.RUSSIAN) : null));
        }

        // Find in resource speakers by (name, company) pair
        Speaker resourceSpeaker = findResourceSpeakerByNameCompany(speaker, speakerLoadMaps.getResourceNameCompanySpeakers());
        if (resourceSpeaker != null) {
            return resourceSpeaker;
        }

        // Find in resource speakers by name
        return findResourceSpeakerByName(speaker, speakerLoadMaps.getResourceNameSpeakers());
    }

    static Talk findResourceTalk(Talk talk,
                                 Map<String, Set<Talk>> resourceRuNameTalks,
                                 Map<String, Set<Talk>> resourceEnNameTalks) {
        // Find in resource talks by Russian name
        Talk resourceTalk = findResourceTalkByName(talk, resourceRuNameTalks, Language.RUSSIAN);
        if (resourceTalk != null) {
            return resourceTalk;
        }

        // Find in resource talks by English name
        return findResourceTalkByName(talk, resourceEnNameTalks, Language.ENGLISH);
    }

    /**
     * Finds resource speaker by pair of name, company.
     *
     * @param speaker                     speaker
     * @param resourceNameCompanySpeakers map of (name, company)/speaker
     * @return resource speaker
     */
    static Speaker findResourceSpeakerByNameCompany(Speaker speaker, Map<NameCompany, Speaker> resourceNameCompanySpeakers) {
        Speaker result = null;

        for (LocaleItem localeItem : speaker.getName()) {
            for (Company company : speaker.getCompanies()) {
                result = resourceNameCompanySpeakers.get(
                        new NameCompany(
                                localeItem.getText(),
                                company));

                if (result != null) {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Finds resource speaker by name.
     *
     * @param speaker              speaker
     * @param resourceNameSpeakers map of name/speakers
     * @return resource speaker
     */
    static Speaker findResourceSpeakerByName(Speaker speaker, Map<String, Set<Speaker>> resourceNameSpeakers) {
        Set<Speaker> resourceSpeakers = null;
        String speakerName = null;

        for (LocaleItem localeItem : speaker.getName()) {
            speakerName = localeItem.getText();
            resourceSpeakers = resourceNameSpeakers.get(speakerName);

            if (resourceSpeakers != null) {
                break;
            }
        }

        if (resourceSpeakers != null) {
            if (resourceSpeakers.isEmpty()) {
                throw new IllegalStateException(String.format("No speakers found in set for speaker name '%s'", speakerName));
            } else if (resourceSpeakers.size() > 1) {
                log.warn("More than one speaker found by name '{}', new speaker will be created (may be necessary to add a known speaker to the method parameters and restart loading)", speakerName);

                return null;
            } else {
                Speaker resourceSpeaker = resourceSpeakers.iterator().next();
                String resourceSpeakerCompanies = resourceSpeaker.getCompanies().stream()
                        .map(c -> LocalizationUtils.getString(c.getName(), Language.RUSSIAN))
                        .collect(Collectors.joining(", "));
                String speakerCompanies = speaker.getCompanies().stream()
                        .map(c -> LocalizationUtils.getString(c.getName(), Language.RUSSIAN))
                        .collect(Collectors.joining(", "));

                log.warn("Speaker found only by name '{}', speaker company (in resource files): '{}', speaker company (in Contentful): '{}')",
                        speakerName, resourceSpeakerCompanies, speakerCompanies);

                return resourceSpeaker;
            }
        }

        return null;
    }

    /**
     * Finds resource talk by name.
     *
     * @param talk              talk
     * @param resourceNameTalks map of name/talks
     * @param language          language
     * @return resource talk
     */
    static Talk findResourceTalkByName(Talk talk, Map<String, Set<Talk>> resourceNameTalks, Language language) {
        String talkName = LocalizationUtils.getString(talk.getName(), language);
        Set<Talk> resourceTalks = resourceNameTalks.get(talkName);

        if (resourceTalks != null) {
            if (resourceTalks.isEmpty()) {
                throw new IllegalStateException(String.format("No talks found in set for talk name '%s'", talkName));
            } else if (resourceTalks.size() > 1) {
                log.warn("More than one talk found by name '{}', new talk will be created", talkName);

                return null;
            } else {
                return resourceTalks.iterator().next();
            }
        }

        return null;
    }

    /**
     * Finds resource place by pair of city, venue address.
     *
     * @param place                          place
     * @param resourceCityVenueAddressPlaces map of (city, venue address)/place
     * @param language                       language
     * @return resource place
     */
    static Place findResourcePlaceByCityVenueAddress(Place place,
                                                     Map<CityVenueAddress, Place> resourceCityVenueAddressPlaces,
                                                     Language language) {
        return resourceCityVenueAddressPlaces.get(
                new CityVenueAddress(
                        LocalizationUtils.getString(place.getCity(), language),
                        LocalizationUtils.getString(place.getVenueAddress(), language)));
    }

    /**
     * Finds resource place by pair of city, venue address.
     *
     * @param place                            place
     * @param resourceRuCityVenueAddressPlaces map of (city, venue address)/place in Russian
     * @param resourceEnCityVenueAddressPlaces map of (city, venue address)/place in English
     * @return resource place
     */
    static Place findResourcePlace(Place place,
                                   Map<CityVenueAddress, Place> resourceRuCityVenueAddressPlaces,
                                   Map<CityVenueAddress, Place> resourceEnCityVenueAddressPlaces) {
        // Find in resource places by Russian (city, venue address) pair
        Place resourcePlace = findResourcePlaceByCityVenueAddress(place, resourceRuCityVenueAddressPlaces, Language.RUSSIAN);
        if (resourcePlace != null) {
            return resourcePlace;
        }

        // Find in resource places by English (city, venue address) pair
        return findResourcePlaceByCityVenueAddress(place, resourceEnCityVenueAddressPlaces, Language.ENGLISH);
    }

    /**
     * Fixes place venue address.
     *
     * @param place place
     * @return fixed place
     */
    static List<LocaleItem> fixVenueAddress(Place place) {
        final String ONLINE_ENGLISH = "Online";
        final String ONLINE_RUSSIAN = "Онлайн";

        List<FixingVenueAddress> enFixingVenueAddresses = List.of(
                new FixingVenueAddress(
                        ONLINE_ENGLISH,
                        ONLINE_ENGLISH,
                        "")
        );
        List<FixingVenueAddress> ruFixingVenueAddresses = List.of(
                new FixingVenueAddress(
                        "Санкт-Петербург",
                        "пл. Победы, 1 , Гостиница «Park Inn by Radisson Пулковская»",
                        "пл. Победы, 1, Гостиница «Park Inn by Radisson Пулковская»"),
                new FixingVenueAddress(
                        "Москва",
                        "Международная ул., 16, Красногорск, Московская обл.,, МВЦ «Крокус Экспо»",
                        "Международная ул., 16, Красногорск, Московская обл., МВЦ «Крокус Экспо»"),
                new FixingVenueAddress(
                        ONLINE_RUSSIAN,
                        ONLINE_ENGLISH,
                        ""),
                new FixingVenueAddress(
                        ONLINE_RUSSIAN,
                        ONLINE_RUSSIAN,
                        "")
        );

        String enVenueAddress = getFixedVenueAddress(
                LocalizationUtils.getString(place.getCity(), Language.ENGLISH),
                LocalizationUtils.getString(place.getVenueAddress(), Language.ENGLISH),
                enFixingVenueAddresses);
        String ruVenueAddress = getFixedVenueAddress(
                LocalizationUtils.getString(place.getCity(), Language.RUSSIAN),
                LocalizationUtils.getString(place.getVenueAddress(), Language.RUSSIAN),
                ruFixingVenueAddresses);

        return ContentfulUtils.extractLocaleItems(enVenueAddress, ruVenueAddress, true);
    }

    /**
     * Gets place venue address.
     *
     * @param city                 city
     * @param venueAddress         original venue address
     * @param fixingVenueAddresses fixing venue addresses
     * @return resulting venue address
     */
    static String getFixedVenueAddress(String city, String venueAddress, List<FixingVenueAddress> fixingVenueAddresses) {
        for (FixingVenueAddress fixingVenueAddress : fixingVenueAddresses) {
            if (fixingVenueAddress.getCity().equals(city) &&
                    fixingVenueAddress.getInvalidVenueAddress().equals(venueAddress)) {
                return fixingVenueAddress.getValidVenueAddress();
            }
        }

        return venueAddress;
    }

    public static void main(String[] args) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
        // Uncomment one of lines and run

        // Load event types
//        loadEventTypes();

        // Load talks, speaker and event
        // 2016
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2016, 10, 14), "2016Joker",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Jean-Philippe BEMPEL", new Company(553, "Ullink")), 155L)));
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2016, 12, 7), "2016hel",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Alexander Thissen", new Company(601, "Xpirit")), 408L)));
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2016, 12, 9), "2016msk",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Alexander Thissen", new Company(601, "Xpirit")), 408L)));
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2016, 12, 10), "2016msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2016, 12, 11), "2016msk");

        // 2017
//        loadTalksSpeakersEvent(Conference.JBREAK, LocalDate.of(2017, 4, 4), "2017JBreak",
//                LoadSettings.invalidTalksSet(Set.of("Верхом на реактивных стримах")));
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2017, 4, 7), "2017JPoint",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Владимир Озеров", new Company(224, "GridGain Systems")), 28L)));
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2017, 4, 21), "2017spb");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2017, 5, 19), "2017spb",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Alexander Thissen", new Company(601, "Xpirit")), 408L)));
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2017, 6, 2), "2017spb");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2017, 6, 4), "2017spb");
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2017, 10, 20), "2017DevOops",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Ray Тsang", new Company(217, "Google")), 377L)));
//        loadTalksSpeakersEvent(Conference.SMART_DATA, LocalDate.of(2017, 10, 21), "2017smartdata");
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2017, 11, 3), "2017Joker");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2017, 11, 11), "2017msk",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Владимир Иванов", new Company(183, "EPAM Systems")), 852L)));
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2017, 11, 12), "2017msk",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Alexander Thissen", new Company(601, "Xpirit")), 408L)));
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2017, 12, 8), "2017msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2017, 12, 10), "2017msk");

        // 2018
//        loadTalksSpeakersEvent(Conference.JBREAK, LocalDate.of(2018, 3, 4), "2018JBreak",
//                LoadSettings.invalidTalksSet(Set.of("Верхом на реактивных стримах")));
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2018, 4, 6), "2018JPoint");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2018, 4, 20), "2018spb");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2018, 4, 22), "2018spb",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Alexander Thissen", new Company(601, "Xpirit")), 408L)));
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2018, 5, 17), "2018spb");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2018, 5, 19), "2018spb");
//        loadTalksSpeakersEvent(Conference.TECH_TRAIN, LocalDate.of(2018, 9, 1), "2018tt");
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2018, 10, 14), "2018DevOops");
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2018, 10, 19), "2018Joker",
//                LoadSettings.knownSpeakerIdsMap(Map.of(
//                        new NameCompany("Алексей Федоров", new Company(291, "JUG Ru Group")), 7L,
//                        new NameCompany("Павел Финкельштейн", new Company(302, "Lamoda")), 8L)));
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2018, 11, 22), "2018msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2018, 11, 24), "2018msk");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2018, 12, 6), "2018msk");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2018, 12, 8), "2018msk");

        // 2019
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2019, 4, 5), "2019jpoint",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Паша Финкельштейн", new Company(302, "Lamoda")), 8L)));
//        loadTalksSpeakersEvent(Conference.CPP_RUSSIA, LocalDate.of(2019, 4, 19), "2019cpp",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Павел Новиков", new Company(27, "Align Technology")), 351L)));
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2019, 5, 15), "2019spb",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Alexander Thissen", new Company(601, "Xpirit")), 408L)));
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2019, 5, 17), "2019spb");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2019, 5, 22), "2019spb");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2019, 5, 24), "2019spb");
//        loadTalksSpeakersEvent(Conference.SPTDC, LocalDate.of(2019, 7, 8), "2019sptdc");
//        loadTalksSpeakersEvent(Conference.HYDRA, LocalDate.of(2019, 7, 11), "2019hydra");
//        loadTalksSpeakersEvent(Conference.TECH_TRAIN, LocalDate.of(2019, 8, 24), "2019tt");
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2019, 10, 25), "2019joker");
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2019, 10, 29), "2019devoops");
//        loadTalksSpeakersEvent(Conference.CPP_RUSSIA, LocalDate.of(2019, 10, 31), "2019-spb-cpp");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2019, 11, 6), "2019msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2019, 11, 8), "2019msk",
//                LoadSettings.knownSpeakerIdsMap(Map.of(new NameCompany("Lucas Fernandes da Costa", new Company(112, "Converge")), 659L)));
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2019, 12, 5), "2019msk");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2019, 12, 7), "2019msk");

        // 2020
//        loadTalksSpeakersEvent(Conference.TECH_TRAIN, LocalDate.of(2020, 6, 6), "2020-spb-tt");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2020, 6, 15), "2020-spb");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2020, 6, 15), "2020-spb",
//                LoadSettings.invalidTalksSet(Set.of("Комбинаторный подход к тестированию распределенной системы", "Автотесты на страже качества IDE",
//                        "Тестирование безопасности для SQA", "Процесс тестирования производительности в геймдеве",
//                        "Производительность iOS-приложений и техники ее тестирования")));
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2020, 6, 22), "2020-spb");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2020, 6, 22), "2020-spb",
//                LoadSettings.invalidTalksSet(Set.of("Monorepo. Стоит ли игра свеч?", "ТестирUI правильно")));
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2020, 6, 29), "2020-jpoint");
//        loadTalksSpeakersEvent(Conference.CPP_RUSSIA, LocalDate.of(2020, 6, 29), "2020-msk-cpp",
//                LoadSettings.invalidTalksSet(Set.of("Сопрограммы в С++20. Прошлое, настоящее и будущее",
//                        "Use-After-Free Busters: C++ Garbage Collector in Chrome",
//                        "A standard audio API for C++",
//                        "Coroutine X-rays and other magical superpowers",
//                        "Компьютерные игры: Как загрузить все ядра CPU")));
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2020, 7, 6), "2020-msk-devoops",
//                LoadSettings.invalidTalksSet(Set.of("Title will be announced soon", "Context based access with Google’s BeyondCorp",
//                        "Применяем dogfooding: От Ops к Dev в Яндекс.Облако", "Kubernetes service discovery",
//                        "Event Gateways: Что? Зачем? Как?", "Continuously delivering infrastructure",
//                        "The lifecycle of a service", "Безопасность и Kubernetes",
//                        "Edge Computing: А trojan horse of DevOps tribe infiltrating the IoT industry")));
//        loadTalksSpeakersEvent(Conference.HYDRA, LocalDate.of(2020, 7, 6), "2020-msk-hydra",
//                new LoadSettings(
//                        Map.of(new NameCompany("Oleg Anastasyev", new Company(653, "OK.RU")), 124L),
//                        Set.of(
//                                "Reasoning about data consistency in distributed systems (part 1)",
//                                "Programming for persistent memory",
//                                "Programming for persistent memory (part 1)",
//                                "Theoretical and practical worlds of failure detectors",
//                                "Cryptographic tools for distributed computing (part 1)",
//                                "Algorand: A secure, scalable and decentralized blockchain"
//                        ),
//                        true));
//        loadTalksSpeakersEvent(Conference.SPTDC, LocalDate.of(2020, 7, 6), "2020-msk-sptdc",
//                LoadSettings.invalidTalksSet(Set.of("Doctoral workshop", "Title will be announced soon")));
//        loadTalksSpeakersEvent(Conference.TECH_TRAIN, LocalDate.of(2020, 10, 24), "2020techtrainautumn");
//        loadTalksSpeakersEvent(Conference.CPP_RUSSIA, LocalDate.of(2020, 11, 11), "2020spbcpp",
//                new LoadSettings(
//                        Collections.emptyMap(),
//                        Set.of("Lightning talks", "C++ Russia 2020 Piter Virtual Afterparty"),
//                        false));
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2020, 11, 4), "2020msk",
//                new LoadSettings(
//                        Collections.emptyMap(),
//                        Set.of("Heisenbug 2020 Moscow Virtual Afterparty"),
//                        false));
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2020, 11, 11), "2020msk",
//                new LoadSettings(
//                        Collections.emptyMap(),
//                        Set.of("Mobius 2020 Moscow Virtual Afterparty"),
//                        false));
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2020, 11, 25), "2020msk",
//                LoadSettings.invalidTalksSet(Set.of("HolyJS 2020 Virtual Afterparty")));
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2020, 11, 25), "2020joker",
//                LoadSettings.invalidTalksSet(Set.of("Joker 2020 Virtual Afterparty")));
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2020, 12, 2), "2020spbdevoops",
//                new LoadSettings(
//                        Collections.emptyMap(),
//                        Set.of("DevOops 2020 Virtual Afterparty"),
//                        false));
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2020, 12, 2), "2020msk",
//                new LoadSettings(
//                        Collections.emptyMap(),
//                        Set.of("DotNext 2020 Virtual Afterparty"),
//                        false));
//        loadTalksSpeakersEvent(Conference.SMART_DATA, LocalDate.of(2020, 12, 9), "2020spbsmartdata",
//                new LoadSettings(
//                        Collections.emptyMap(),
//                        Set.of("SmartData 2020 Virtual Afterparty"),
//                        false));
    }
}
