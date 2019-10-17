package guess.util;

import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;
import guess.domain.source.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Constructor with LocalDate support.
 */
class LocalDateYamlConstructor extends Constructor {
    LocalDateYamlConstructor(Class<? extends Object> theRoot) {
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

    /**
     * Reads question sets from resource files.
     *
     * @param questionsDirectoryName    questions directory name
     * @param descriptionsDirectoryName descriptions directory name
     * @return question sets
     * @throws IOException if an I/O error occurs
     */
    public static List<QuestionSet> readQuestionSets(String questionsDirectoryName, String descriptionsDirectoryName) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource speakersResource = resolver.getResource(String.format("classpath:%s/speakers.yml", descriptionsDirectoryName));
        Resource talksResource = resolver.getResource(String.format("classpath:%s/talks.yml", descriptionsDirectoryName));
        Resource eventTypesResource = resolver.getResource(String.format("classpath:%s/event-types.yml", descriptionsDirectoryName));
        Resource eventsResource = resolver.getResource(String.format("classpath:%s/events.yml", descriptionsDirectoryName));

        Yaml yamlSpeakers = new Yaml(new Constructor(Speakers.class));
        Yaml yamlTalks = new Yaml(new Constructor(Talks.class));
        Yaml yamlEventTypes = new Yaml(new Constructor(EventTypes.class));
        Yaml yamlEvents = new Yaml(new LocalDateYamlConstructor(Events.class));

        // Read descriptions from YAML files
        Speakers speakers = yamlSpeakers.load(speakersResource.getInputStream());
        Map<Long, Speaker> speakerMap = listToMap(speakers.getSpeakers(), Speaker::getId);

        Talks talks = yamlTalks.load(talksResource.getInputStream());
        Map<Long, Talk> talkMap = listToMap(talks.getTalks(), Talk::getId);

        EventTypes eventTypes = yamlEventTypes.load(eventTypesResource.getInputStream());
        Map<Long, EventType> eventTypeMap = listToMap(eventTypes.getEventTypes(), EventType::getId);

        Events events = yamlEvents.load(eventsResource.getInputStream());

        // Link entities
        linkSpeakersToTalks(speakerMap, talks.getTalks());
        linkEventsToEventTypes(eventTypeMap, events.getEvents());
        linkTalksToEvents(talkMap, events.getEvents());

        // Create question sets
        List<QuestionSet> questionSets = new ArrayList<>();
        for (EventType eventType : eventTypes.getEventTypes()) {
            // Fill speaker and talk questions
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();
            List<TalkQuestion> talkQuestions = new ArrayList<>();

            for (Event event : eventType.getEvents()) {
                for (Talk talk : event.getTalks()) {
                    for (Speaker speaker : talk.getSpeakers()) {
                        speakerQuestions.add(new SpeakerQuestion(speaker));
                    }

                    talkQuestions.add(new TalkQuestion(
                            talk.getSpeakers().get(0),
                            talk));
                }
            }

            questionSets.add(new QuestionSet(
                    eventType.getId(),
                    LocalizationUtils.getEnglishName(eventType.getName()),
                    eventType.getLogoFileName(),
                    QuestionUtils.removeDuplicatesByFileName(speakerQuestions),
                    QuestionUtils.removeDuplicatesById(talkQuestions)));
        }

        //TODO: delete
        Unsafe.replaceSpeakerQuestions(questionSets, questionsDirectoryName);

        return questionSets;
    }

    /**
     * Reads events from resource files.
     *
     * @param descriptionsDirectoryName descriptions directory name
     * @return events
     * @throws IOException if an I/O error occurs
     */
    public static List<Event> readEvents(String descriptionsDirectoryName) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource eventTypesResource = resolver.getResource(String.format("classpath:%s/event-types.yml", descriptionsDirectoryName));
        Resource eventsResource = resolver.getResource(String.format("classpath:%s/events.yml", descriptionsDirectoryName));

        Yaml yamlEventTypes = new Yaml(new Constructor(EventTypes.class));
        Yaml yamlEvents = new Yaml(new LocalDateYamlConstructor(Events.class));

        // Read descriptions from YAML files
        EventTypes eventTypes = yamlEventTypes.load(eventTypesResource.getInputStream());
        Map<Long, EventType> eventTypeMap = listToMap(eventTypes.getEventTypes(), EventType::getId);

        Events events = yamlEvents.load(eventsResource.getInputStream());

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
    private static <K, T> Map<K, T> listToMap(List<T> list, Function<? super T, ? extends K> keyExtractor) {
        Map<K, T> map =
                list.stream().collect(Collectors.toMap(keyExtractor, s -> s));
        if (map.size() != list.size()) {
            throw new IllegalStateException("Entities with duplicate ids found");
        }
        return map;
    }
}
