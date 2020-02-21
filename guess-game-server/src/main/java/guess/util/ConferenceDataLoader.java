package guess.util;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.NameCompany;
import guess.domain.source.image.UrlFilename;
import guess.domain.source.*;
import guess.util.yaml.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
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
     * @throws NoSuchFieldException       if field name is invalid
     */
    private static void loadEventTypes() throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
        // Read event types from resource files
        SourceInformation resourceSourceInformation = YamlUtils.readSourceInformation();
        List<EventType> resourceEventTypes = resourceSourceInformation.getEventTypes().stream()
                .filter(et -> et.getConference() != null)
                .collect(Collectors.toList());
        log.info("Event types (in resource files): {}", resourceEventTypes.size());

        // Read event types from Contentful
        List<EventType> contentfulEventTypes = ContentfulUtils.getEventTypes().stream()
                .filter(et -> et.getConference() != null)
                .collect(Collectors.toList());
        log.info("Event types (in Contentful): {}", contentfulEventTypes.size());

        // Find event types
        Map<Conference, EventType> resourceEventTypeMap = resourceEventTypes.stream()
                .collect(Collectors.toMap(
                        EventType::getConference,
                        et -> et));
        AtomicLong id = new AtomicLong(
                resourceSourceInformation.getEventTypes().stream()
                        .map(EventType::getId)
                        .max(Long::compare)
                        .orElse(-1L));
        List<EventType> eventTypesToAppend = new ArrayList<>();
        List<EventType> eventTypesToUpdate = new ArrayList<>();

        contentfulEventTypes.forEach(
                et -> {
                    EventType resourceEventType = resourceEventTypeMap.get(et.getConference());

                    if (resourceEventType == null) {
                        // Event type not exists
                        et.setId(id.incrementAndGet());

                        eventTypesToAppend.add(et);
                    } else {
                        // Event type exists
                        et.setId(resourceEventType.getId());
                        et.setLogoFileName(resourceEventType.getLogoFileName());

                        if (ContentfulUtils.needUpdate(resourceEventType, et)) {
                            // Event type need to update
                            eventTypesToUpdate.add(et);
                        }
                    }
                }
        );

        // Save files
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
     * @param conference         conference
     * @param startDate          start date
     * @param conferenceCode     conference code
     * @param knownSpeakerIdsMap (name, company)/(speaker id) map for known speakers
     * @throws IOException                if resource files could not be opened
     * @throws SpeakerDuplicatedException if speakers duplicated
     * @throws NoSuchFieldException       if field name is invalid
     */
    private static void loadTalksSpeakersEvent(Conference conference, LocalDate startDate, String conferenceCode,
                                               Map<NameCompany, Long> knownSpeakerIdsMap) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
        log.info("{} {} {}", conference, startDate, conferenceCode);

        // Read event types, events, speakers, talks from resource files
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
        List<Talk> contentfulTalks = ContentfulUtils.getTalks(conference, conferenceCode);
        log.info("Talks (in Contentful): {}", contentfulTalks.size());
        contentfulTalks.forEach(
                t -> log.trace("Talk: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(t.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(t.getName(), Language.RUSSIAN))
        );

        // Order speakers with talk order
        List<Speaker> contentfulSpeakers = contentfulTalks.stream()
                .flatMap(t -> t.getSpeakers().stream())
                .distinct()
                .collect(Collectors.toList());
        log.info("Speakers (in Contentful): {}", contentfulSpeakers.size());
        contentfulSpeakers.forEach(
                s -> log.trace("Speaker: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(s.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(s.getName(), Language.RUSSIAN))
        );

        // Find speakers
        Map<Long, Speaker> resourceSpeakerIdsMap = resourceSourceInformation.getSpeakers().stream()
                .collect(Collectors.toMap(
                        Speaker::getId,
                        s -> s));
        Map<NameCompany, Speaker> resourceRuNameCompanySpeakers = resourceSourceInformation.getSpeakers().stream()
                .collect(Collectors.toMap(
                        s -> new NameCompany(
                                LocalizationUtils.getString(s.getName(), Language.RUSSIAN).trim(),
                                LocalizationUtils.getString(s.getCompany(), Language.RUSSIAN).trim()),
                        s -> s
                ));
        Map<NameCompany, Speaker> resourceEnNameCompanySpeakers = resourceSourceInformation.getSpeakers().stream()
                .collect(Collectors.toMap(
                        s -> new NameCompany(
                                LocalizationUtils.getString(s.getName(), Language.ENGLISH).trim(),
                                LocalizationUtils.getString(s.getCompany(), Language.ENGLISH).trim()),
                        s -> s
                ));
        Map<String, Set<Speaker>> resourceRuNameSpeakers = resourceSourceInformation.getSpeakers().stream()
                .collect(Collectors.groupingBy(
                        s -> LocalizationUtils.getString(s.getName(), Language.RUSSIAN).trim(),
                        Collectors.toSet()
                ));
        Map<String, Set<Speaker>> resourceEnNameSpeakers = resourceSourceInformation.getSpeakers().stream()
                .collect(Collectors.groupingBy(
                        s -> LocalizationUtils.getString(s.getName(), Language.ENGLISH).trim(),
                        Collectors.toSet()
                ));
        AtomicLong speakerId = new AtomicLong(
                resourceSourceInformation.getSpeakers().stream()
                        .map(Speaker::getId)
                        .max(Long::compare)
                        .orElse(-1L));
        List<Speaker> speakersToAppend = new ArrayList<>();
        List<Speaker> speakersToUpdate = new ArrayList<>();
        List<UrlFilename> urlFilenamesToAppend = new ArrayList<>();
        List<UrlFilename> urlFilenamesToUpdate = new ArrayList<>();

        for (Speaker s : contentfulSpeakers) {
            Speaker resourceSpeaker = findResourceSpeaker(s, knownSpeakerIdsMap, resourceSpeakerIdsMap,
                    resourceRuNameCompanySpeakers, resourceEnNameCompanySpeakers,
                    resourceRuNameSpeakers, resourceEnNameSpeakers);

            if (resourceSpeaker == null) {
                // Speaker not exists
                long id = speakerId.incrementAndGet();
                String sourceUrl = s.getFileName();
                String destinationFileName = String.format("%04d.jpg", id);

                s.setId(id);

                urlFilenamesToAppend.add(new UrlFilename(sourceUrl, destinationFileName));
                s.setFileName(destinationFileName);

                speakersToAppend.add(s);
            } else {
                // Speaker exists
                s.setId(resourceSpeaker.getId());
                String sourceUrl = s.getFileName();
                String destinationFileName = resourceSpeaker.getFileName();
                s.setFileName(destinationFileName);

                if ((resourceSpeaker.getTwitter() != null) && !resourceSpeaker.getTwitter().isEmpty() &&
                        ((s.getTwitter() == null) || s.getTwitter().isEmpty())) {
                    s.setTwitter(resourceSpeaker.getTwitter());
                }

                if ((resourceSpeaker.getGitHub() != null) && !resourceSpeaker.getGitHub().isEmpty() &&
                        ((s.getGitHub() == null) || s.getGitHub().isEmpty())) {
                    s.setGitHub(resourceSpeaker.getGitHub());
                }

                if (resourceSpeaker.isJavaChampion() && !s.isJavaChampion()) {
                    s.setJavaChampion(true);
                }

                if (resourceSpeaker.isMvp() && !s.isMvp()) {
                    s.setMvp(true);
                }

                if (ImageUtils.needUpdate(sourceUrl, destinationFileName)) {
                    urlFilenamesToUpdate.add(new UrlFilename(sourceUrl, destinationFileName));
                }

                if (ContentfulUtils.needUpdate(resourceSpeaker, s)) {
                    speakersToUpdate.add(s);
                }
            }
        }

        // Find talks
        contentfulTalks.forEach(
                t -> t.setSpeakerIds(t.getSpeakers().stream()
                        .map(Speaker::getId)
                        .collect(Collectors.toList())
                )
        );
        List<Talk> talksToDelete = new ArrayList<>();
        List<Talk> talksToAppend = new ArrayList<>();
        List<Talk> talksToUpdate = new ArrayList<>();
        AtomicLong talksId = new AtomicLong(
                resourceSourceInformation.getTalks().stream()
                        .map(Talk::getId)
                        .max(Long::compare)
                        .orElse(-1L));

        if (resourceEvent == null) {
            // Event not exists
            contentfulTalks.forEach(
                    t -> {
                        t.setId(talksId.incrementAndGet());
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
            contentfulTalks.forEach(
                    t -> {
                        Talk resourceTalk = findResourceTalk(t, resourceRuNameTalks, resourceEnNameTalks);

                        if (resourceTalk == null) {
                            // Talk not exists
                            t.setId(talksId.incrementAndGet());
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
                    .filter(dt -> {
                        if (contentfulTalks.contains(dt)) {
                            return false;
                        } else {
                            boolean talkExistsInAnyOtherEvent = resourceSourceInformation.getEvents().stream()
                                    .filter(e -> !e.equals(resourceEvent))
                                    .flatMap(e -> e.getTalks().stream())
                                    .anyMatch(rt -> rt.equals(dt));

                            if (talkExistsInAnyOtherEvent) {
                                log.warn("Deleting '{}' talk exists in other events and can't be deleted",
                                        LocalizationUtils.getString(dt.getName(), Language.ENGLISH));
                            }

                            return !talkExistsInAnyOtherEvent;
                        }
                    })
                    .collect(Collectors.toList());
        }

        // Find event
        contentfulEvent.setEventType(resourceEventType);
        contentfulEvent.setEventTypeId(resourceEventType.getId());
        contentfulEvent.setTalks(contentfulTalks);
        contentfulEvent.setTalkIds(contentfulTalks.stream()
                .map(Talk::getId)
                .collect(Collectors.toList()));
        Event eventToAppend = null;
        Event eventToUpdate = null;

        if (resourceEvent == null) {
            eventToAppend = contentfulEvent;
        } else {
            if (ContentfulUtils.needUpdate(resourceEvent, contentfulEvent)) {
                eventToUpdate = contentfulEvent;
            }
        }

        // Save files
        if (urlFilenamesToAppend.isEmpty() && urlFilenamesToUpdate.isEmpty() &&
                speakersToAppend.isEmpty() && speakersToUpdate.isEmpty() &&
                talksToDelete.isEmpty() && talksToAppend.isEmpty() && talksToUpdate.isEmpty() &&
                (eventToAppend == null) && (eventToUpdate == null)) {
            log.info("All speakers, talks and event are up-to-date");
        } else {
            YamlUtils.clearDumpDirectory();

            if (!urlFilenamesToAppend.isEmpty()) {
                logAndCreateSpeakerImages(urlFilenamesToAppend, "Speaker images (to append): {}");
            }

            if (!urlFilenamesToUpdate.isEmpty()) {
                logAndCreateSpeakerImages(urlFilenamesToUpdate, "Speaker images (to update): {}");
            }

            if (!speakersToAppend.isEmpty()) {
                logAndDumpSpeakers(speakersToAppend, "Speakers (to append resource file): {}", "speakers-to-append.yml");
            }

            if (!speakersToUpdate.isEmpty()) {
                speakersToUpdate.sort(Comparator.comparing(Speaker::getId));
                logAndDumpSpeakers(speakersToUpdate, "Speakers (to update resource file): {}", "speakers-to-update.yml");
            }

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

            if (eventToAppend != null) {
                YamlUtils.dumpEvent(eventToAppend, "event-to-append.yml");
            }

            if (eventToUpdate != null) {
                YamlUtils.dumpEvent(eventToUpdate, "event-to-update.yml");
            }
        }
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
    private static void loadTalksSpeakersEvent(Conference conference, LocalDate startDate, String conferenceCode) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
        loadTalksSpeakersEvent(conference, startDate, conferenceCode, Collections.emptyMap());
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
    private static void logAndDumpEventTypes(List<EventType> eventTypes, String logMessage, String filename) throws IOException, NoSuchFieldException {
        log.info(logMessage, eventTypes.size());
        eventTypes.forEach(
                et -> log.debug("Event type: id: {}, conference: {}, nameEn: {}, nameRu: {}",
                        et.getId(),
                        et.getConference(),
                        LocalizationUtils.getString(et.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(et.getName(), Language.RUSSIAN)
                )
        );

        YamlUtils.dumpEventTypes(eventTypes, filename);
    }

    /**
     * Logs and creates speaker images.
     *
     * @param urlFilenames url, filenames pairs
     * @param logMessage   log message
     * @throws IOException if file creation error occurs
     */
    private static void logAndCreateSpeakerImages(List<UrlFilename> urlFilenames, String logMessage) throws IOException {
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
    private static void logAndDumpSpeakers(List<Speaker> speakers, String logMessage, String filename) throws IOException, NoSuchFieldException {
        log.info(logMessage, speakers.size());
        speakers.forEach(
                s -> log.trace("Speaker: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(s.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(s.getName(), Language.RUSSIAN))
        );

        YamlUtils.dumpSpeakers(speakers, filename);

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
    private static void logAndDumpTalks(List<Talk> talks, String logMessage, String filename) throws IOException, NoSuchFieldException {
        log.info(logMessage, talks.size());
        talks.forEach(
                t -> log.trace("Talk: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(t.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(t.getName(), Language.RUSSIAN))
        );

        YamlUtils.dumpTalks(talks, filename);
    }

    private static Speaker findResourceSpeaker(Speaker speaker, Map<NameCompany, Long> knownSpeakerIdsMap,
                                               Map<Long, Speaker> resourceSpeakerIdsMap,
                                               Map<NameCompany, Speaker> resourceRuNameCompanySpeakers,
                                               Map<NameCompany, Speaker> resourceEnNameCompanySpeakers,
                                               Map<String, Set<Speaker>> resourceRuNameSpeakers,
                                               Map<String, Set<Speaker>> resourceEnNameSpeakers) {
        // Find in known speakers by (name, company) pair because
        // - speaker could change his/her last name (for example, woman got married);
        // - speaker (with non-unique pair of name, company) could change his/her company.
        Long resourceSpeakerId = knownSpeakerIdsMap.get(
                new NameCompany(
                        LocalizationUtils.getString(speaker.getName(), Language.RUSSIAN),
                        LocalizationUtils.getString(speaker.getCompany(), Language.RUSSIAN)));
        if (resourceSpeakerId != null) {
            Speaker resourceSpeaker = resourceSpeakerIdsMap.get(resourceSpeakerId);

            return Objects.requireNonNull(resourceSpeaker,
                    () -> String.format("Resource speaker id %d not found (change id of known speaker '%s' and company '%s' in method parameters and rerun loading)",
                            resourceSpeakerId,
                            LocalizationUtils.getString(speaker.getName(), Language.RUSSIAN),
                            LocalizationUtils.getString(speaker.getCompany(), Language.RUSSIAN)));
        }

        // Find in resource speakers by Russian (name, company) pair
        Speaker resourceSpeaker = findResourceSpeakerByNameCompany(speaker, resourceRuNameCompanySpeakers, Language.RUSSIAN);
        if (resourceSpeaker != null) {
            return resourceSpeaker;
        }

        // Find in resource speakers by English (name, company) pair
        resourceSpeaker = findResourceSpeakerByNameCompany(speaker, resourceEnNameCompanySpeakers, Language.ENGLISH);
        if (resourceSpeaker != null) {
            return resourceSpeaker;
        }

        // Find in resource speakers by Russian name
        resourceSpeaker = findResourceSpeakerByName(speaker, resourceRuNameSpeakers, Language.RUSSIAN);
        if (resourceSpeaker != null) {
            return resourceSpeaker;
        }

        // Find in resource speakers by English name
        return findResourceSpeakerByName(speaker, resourceEnNameSpeakers, Language.ENGLISH);
    }

    private static Talk findResourceTalk(Talk talk,
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
     * @param language                    language
     * @return resource speaker
     */
    private static Speaker findResourceSpeakerByNameCompany(Speaker speaker, Map<NameCompany, Speaker> resourceNameCompanySpeakers, Language language) {
        return resourceNameCompanySpeakers.get(
                new NameCompany(
                        LocalizationUtils.getString(speaker.getName(), language),
                        LocalizationUtils.getString(speaker.getCompany(), language)));
    }

    /**
     * Finds resource speaker by name.
     *
     * @param speaker              speaker
     * @param resourceNameSpeakers map of name/speakers
     * @param language             language
     * @return resource speaker
     */
    private static Speaker findResourceSpeakerByName(Speaker speaker, Map<String, Set<Speaker>> resourceNameSpeakers, Language language) {
        String speakerName = LocalizationUtils.getString(speaker.getName(), language);
        Set<Speaker> resourceSpeakers = resourceNameSpeakers.get(speakerName);

        if (resourceSpeakers != null) {
            if (resourceSpeakers.size() == 0) {
                throw new IllegalStateException(String.format("No speakers found in set for speaker name '%s'", speakerName));
            } else if (resourceSpeakers.size() > 1) {
                log.warn("More than one speaker found by name '{}', new speaker will be created (may be necessary to add a known speaker to the method parameters and restart loading)", speakerName);

                return null;
            } else {
                Speaker resourceSpeaker = resourceSpeakers.iterator().next();

                log.warn("Speaker found only by name '{}', speaker company (in resource files): '{}', speaker company (in Contentful): '{}')",
                        speakerName,
                        LocalizationUtils.getString(resourceSpeaker.getCompany(), language),
                        LocalizationUtils.getString(speaker.getCompany(), language));

                return resourceSpeaker;
            }
        }

        return null;
    }

    private static Talk findResourceTalkByName(Talk talk, Map<String, Set<Talk>> resourceNameTalks, Language language) {
        String talkName = LocalizationUtils.getString(talk.getName(), language);
        Set<Talk> resourceTalks = resourceNameTalks.get(talkName);

        if (resourceTalks != null) {
            if (resourceTalks.size() == 0) {
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

    public static void main(String[] args) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
        // Uncomment one of lines and run

        // Load event types
//        loadEventTypes();

        // Load talks, speaker and event
        // 2016
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2016, 10, 14), "2016Joker",
//                Map.of(new NameCompany("Jean-Philippe BEMPEL", "Ullink"), 155L));
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2016, 12, 7), "2016hel");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2016, 12, 9), "2016msk");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2016, 12, 10), "2016msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2016, 12, 11), "2016msk");

        // 2017
//        loadTalksSpeakersEvent(Conference.JBREAK, LocalDate.of(2017, 4, 4), "2017JBreak");
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2017, 4, 7), "2017JPoint",
//                Map.of(new NameCompany("Владимир Озеров", "GridGain Systems"), 28L));
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2017, 4, 21), "2017spb");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2017, 5, 19), "2017spb");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2017, 6, 2), "2017spb");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2017, 6, 4), "2017spb");
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2017, 10, 20), "2017DevOops");
//        loadTalksSpeakersEvent(Conference.SMART_DATA, LocalDate.of(2017, 10, 21), "2017smartdata");
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2017, 11, 3), "2017Joker");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2017, 11, 11), "2017msk",
//                Map.of(new NameCompany("Владимир Иванов", "EPAM Systems"), 852L));
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2017, 11, 12), "2017msk");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2017, 12, 8), "2017msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2017, 12, 10), "2017msk");

        // 2018
