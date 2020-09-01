package guess.util.yaml;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.source.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("YamlUtils class tests")
@TestMethodOrder(OrderAnnotation.class)
public class YamlUtilsTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSourceInformation method tests (with exception)")
    class GetSourceInformationTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem("en", "name0")));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setName(List.of(new LocaleItem("en", "name0")));

            PlaceList placeList = new PlaceList();
            placeList.setPlaces(Collections.emptyList());

            EventTypeList eventTypeList = new EventTypeList();
            eventTypeList.setEventTypes(Collections.emptyList());

            EventList eventList = new EventList();
            eventList.setEvents(Collections.emptyList());

            SpeakerList speakerList0 = new SpeakerList();
            speakerList0.setSpeakers(List.of(speaker0));

            SpeakerList speakerList1 = new SpeakerList();
            speakerList1.setSpeakers(List.of(speaker0, speaker1));

            TalkList talkList = new TalkList();
            talkList.setTalks(Collections.emptyList());

            return Stream.of(
                    arguments(placeList, eventTypeList, eventList, speakerList0, talkList,
                            new SourceInformation(
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    List.of(speaker0),
                                    Collections.emptyList()
                            ),
                            null),
                    arguments(placeList, eventTypeList, eventList, speakerList1, talkList, null, SpeakerDuplicatedException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSourceInformation(PlaceList placeList, EventTypeList eventTypeList, EventList eventList,
                                  SpeakerList speakerList, TalkList talkList, SourceInformation expectedResult,
                                  Class<? extends Exception> expectedException) throws SpeakerDuplicatedException {
            if (expectedException == null) {
                assertEquals(expectedResult, YamlUtils.getSourceInformation(placeList, eventTypeList, eventList, speakerList, talkList));
            } else {
                assertThrows(expectedException, () -> YamlUtils.getSourceInformation(placeList, eventTypeList, eventList, speakerList, talkList));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("linkSpeakersToTalks method tests (with exception)")
    class LinkSpeakersToTalksExceptionTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setSpeakerIds(Collections.emptyList());

            Talk talk1 = new Talk();
            talk1.setSpeakerIds(List.of(0L));

            Talk talk2 = new Talk();
            talk2.setSpeakerIds(List.of(1L));

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            return Stream.of(
                    arguments(Collections.emptyMap(), List.of(talk0), IllegalStateException.class),
                    arguments(Collections.emptyMap(), List.of(talk0, talk1), IllegalStateException.class),
                    arguments(Map.of(0L, speaker0), List.of(talk1, talk0), IllegalStateException.class),
                    arguments(Collections.emptyMap(), List.of(talk1, talk0), NullPointerException.class),
                    arguments(Map.of(0L, speaker0), List.of(talk1, talk2, talk0), NullPointerException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void linkSpeakersToTalks(Map<Long, Speaker> speakers, List<Talk> talks, Class<? extends Exception> expected) {
            assertThrows(expected, () -> YamlUtils.linkSpeakersToTalks(speakers, talks));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("linkEventsToEventTypes method tests (with exception)")
    class LinkEventsToEventTypesTest {
        private Stream<Arguments> data() {
            Event event0 = new Event();
            event0.setEventTypeId(0);

            Event event1 = new Event();
            event1.setEventTypeId(1);

            EventType eventType0 = new EventType();
            eventType0.setId(0);

            return Stream.of(
                    arguments(Collections.emptyMap(), List.of(event0), NullPointerException.class),
                    arguments(Map.of(0L, eventType0), List.of(event1), NullPointerException.class),
                    arguments(Map.of(0L, eventType0), List.of(event0, event1), NullPointerException.class),
                    arguments(Map.of(0L, eventType0), List.of(event1, event0), NullPointerException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void linkSpeakersToTalks(Map<Long, EventType> eventTypes, List<Event> events, Class<? extends Exception> expected) {
            assertThrows(expected, () -> YamlUtils.linkEventsToEventTypes(eventTypes, events));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("linkEventsToPlaces method tests (with exception)")
    class LinkEventsToPlacesTest {
        private Stream<Arguments> data() {
            Event event0 = new Event();
            event0.setPlaceId(0);

            Event event1 = new Event();
            event1.setPlaceId(1);

            Place place0 = new Place();
            place0.setId(0);

            return Stream.of(
                    arguments(Collections.emptyMap(), List.of(event0), NullPointerException.class),
                    arguments(Map.of(0L, place0), List.of(event1), NullPointerException.class),
                    arguments(Map.of(0L, place0), List.of(event0, event1), NullPointerException.class),
                    arguments(Map.of(0L, place0), List.of(event1, event0), NullPointerException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void linkSpeakersToTalks(Map<Long, Place> places, List<Event> events, Class<? extends Exception> expected) {
            assertThrows(expected, () -> YamlUtils.linkEventsToPlaces(places, events));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("linkTalksToEvents method tests (with exception)")
    class LinkTalksToEventsTest {
        private Stream<Arguments> data() {
            Event event0 = new Event();
            event0.setPlaceId(0);
            event0.setTalkIds(List.of(0L));

            Event event1 = new Event();
            event1.setPlaceId(1);
            event1.setTalkIds(List.of(1L));

            Talk talk0 = new Talk();
            talk0.setId(0L);

            return Stream.of(
                    arguments(Collections.emptyMap(), List.of(event0), NullPointerException.class),
                    arguments(Map.of(0L, talk0), List.of(event1), NullPointerException.class),
                    arguments(Map.of(0L, talk0), List.of(event0, event1), NullPointerException.class),
                    arguments(Map.of(0L, talk0), List.of(event1, event0), NullPointerException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void linkSpeakersToTalks(Map<Long, Talk> talks, List<Event> events, Class<? extends Exception> expected) {
            assertThrows(expected, () -> YamlUtils.linkTalksToEvents(talks, events));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("listToMap method tests (with exception)")
    class ListToMapTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Function<? super Speaker, ? extends Long> keyExtractor = (Function<Speaker, Long>) Speaker::getId;

            return Stream.of(
                    arguments(List.of(speaker0, speaker0), keyExtractor, IllegalStateException.class),
                    arguments(List.of(speaker0, speaker1, speaker1), keyExtractor, IllegalStateException.class),
                    arguments(List.of(speaker0, speaker1, speaker1, speaker0), keyExtractor, IllegalStateException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void listToMap(List<Speaker> speakers, Function<? super Speaker, ? extends Long> keyExtractor, Class<? extends Exception> expected) {
            assertThrows(expected, () -> YamlUtils.listToMap(speakers, keyExtractor));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("findSpeakerDuplicates method tests")
    class FindSpeakerDuplicatesTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem("en", "name0")));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setName(List.of(new LocaleItem("en", "name0")));
            speaker1.setCompany(List.of(new LocaleItem("en", null)));

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);
            speaker2.setName(List.of(new LocaleItem("en", "name0")));
            speaker2.setCompany(List.of(new LocaleItem("en", "")));

            Speaker speaker3 = new Speaker();
            speaker3.setId(3);
            speaker3.setName(List.of(new LocaleItem("en", "name3")));
            speaker3.setCompany(List.of(new LocaleItem("en", "company3")));

            Speaker speaker4 = new Speaker();
            speaker4.setId(4);
            speaker4.setName(List.of(new LocaleItem("en", "name3")));
            speaker4.setCompany(List.of(new LocaleItem("en", "company3")));

            return Stream.of(
                    arguments(Collections.emptyList(), false),
                    arguments(List.of(speaker0, speaker1), true),
                    arguments(List.of(speaker0, speaker2), true),
                    arguments(List.of(speaker0, speaker1, speaker2), true),
                    arguments(List.of(speaker3, speaker4), true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void listToMap(List<Speaker> speakers, boolean expected) {
            assertEquals(expected, YamlUtils.findSpeakerDuplicates(speakers));
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        YamlUtils.clearDumpDirectory();
    }

    @AfterEach
    void tearDown() throws IOException {
        YamlUtils.clearDumpDirectory();
    }

    @Test
    @Order(1)
    void clearDumpDirectory() throws IOException {
        Path directoryPath = Path.of(YamlUtils.OUTPUT_DIRECTORY_NAME);
        Path filePath = Path.of(YamlUtils.OUTPUT_DIRECTORY_NAME + "/file.ext");

        assertFalse(Files.exists(filePath));
        assertFalse(Files.exists(directoryPath));

        Files.createDirectory(directoryPath);
        Files.createFile(filePath);

        assertTrue(Files.exists(directoryPath) && Files.isDirectory(directoryPath));
        assertTrue(Files.exists(filePath) && !Files.isDirectory(filePath));

        YamlUtils.clearDumpDirectory();

        assertFalse(Files.exists(filePath));
        assertFalse(Files.exists(directoryPath));
    }

    @Test
    @Order(2)
    void dump() throws IOException, NoSuchFieldException {
        Path directoryPath = Path.of(YamlUtils.OUTPUT_DIRECTORY_NAME);
        Path filePath = Path.of(YamlUtils.OUTPUT_DIRECTORY_NAME + "/event-types.yml");

        assertFalse(Files.exists(filePath));
        assertFalse(Files.exists(directoryPath));

        YamlUtils.dump(new EventTypeList(Collections.emptyList()), "event-types.yml");

        assertTrue(Files.exists(directoryPath) && Files.isDirectory(directoryPath));
        assertTrue(Files.exists(filePath) && !Files.isDirectory(filePath));
    }
}
