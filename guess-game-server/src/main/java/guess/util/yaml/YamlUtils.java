package guess.util.yaml;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Language;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;
import guess.domain.source.*;
import guess.util.FileUtils;
import guess.util.LocalizationUtils;
import guess.util.QuestionUtils;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * YAML utility methods.
 */
public class YamlUtils {
    private static final Logger log = LoggerFactory.getLogger(YamlUtils.class);

    private static final String DESCRIPTIONS_DIRECTORY_NAME = "descriptions";
    private static final String OUTPUT_DIRECTORY_NAME = "output";

    /**
     * Reads source information from resource files.
     *
     * @return source information
     * @throws IOException                if resource files could not be opened
     * @throws SpeakerDuplicatedException if speaker duplicated
     */
    public static SourceInformation readSourceInformation() throws SpeakerDuplicatedException, IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource speakersResource = resolver.getResource(String.format("classpath:%s/speakers.yml", DESCRIPTIONS_DIRECTORY_NAME));
        Resource talksResource = resolver.getResource(String.format("classpath:%s/talks.yml", DESCRIPTIONS_DIRECTORY_NAME));
        Resource eventTypesResource = resolver.getResource(String.format("classpath:%s/event-types.yml", DESCRIPTIONS_DIRECTORY_NAME));
        Resource placesResource = resolver.getResource(String.format("classpath:%s/places.yml", DESCRIPTIONS_DIRECTORY_NAME));
        Resource eventsResource = resolver.getResource(String.format("classpath:%s/events.yml", DESCRIPTIONS_DIRECTORY_NAME));

        Yaml speakersYaml = new Yaml(new Constructor(Speakers.class));
        Yaml talksYaml = new Yaml(new LocalDateLocalTimeYamlConstructor(Talks.class));
        Yaml eventTypesYaml = new Yaml(new Constructor(EventTypes.class));
        Yaml placesYaml = new Yaml(new Constructor(Places.class));
        Yaml eventsYaml = new Yaml(new LocalDateLocalTimeYamlConstructor(Events.class));

        // Read descriptions from YAML files
        Speakers speakers = speakersYaml.load(speakersResource.getInputStream());
        Map<Long, Speaker> speakerMap = listToMap(speakers.getSpeakers(), Speaker::getId);

        Talks talks = talksYaml.load(talksResource.getInputStream());
        Map<Long, Talk> talkMap = listToMap(talks.getTalks(), Talk::getId);

        EventTypes eventTypes = eventTypesYaml.load(eventTypesResource.getInputStream());
        Map<Long, EventType> eventTypeMap = listToMap(eventTypes.getEventTypes(), EventType::getId);

        Places places = placesYaml.load(placesResource.getInputStream());
        Map<Long, Place> placeMap = listToMap(places.getPlaces(), Place::getId);

        Events events = eventsYaml.load(eventsResource.getInputStream());

        // Find duplicates for speaker names and for speaker names with company name
        if (findSpeakerDuplicates(speakers.getSpeakers())) {
            throw new SpeakerDuplicatedException();
        }

        // Link entities
        linkSpeakersToTalks(speakerMap, talks.getTalks());
        linkEventsToEventTypes(eventTypeMap, events.getEvents());
        linkEventsToPlaces(placeMap, events.getEvents());
        linkTalksToEvents(talkMap, events.getEvents());

