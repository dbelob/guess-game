package guess.util.yaml;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Language;
import guess.domain.source.*;
import guess.util.FileUtils;
import guess.util.LocalizationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * YAML utility methods.
 */
public class YamlUtils {
    private static final Logger log = LoggerFactory.getLogger(YamlUtils.class);

    private static final String DATA_DIRECTORY_NAME = "data";
    static final String OUTPUT_DIRECTORY_NAME = "output";

    private YamlUtils() {
    }

    /**
     * Reads source information from resource files.
     *
     * @return source information
     * @throws IOException                if resource files could not be opened
     * @throws SpeakerDuplicatedException if speaker duplicated
     */
    public static SourceInformation readSourceInformation() throws SpeakerDuplicatedException, IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource placesResource = resolver.getResource(String.format("classpath:%s/places.yml", DATA_DIRECTORY_NAME));
        Resource organizersResource = resolver.getResource(String.format("classpath:%s/organizers.yml", DATA_DIRECTORY_NAME));
        Resource eventTypesResource = resolver.getResource(String.format("classpath:%s/event-types.yml", DATA_DIRECTORY_NAME));
        Resource eventsResource = resolver.getResource(String.format("classpath:%s/events.yml", DATA_DIRECTORY_NAME));
        Resource companiesResource = resolver.getResource(String.format("classpath:%s/companies.yml", DATA_DIRECTORY_NAME));
        Resource companySynonymsResource = resolver.getResource(String.format("classpath:%s/company-synonyms.yml", DATA_DIRECTORY_NAME));
        Resource speakersResource = resolver.getResource(String.format("classpath:%s/speakers.yml", DATA_DIRECTORY_NAME));
        Resource talksResource = resolver.getResource(String.format("classpath:%s/talks.yml", DATA_DIRECTORY_NAME));

        Yaml placesYaml = new Yaml(new Constructor(PlaceList.class));
        Yaml organizerYaml = new Yaml(new Constructor(OrganizerList.class));
        Yaml eventTypesYaml = new Yaml(new Constructor(EventTypeList.class));
        Yaml eventsYaml = new Yaml(new DateTimeYamlConstructor(EventList.class));
        Yaml companiesYaml = new Yaml(new DateTimeYamlConstructor(CompanyList.class));
        Yaml companySynonymsYaml = new Yaml(new DateTimeYamlConstructor(CompanySynonymsList.class));
        Yaml speakersYaml = new Yaml(new DateTimeYamlConstructor(SpeakerList.class));
        Yaml talksYaml = new Yaml(new DateTimeYamlConstructor(TalkList.class));

        // Read from YAML files
        PlaceList placeList = placesYaml.load(placesResource.getInputStream());
        OrganizerList organizerList = organizerYaml.load(organizersResource.getInputStream());
        EventTypeList eventTypeList = eventTypesYaml.load(eventTypesResource.getInputStream());
        EventList eventList = eventsYaml.load(eventsResource.getInputStream());
        CompanyList companyList = companiesYaml.load(companiesResource.getInputStream());
        CompanySynonymsList companySynonymsList = companySynonymsYaml.load(companySynonymsResource.getInputStream());
        SpeakerList speakerList = speakersYaml.load(speakersResource.getInputStream());
        TalkList talkList = talksYaml.load(talksResource.getInputStream());

