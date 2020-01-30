package guess.util;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Language;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;
import guess.domain.source.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Constructor with LocalDate support.
 */
class LocalDateYamlConstructor extends Constructor {
    LocalDateYamlConstructor(Class<?> theRoot) {
        super(theRoot);

        this.yamlClassConstructors.put(NodeId.scalar, new LocalDateConstructor());
    }

    private class LocalDateConstructor extends ConstructScalar {
        public Object construct(Node node) {
            if (node.getType().equals(LocalDate.class)) {
                return LocalDate.parse(((ScalarNode) node).getValue());
            } else {
                return super.construct(node);
            }
        }
    }
}

/**
 * YAML utility methods.
 */
public class YamlUtils {
    private static final Logger log = LoggerFactory.getLogger(YamlUtils.class);

    private static String DESCRIPTIONS_DIRECTORY_NAME = "descriptions";
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
        Resource eventsResource = resolver.getResource(String.format("classpath:%s/events.yml", DESCRIPTIONS_DIRECTORY_NAME));

        Yaml speakersYaml = new Yaml(new Constructor(Speakers.class));
        Yaml talksYaml = new Yaml(new Constructor(Talks.class));
        Yaml eventTypesYaml = new Yaml(new Constructor(EventTypes.class));
        Yaml eventsYaml = new Yaml(new LocalDateYamlConstructor(Events.class));

        // Read descriptions from YAML files
        Speakers speakers = speakersYaml.load(speakersResource.getInputStream());
        Map<Long, Speaker> speakerMap = listToMap(speakers.getSpeakers(), Speaker::getId);

        Talks talks = talksYaml.load(talksResource.getInputStream());
        Map<Long, Talk> talkMap = listToMap(talks.getTalks(), Talk::getId);

        EventTypes eventTypes = eventTypesYaml.load(eventTypesResource.getInputStream());
        Map<Long, EventType> eventTypeMap = listToMap(eventTypes.getEventTypes(), EventType::getId);

        Events events = eventsYaml.load(eventsResource.getInputStream());

        // Find duplicates for speaker names and for speaker names with company name
        if (findSpeakerDuplicates(speakers.getSpeakers())) {
            throw new SpeakerDuplicatedException();
        }

        // Link entities
        linkSpeakersToTalks(speakerMap, talks.getTalks());
        linkEventsToEventTypes(eventTypeMap, events.getEvents());
        linkTalksToEvents(talkMap, events.getEvents());

        return new SourceInformation(eventTypes.getEventTypes(), events.getEvents(), speakers.getSpeakers(), talks.getTalks());
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

        //TODO: delete
        Unsafe.replaceSpeakerQuestions(questionSets, sourceInformation.getSpeakers());

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
        Resource eventsResource = resolver.getResource(String.format("classpath:%s/events.yml", DESCRIPTIONS_DIRECTORY_NAME));

        Yaml eventTypesYaml = new Yaml(new Constructor(EventTypes.class));
        Yaml eventsYaml = new Yaml(new LocalDateYamlConstructor(Events.class));

        // Read descriptions from YAML files
        EventTypes eventTypes = eventTypesYaml.load(eventTypesResource.getInputStream());
        Map<Long, EventType> eventTypeMap = listToMap(eventTypes.getEventTypes(), EventType::getId);

        Events events = eventsYaml.load(eventsResource.getInputStream());

        // Link entities
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
                        () -> String.format("Speaker id %d not found", speakerId));
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
                    () -> String.format("EventType id %d not found", event.getEventTypeId()));
            eventType.getEvents().add(event);
            event.setEventType(eventType);
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
                        () -> String.format("Talk id %d not found", talkId));
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
    public static <K, T> Map<K, T> listToMap(List<T> list, Function<? super T, ? extends K> keyExtractor) {
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
     * Dumps event types to file.
     *
     * @param eventTypes event types
     * @param filename   filename
     * @throws IOException if file creation occurs
     */
    public static void dump(List<EventType> eventTypes, String filename) throws IOException {
        String fullFilename = String.format("%s/%s", OUTPUT_DIRECTORY_NAME, filename);
        File file = new File(fullFilename);
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);

        //TODO: keep field order
        //TODO: use remplate (?)
        Yaml eventTypesYaml = new Yaml(new Constructor(EventTypes.class));
        eventTypesYaml.dump(new EventTypes(eventTypes), writer);

        log.info("File '{}' saved", fullFilename);
    }
}