        return new SourceInformation(
                eventTypes.getEventTypes(),
                places.getPlaces(),
                events.getEvents(),
                speakers.getSpeakers(),
                talks.getTalks());
    }

    /**
     * Reads question sets from resource files.
     *
     * @return question sets
     * @throws IOException                if an I/O error occurs
     * @throws SpeakerDuplicatedException if speaker duplicated
     */
    public static List<QuestionSet> readQuestionSets() throws IOException, SpeakerDuplicatedException {
        SourceInformation sourceInformation = readSourceInformation();

        // Create question sets
        List<QuestionSet> questionSets = new ArrayList<>();
        for (EventType eventType : sourceInformation.getEventTypes()) {
            // Fill speaker and talk questions
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();
            List<TalkQuestion> talkQuestions = new ArrayList<>();

            for (Event event : eventType.getEvents()) {
                for (Talk talk : event.getTalks()) {
                    for (Speaker speaker : talk.getSpeakers()) {
                        speakerQuestions.add(new SpeakerQuestion(speaker));
                    }

                    talkQuestions.add(new TalkQuestion(
                            talk.getSpeakers(),
                            talk));
                }
            }

            questionSets.add(new QuestionSet(
                    eventType.getId(),
                    createEventTypeNameWithPrefix(eventType),
                    eventType.getLogoFileName(),
                    QuestionUtils.removeDuplicatesById(speakerQuestions),
                    QuestionUtils.removeDuplicatesById(talkQuestions)));
        }

        return questionSets;
    }

    /**
     * Creates name with prefix.
     *
     * @param eventType event type
     * @return name with prefix
     */
    private static List<LocaleItem> createEventTypeNameWithPrefix(EventType eventType) {
        final String CONFERENCES_EVENT_TYPE_PREFIX = "conferencesEventTypePrefix";
        final String MEETUPS_EVENT_TYPE_PREFIX = "meetupsEventTypePrefix";

        List<LocaleItem> localeItems = new ArrayList<>();
        String resourceKey = (eventType.getConference() != null) ? CONFERENCES_EVENT_TYPE_PREFIX : MEETUPS_EVENT_TYPE_PREFIX;
        String enText = LocalizationUtils.getString(eventType.getName(), Language.ENGLISH);
        String ruText = LocalizationUtils.getString(eventType.getName(), Language.RUSSIAN);

        if ((enText != null) && !enText.isEmpty()) {
            localeItems.add(new LocaleItem(
                    Language.ENGLISH.getCode(),
                    String.format(LocalizationUtils.getResourceString(resourceKey, Language.ENGLISH), enText)));
        }

        if ((ruText != null) && !ruText.isEmpty()) {
            localeItems.add(new LocaleItem(
                    Language.RUSSIAN.getCode(),
                    String.format(LocalizationUtils.getResourceString(resourceKey, Language.RUSSIAN), ruText)));
        }

        return localeItems;
    }

    /**
     * Reads events from resource files.
     *
     * @return events
     * @throws IOException if an I/O error occurs
     */
    public static List<Event> readEvents() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource eventTypesResource = resolver.getResource(String.format("classpath:%s/event-types.yml", DESCRIPTIONS_DIRECTORY_NAME));
        Resource placesResource = resolver.getResource(String.format("classpath:%s/places.yml", DESCRIPTIONS_DIRECTORY_NAME));
        Resource eventsResource = resolver.getResource(String.format("classpath:%s/events.yml", DESCRIPTIONS_DIRECTORY_NAME));

        Yaml eventTypesYaml = new Yaml(new Constructor(EventTypes.class));
        Yaml placesYaml = new Yaml(new Constructor(Places.class));
        Yaml eventsYaml = new Yaml(new LocalDateLocalTimeYamlConstructor(Events.class));

        // Read descriptions from YAML files
        EventTypes eventTypes = eventTypesYaml.load(eventTypesResource.getInputStream());
        Map<Long, EventType> eventTypeMap = listToMap(eventTypes.getEventTypes(), EventType::getId);

        Places places = placesYaml.load(placesResource.getInputStream());
        Map<Long, Place> placeMap = listToMap(places.getPlaces(), Place::getId);

        Events events = eventsYaml.load(eventsResource.getInputStream());

        // Link entities
        linkEventsToPlaces(placeMap, events.getEvents());
        linkEventsToEventTypes(eventTypeMap, events.getEvents());

        return events.getEvents();
    }

    /**
     * Links speakers to talks
     *
     * @param speakers speakers
     * @param talks    talks
     */
    private static void linkSpeakersToTalks(Map<Long, Speaker> speakers, List<Talk> talks) {
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
    private static void linkEventsToEventTypes(Map<Long, EventType> eventTypes, List<Event> events) {
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
    private static void linkEventsToPlaces(Map<Long, Place> places, List<Event> events) {
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
    private static void linkTalksToEvents(Map<Long, Talk> talks, List<Event> events) {
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
     * Converts list of entities into map, throwing the IllegalStateException in case duplicate entities are found.
     *
     * @param list         Input list
     * @param keyExtractor Map key extractor for given entity class
     * @param <K>          Map key type
     * @param <T>          Entity (map value) type
     * @return Map of entities, or IllegalStateException if duplicate entities are found
     */
    private static <K, T> Map<K, T> listToMap(List<T> list, Function<? super T, ? extends K> keyExtractor) {
        Map<K, T> map =
                list.stream().collect(Collectors.toMap(keyExtractor, s -> s));
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
    private static boolean findSpeakerDuplicates(List<Speaker> speakers) {
        Set<Speaker> speakerDuplicates = new TreeSet<>(Comparator.comparingLong(Speaker::getId));

        for (Language language : Language.values()) {
            speakerDuplicates.addAll(LocalizationUtils.getSpeakerDuplicates(
                    speakers,
                    language,
                    s -> LocalizationUtils.getString(s.getName(), language),
                    s -> {
                        // Without company
                        String company = LocalizationUtils.getString(s.getCompany(), language);
                        return ((company == null) || company.isEmpty());
                    }));
        }

        if (!speakerDuplicates.isEmpty()) {
            log.error("{} speaker duplicates exist (add company to them): {}", speakerDuplicates.size(), speakerDuplicates);
            return true;
        }

        for (Language language : Language.values()) {
            speakerDuplicates.addAll(LocalizationUtils.getSpeakerDuplicates(
                    speakers,
                    language,
                    s -> LocalizationUtils.getSpeakerNameWithCompany(s, language),
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
        Path directoryPath = Path.of(OUTPUT_DIRECTORY_NAME);

        if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
            Files.walk(directoryPath)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    /**
     * Dumps items to file.
     *
     * @param items    items
     * @param filename filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    private static <T> void dump(T items, String filename) throws IOException, NoSuchFieldException {
        File file = new File(String.format("%s/%s", OUTPUT_DIRECTORY_NAME, filename));
        FileUtils.checkAndCreateDirectory(file.getParentFile());

        FileWriter writer = new FileWriter(file);

        DumperOptions options = new DumperOptions();
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setIndent(4);
        options.setIndicatorIndent(2);
        options.setWidth(120);

        List<PropertyMatcher> propertyMatchers = List.of(
                new PropertyMatcher(EventType.class,
                        List.of("id", "conference", "logoFileName", "name", "description", "siteLink", "vkLink",
                                "twitterLink", "facebookLink", "youtubeLink", "telegramLink")),
                new PropertyMatcher(Place.class,
                        List.of("id", "city", "venueAddress", "mapCoordinates")),
                new PropertyMatcher(Event.class,
                        List.of("eventTypeId", "name", "startDate", "endDate", "siteLink", "youtubeLink", "placeId",
                                "talkIds")),
                new PropertyMatcher(Talk.class,
                        List.of("id", "name", "shortDescription", "longDescription", "talkDay", "trackTime", "track",
                                "language", "presentationLinks", "videoLinks", "speakerIds")),
                new PropertyMatcher(Speaker.class,
                        List.of("id", "fileName", "name", "company", "bio", "twitter", "gitHub", "javaChampion", "mvp")),
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

        log.info("File '{}' saved", file.getAbsolutePath());
    }

    /**
     * Dumps event types to file.
     *
     * @param eventTypes event types
     * @param filename   filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    public static void dumpEventTypes(List<EventType> eventTypes, String filename) throws IOException, NoSuchFieldException {
        dump(new EventTypes(eventTypes), filename);
    }

    /**
     * Dumps speakers to file.
     *
     * @param speakers speakers
     * @param filename filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    public static void dumpSpeakers(List<Speaker> speakers, String filename) throws IOException, NoSuchFieldException {
        dump(new Speakers(speakers), filename);
    }

    /**
     * Dumps talks to file.
     *
     * @param talks    talks
     * @param filename filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    public static void dumpTalks(List<Talk> talks, String filename) throws IOException, NoSuchFieldException {
        dump(new Talks(talks), filename);
    }

    /**
     * Dumps event to file.
     *
     * @param event    event
     * @param filename filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    public static void dumpEvent(Event event, String filename) throws IOException, NoSuchFieldException {
        dump(new Events(Collections.singletonList(event)), filename);
    }

    /**
     * Dumps place to file.
     *
     * @param place    place
     * @param filename filename
     * @throws IOException          if file creation error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    public static void dumpPlace(Place place, String filename) throws IOException, NoSuchFieldException {
        dump(new Places(Collections.singletonList(place)), filename);
    }
}