        return getSourceInformation(placeList, organizerList, eventTypeList, eventList, companyList, companySynonymsList, speakerList, talkList);
    }

    /**
     * Gets source information from resource lists.
     *
     * @param placeList           places
     * @param organizerList       organizers
     * @param eventTypeList       event types
     * @param eventList           events
     * @param companyList         companies
     * @param companySynonymsList company synonyms
     * @param speakerList         speakers
     * @param talkList            talks
     * @return source information
     * @throws SpeakerDuplicatedException if speaker duplicated
     */
    static SourceInformation getSourceInformation(PlaceList placeList, OrganizerList organizerList, EventTypeList eventTypeList,
                                                  EventList eventList, CompanyList companyList, CompanySynonymsList companySynonymsList,
                                                  SpeakerList speakerList, TalkList talkList)
            throws SpeakerDuplicatedException {
        Map<Long, Place> placeMap = listToMap(placeList.getPlaces(), Place::getId);
        Map<Long, Organizer> organizerMap = listToMap(organizerList.getOrganizers(), Organizer::getId);
        Map<Long, EventType> eventTypeMap = listToMap(eventTypeList.getEventTypes(), EventType::getId);
        Map<Long, Company> companyMap = listToMap(companyList.getCompanies(), Company::getId);
        Map<Long, Speaker> speakerMap = listToMap(speakerList.getSpeakers(), Speaker::getId);
        Map<Long, Talk> talkMap = listToMap(talkList.getTalks(), Talk::getId);

        // Set event identifiers
        setEventIds(eventList.getEvents());

        // Link entities
//        linkEventTypesToOrganizers(organizerMap, eventTypeList.getEventTypes());  //TODO: add
        linkEventsToEventTypes(eventTypeMap, eventList.getEvents());
        linkEventsToPlaces(placeMap, eventList.getEvents());
        linkTalksToEvents(talkMap, eventList.getEvents());
        linkSpeakersToCompanies(companyMap, speakerList.getSpeakers());
        linkSpeakersToTalks(speakerMap, talkList.getTalks());

        // Find duplicates for speaker names and for speaker names with company name
        if (findSpeakerDuplicates(speakerList.getSpeakers())) {
            throw new SpeakerDuplicatedException();
        }

        return new SourceInformation(
                placeList.getPlaces(),
//                organizerList.getOrganizers(),    //TODO: add
                eventTypeList.getEventTypes(),
                eventList.getEvents(),
                companyList.getCompanies(),
                companySynonymsList.getCompanySynonyms(),
                speakerList.getSpeakers(),
                talkList.getTalks());
    }

    /**
     * Sets identifiers for events.
     *
     * @param events events
     */
    private static void setEventIds(List<Event> events) {
        AtomicLong id = new AtomicLong(0);

        events.forEach(e -> e.setId(id.getAndIncrement()));
    }

    /**
     * Links speakers to talks
     *
     * @param speakers speakers
     * @param talks    talks
     */
    static void linkSpeakersToTalks(Map<Long, Speaker> speakers, List<Talk> talks) {
        for (Talk talk : talks) {
            if (talk.getSpeakerIds().isEmpty()) {
                throw new IllegalStateException(String.format("No speakers found for talk %s", talk.getName()));
            }

            // For any speakerId
            for (Long speakerId : talk.getSpeakerIds()) {
                // Find speaker by id
                Speaker speaker = speakers.get(speakerId);
                Objects.requireNonNull(speaker,
                        () -> String.format("Speaker id %d not found for talk %s", speakerId, talk.toString()));
                talk.getSpeakers().add(speaker);
            }
        }
    }

    /**
     * Links events to event types.
     *
     * @param eventTypes event types
     * @param events     events
     */
    static void linkEventsToEventTypes(Map<Long, EventType> eventTypes, List<Event> events) {
        for (Event event : events) {
            // Find event type by id
            EventType eventType = eventTypes.get(event.getEventTypeId());
            Objects.requireNonNull(eventType,
                    () -> String.format("EventType id %d not found for event %s", event.getEventTypeId(), event.toString()));
            eventType.getEvents().add(event);
            event.setEventType(eventType);
        }
    }

    /**
     * Links events to places.
     *
     * @param places places
     * @param events events
     */
    static void linkEventsToPlaces(Map<Long, Place> places, List<Event> events) {
        for (Event event : events) {
            // Find place by id
            Place place = places.get(event.getPlaceId());
            Objects.requireNonNull(place,
                    () -> String.format("Place id %d not found for event %s", event.getPlaceId(), event.toString()));
            event.setPlace(place);
        }
    }

    /**
     * Links talks to events.
     *
     * @param talks  talks
     * @param events events
     */
    static void linkTalksToEvents(Map<Long, Talk> talks, List<Event> events) {
        for (Event event : events) {
            // For any talkId
            for (Long talkId : event.getTalkIds()) {
                // Find talk by id
                Talk talk = talks.get(talkId);
                Objects.requireNonNull(talk,
                        () -> String.format("Talk id %d not found for event %s", talkId, event.toString()));
                event.getTalks().add(talk);
            }
        }
    }

    /**
     * Links speakers to companies.
     *
     * @param companies companies
     * @param speakers  speakers
     */
    static void linkSpeakersToCompanies(Map<Long, Company> companies, List<Speaker> speakers) {
        for (Speaker speaker : speakers) {
            List<Company> speakerCompanies = new ArrayList<>();

            // Find companies by id
            for (Long companyId : speaker.getCompanyIds()) {
                Company company = companies.get(companyId);
                Objects.requireNonNull(company,
                        () -> String.format("Company id %d not found for speaker %s", companyId, speaker.toString()));
                speakerCompanies.add(company);
            }

            speaker.setCompanies(speakerCompanies);
        }
    }

    /**
     * Converts list of entities into map, throwing the IllegalStateException in case duplicate entities are found.
     *
     * @param list         input list
     * @param keyExtractor map key extractor for given entity class
     * @param <K>          map key type
     * @param <T>          entity (map value) type
     * @return map of entities, or IllegalStateException if duplicate entities are found
     */
    static <K, T> Map<K, T> listToMap(List<T> list, Function<? super T, ? extends K> keyExtractor) {
        Map<K, T> map = list.stream()
                .distinct()
                .collect(Collectors.toMap(keyExtractor, s -> s));

        if (map.size() != list.size()) {
            throw new IllegalStateException("Entities with duplicate ids found");
        }

        return map;
    }

    /**
     * Finds speaker duplicates.
     *
     * @param speakers speakers
     * @return {@code true} if duplicates found, {@code false} otherwise
     */
    static boolean findSpeakerDuplicates(List<Speaker> speakers) {
        Set<Speaker> speakerDuplicates = new TreeSet<>(Comparator.comparingLong(Speaker::getId));

        for (Language language : Language.values()) {
            speakerDuplicates.addAll(LocalizationUtils.getSpeakerDuplicates(
                    speakers,
                    s -> LocalizationUtils.getString(s.getName(), language),
                    s -> s.getCompanies().isEmpty()));
        }

        if (!speakerDuplicates.isEmpty()) {
            log.error("{} speaker duplicates exist (add company to them): {}", speakerDuplicates.size(), speakerDuplicates);
            return true;
        }

        for (Language language : Language.values()) {
            speakerDuplicates.addAll(LocalizationUtils.getSpeakerDuplicates(
                    speakers,
                    s -> LocalizationUtils.getSpeakerNameWithCompanies(s, language),
                    s -> true));
        }

        if (!speakerDuplicates.isEmpty()) {
            log.error("{} speaker duplicates exist (change company in them): {}", speakerDuplicates.size(), speakerDuplicates);
            return true;
        }

        return false;
    }

    /**
     * Deletes all files in output directory.
     *
     * @throws IOException if file iteration occurs
     */
    public static void clearDumpDirectory() throws IOException {
        FileUtils.deleteDirectory(OUTPUT_DIRECTORY_NAME);
    }

    /**
     * Dumps items to file.
     *
     * @param items    items
     * @param filename filename
     * @throws IOException          if deletion error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    public static <T> void dump(T items, String filename) throws IOException, NoSuchFieldException {
        File file = new File(String.format("%s/%s", OUTPUT_DIRECTORY_NAME, filename));
        FileUtils.checkAndCreateDirectory(file.getParentFile());

        try (FileWriter writer = new FileWriter(file)) {
            DumperOptions options = new DumperOptions();
            options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
            options.setIndent(4);
            options.setIndicatorIndent(2);
            options.setWidth(120);

            List<PropertyMatcher> propertyMatchers = List.of(
                    new PropertyMatcher(EventType.class,
                            List.of("id", "conference", "logoFileName", "name", "shortDescription", "longDescription",
                                    "siteLink", "vkLink", "twitterLink", "facebookLink", "youtubeLink", "telegramLink")),
                    new PropertyMatcher(Place.class,
                            List.of("id", "city", "venueAddress", "mapCoordinates")),
                    new PropertyMatcher(Event.class,
                            List.of("eventTypeId", "name", "startDate", "endDate", "siteLink", "youtubeLink", "placeId",
                                    "talkIds")),
                    new PropertyMatcher(Talk.class,
                            List.of("id", "name", "shortDescription", "longDescription", "talkDay", "trackTime", "track",
                                    "language", "presentationLinks", "videoLinks", "speakerIds")),
                    new PropertyMatcher(Company.class,
                            List.of("id", "name")),
                    new PropertyMatcher(Speaker.class,
                            List.of("id", "photoFileName", "photoUpdatedAt", "name", "companyIds", "bio", "twitter",
                                    "gitHub", "javaChampion", "mvp", "mvpReconnect")),
                    new PropertyMatcher(LocaleItem.class,
                            List.of("language", "text"))
            );
            CustomRepresenter representer = new CustomRepresenter(propertyMatchers);
            representer.addClassTag(items.getClass(), Tag.MAP);

            CustomYaml eventTypesYaml = new CustomYaml(
                    new Constructor(items.getClass()),
                    representer,
                    options);
            eventTypesYaml.dump(items, writer);
        }

        log.info("File '{}' saved", file.getAbsolutePath());
    }
}