//        loadTalksSpeakersEvent(Conference.JBREAK, LocalDate.of(2018, 3, 4), "2018JBreak");
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2018, 4, 6), "2018JPoint");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2018, 4, 20), "2018spb");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2018, 4, 22), "2018spb");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2018, 5, 17), "2018spb");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2018, 5, 19), "2018spb");
//        loadTalksSpeakersEvent(Conference.TECH_TRAIN, LocalDate.of(2018, 9, 1), "2018tt");
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2018, 10, 14), "2018DevOops");
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2018, 10, 19), "2018Joker");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2018, 11, 22), "2018msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2018, 11, 24), "2018msk");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2018, 12, 6), "2018msk");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2018, 12, 8), "2018msk");

        // 2019
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2019, 4, 5), "2019jpoint");
//        loadTalksSpeakersEvent(Conference.CPP_RUSSIA, LocalDate.of(2019, 4, 19), "2019cpp",
//                Map.of(new NameCompany("Павел Новиков", "Align Technology"), 351L));
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2019, 5, 15), "2019spb");
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
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2019, 11, 8), "2019msk");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2019, 12, 5), "2019msk");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2019, 12, 7), "2019msk");

        // 2020
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2020, 4, 6), "2020-spb");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2020, 4, 8), "2020-spb");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2020, 4, 10), "2020-spb");
//        loadTalksSpeakersEvent(Conference.CPP_RUSSIA, LocalDate.of(2020, 4, 27), "2020-msk-cpp");
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2020, 4, 29), "2020-msk-devoops");
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2020, 5, 15), "2020-jpoint");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2020, 6, 23), "2020-spb");
//        loadTalksSpeakersEvent(Conference.SPTDC, LocalDate.of(2020, 7, 8), "2020-spb-sptdc");         // valid date?
//        loadTalksSpeakersEvent(Conference.HYDRA, LocalDate.of(2020, 7, 11), "2020-spb-hydra");        // valid date?
//        loadTalksSpeakersEvent(Conference.TECH_TRAIN, LocalDate.of(2020, 8, 24), "2020-techtrain");    // valid date?
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2020, 10, 7), "2020-spb-devoops");
//        loadTalksSpeakersEvent(Conference.CPP_RUSSIA, LocalDate.of(2020, 10, 9), "2020-spb-cpp");
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2020, 10, 23), "2020-joker");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2020, 11, 3), "2020-msk");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2020, 11, 5), "2020-msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2020, 11, 8), "2020-msk");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2020, 12, 7), "2020-msk");             // valid date?
    }
}
