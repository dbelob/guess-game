package guess.util.yaml;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.source.*;
import guess.util.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("YamlUtils class tests")
@TestMethodOrder(OrderAnnotation.class)
class YamlUtilsTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSourceInformation method tests")
    class GetSourceInformationTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem("en", "name0")));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setName(List.of(new LocaleItem("en", "name0")));

            List<Place> places = Collections.emptyList();
            List<Organizer> organizers = Collections.emptyList();
            List<EventType> eventTypes = Collections.emptyList();
            List<Event> events = Collections.emptyList();
            List<Company> companies = Collections.emptyList();
            List<CompanyGroup> companyGroupList = Collections.emptyList();
            List<CompanySynonyms> companySynonymsList = Collections.emptyList();

            List<Speaker> speakers0 = List.of(speaker0);
            List<Speaker> speakers1 = List.of(speaker0, speaker1);

            List<Talk> talks = Collections.emptyList();

            return Stream.of(
                    arguments(places, organizers, eventTypes, events, companies, companyGroupList, companySynonymsList,
                            speakers0, talks, null,
                            new SourceInformation(
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    new SourceInformation.SpeakerInformation(
                                            Collections.emptyList(),
                                            Collections.emptyList(),
                                            Collections.emptyList(),
                                            List.of(speaker0)
                                    ),
                                    Collections.emptyList()
                            )),
                    arguments(places, organizers, eventTypes, events, companies, companyGroupList, companySynonymsList,
                            speakers1, talks, SpeakerDuplicatedException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSourceInformation(List<Place> places, List<Organizer> organizers, List<EventType> eventTypes, List<Event> events,
                                  List<Company> companies, List<CompanyGroup> companyGroupList, List<CompanySynonyms> companySynonymsList,
                                  List<Speaker> speakers, List<Talk> talks, Class<? extends Exception> expectedException,
                                  SourceInformation expectedResult) throws SpeakerDuplicatedException {
            if (expectedException == null) {
                assertEquals(expectedResult, YamlUtils.getSourceInformation(
                        places,
                        organizers,
                        eventTypes,
                        events,
                        new SourceInformation.SpeakerInformation(
                                companies,
                                companyGroupList,
                                companySynonymsList,
                                speakers
                        ),
                        talks));
            } else {
                assertThrows(expectedException, () -> YamlUtils.getSourceInformation(
                        places,
                        organizers,
                        eventTypes,
                        events,
                        new SourceInformation.SpeakerInformation(
                                companies,
                                companyGroupList,
                                companySynonymsList,
                                speakers
                        ),
                        talks));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkAndFillTimeZones method tests")
    class CheckAndFillTimeZonesTest {
        EventType createEventType(long id) {
            EventType eventType = new EventType();

            eventType.setId(id);
            if (id == 0) {
                eventType.setTimeZone("Europe/Moscow");
            }

            return eventType;
        }

        Event createEvent(long id) {
            Event event = new Event();

            event.setId(id);
            if (id == 0) {
                event.setTimeZone("Europe/Moscow");
            }

            return event;
        }

        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyList(), null),
                    arguments(List.of(createEventType(0)), List.of(createEvent(0)), null),
                    arguments(List.of(createEventType(0)), List.of(createEvent(1)), null),
                    arguments(List.of(createEventType(0)), List.of(createEvent(0), createEvent(1)), null),
                    arguments(List.of(createEventType(1)), List.of(createEvent(0)), NullPointerException.class),
                    arguments(List.of(createEventType(1)), List.of(createEvent(1)), NullPointerException.class),
                    arguments(List.of(createEventType(1)), List.of(createEvent(0), createEvent(1)), NullPointerException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void checkAndFillTimeZones(List<EventType> eventTypes, List<Event> events, Class<? extends Exception> expected) {
            if (expected == null) {
                eventTypes.forEach(et -> assertNull(et.getTimeZoneId()));
                events.forEach(e -> assertNull(e.getTimeZoneId()));

                assertDoesNotThrow(() -> YamlUtils.checkAndFillTimeZones(eventTypes, events));

                eventTypes.forEach(et -> assertNotNull(et.getTimeZoneId()));
                events.forEach(e -> assertTrue(
                        ((e.getTimeZone() == null) && (e.getTimeZoneId() == null)) ||
                                ((e.getTimeZone() != null) && (e.getTimeZoneId() != null))));
            } else {
                assertThrows(expected, () -> YamlUtils.checkAndFillTimeZones(eventTypes, events));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("linkEventTypesToOrganizers method tests")
    class LinkEventTypesToOrganizersTest {
        private Stream<Arguments> data() {
            EventType eventType0 = new EventType();
            eventType0.setOrganizerId(0);

            EventType eventType1 = new EventType();
            eventType1.setOrganizerId(1);

            Organizer organizer0 = new Organizer();
            organizer0.setId(0);

            return Stream.of(
                    arguments(Collections.emptyMap(), List.of(eventType0), NullPointerException.class),
                    arguments(Map.of(0L, organizer0), List.of(eventType0), null),
                    arguments(Map.of(0L, organizer0), List.of(eventType1), NullPointerException.class),
                    arguments(Map.of(0L, organizer0), List.of(eventType0, eventType1), NullPointerException.class),
                    arguments(Map.of(0L, organizer0), List.of(eventType1, eventType0), NullPointerException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void linkEventTypesToOrganizers(Map<Long, Organizer> organizers, List<EventType> eventTypes, Class<? extends Exception> expected) {
            if (expected == null) {
                eventTypes.forEach(et -> assertNull(et.getOrganizer()));

                assertDoesNotThrow(() -> YamlUtils.linkEventTypesToOrganizers(organizers, eventTypes));

                eventTypes.forEach(et -> assertNotNull(et.getOrganizer()));
            } else {
                assertThrows(expected, () -> YamlUtils.linkEventTypesToOrganizers(organizers, eventTypes));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("linkEventsToEventTypes method tests")
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
                    arguments(Map.of(0L, eventType0), List.of(event0), null),
                    arguments(Map.of(0L, eventType0), List.of(event1), NullPointerException.class),
                    arguments(Map.of(0L, eventType0), List.of(event0, event1), NullPointerException.class),
                    arguments(Map.of(0L, eventType0), List.of(event1, event0), NullPointerException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void linkSpeakersToTalks(Map<Long, EventType> eventTypes, List<Event> events, Class<? extends Exception> expected) {
            if (expected == null) {
                eventTypes.forEach((id, et) -> assertTrue(et.getEvents().isEmpty()));
                events.forEach(et -> assertNull(et.getEventType()));

                assertDoesNotThrow(() -> YamlUtils.linkEventsToEventTypes(eventTypes, events));

                eventTypes.forEach((id, et) -> assertFalse(et.getEvents().isEmpty()));
                events.forEach(et -> assertNotNull(et.getEventType()));
            } else {
                assertThrows(expected, () -> YamlUtils.linkEventsToEventTypes(eventTypes, events));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("linkEventsToPlaces method tests")
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
                    arguments(Map.of(0L, place0), List.of(event0), null),
                    arguments(Map.of(0L, place0), List.of(event1), NullPointerException.class),
                    arguments(Map.of(0L, place0), List.of(event0, event1), NullPointerException.class),
                    arguments(Map.of(0L, place0), List.of(event1, event0), NullPointerException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void linkSpeakersToTalks(Map<Long, Place> places, List<Event> events, Class<? extends Exception> expected) {
            if (expected == null) {
                events.forEach(et -> assertNull(et.getPlace()));

                assertDoesNotThrow(() -> YamlUtils.linkEventsToPlaces(places, events));

                events.forEach(et -> assertNotNull(et.getPlace()));
            } else {
                assertThrows(expected, () -> YamlUtils.linkEventsToPlaces(places, events));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("linkTalksToEvents method tests")
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
                    arguments(Map.of(0L, talk0), List.of(event0), null),
                    arguments(Map.of(0L, talk0), List.of(event1), NullPointerException.class),
                    arguments(Map.of(0L, talk0), List.of(event0, event1), NullPointerException.class),
                    arguments(Map.of(0L, talk0), List.of(event1, event0), NullPointerException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void linkSpeakersToTalks(Map<Long, Talk> talks, List<Event> events, Class<? extends Exception> expected) {
            if (expected == null) {
                events.forEach(e -> assertTrue(e.getTalks().isEmpty()));

                assertDoesNotThrow(() -> YamlUtils.linkTalksToEvents(talks, events));

                events.forEach(e -> assertFalse(e.getTalks().isEmpty()));
            } else {
                assertThrows(expected, () -> YamlUtils.linkTalksToEvents(talks, events));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("linkSpeakersToCompanies method tests")
    class LinkSpeakersToCompaniesTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setCompanyIds(List.of(0L));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setCompanyIds(List.of(1L));

            Company company0 = new Company();
            company0.setId(0);

            return Stream.of(
                    arguments(Collections.emptyMap(), List.of(speaker0), NullPointerException.class),
                    arguments(Map.of(0L, company0), List.of(speaker0), null),
                    arguments(Map.of(0L, company0), List.of(speaker1), NullPointerException.class),
                    arguments(Map.of(0L, company0), List.of(speaker0, speaker1), NullPointerException.class),
                    arguments(Map.of(0L, company0), List.of(speaker1, speaker0), NullPointerException.class)
            );
        }


        @ParameterizedTest
        @MethodSource("data")
        void linkSpeakersToCompanies(Map<Long, Company> companies, List<Speaker> speakers, Class<? extends Exception> expected) {
            if (expected == null) {
                speakers.forEach(s -> assertTrue(s.getCompanies().isEmpty()));

                assertDoesNotThrow(() -> YamlUtils.linkSpeakersToCompanies(companies, speakers));

                speakers.forEach(s -> assertFalse(s.getCompanies().isEmpty()));
            } else {
                assertThrows(expected, () -> YamlUtils.linkSpeakersToCompanies(companies, speakers));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("linkSpeakersToTalks method tests")
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
                    arguments(Map.of(0L, speaker0), List.of(talk1), null),
                    arguments(Map.of(0L, speaker0), List.of(talk1, talk0), IllegalStateException.class),
                    arguments(Collections.emptyMap(), List.of(talk1, talk0), NullPointerException.class),
                    arguments(Map.of(0L, speaker0), List.of(talk1, talk2, talk0), NullPointerException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void linkSpeakersToTalks(Map<Long, Speaker> speakers, List<Talk> talks, Class<? extends Exception> expected) {
            if (expected == null) {
                talks.forEach(t -> assertTrue(t.getSpeakers().isEmpty()));

                assertDoesNotThrow(() -> YamlUtils.linkSpeakersToTalks(speakers, talks));

                talks.forEach(t -> assertFalse(t.getSpeakers().isEmpty()));
            } else {
                assertThrows(expected, () -> YamlUtils.linkSpeakersToTalks(speakers, talks));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("listToMap method tests")
    class ListToMapTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Function<? super Speaker, ? extends Long> keyExtractor = (Function<Speaker, Long>) Speaker::getId;

            return Stream.of(
                    arguments(List.of(speaker0), keyExtractor, null, Map.of(0L, speaker0)),
                    arguments(List.of(speaker0, speaker0), keyExtractor, IllegalStateException.class, null),
                    arguments(List.of(speaker0, speaker1, speaker1), keyExtractor, IllegalStateException.class, null),
                    arguments(List.of(speaker0, speaker1, speaker1, speaker0), keyExtractor, IllegalStateException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void listToMap(List<Speaker> speakers, Function<? super Speaker, ? extends Long> keyExtractor,
                       Class<? extends Exception> expectedException, Map<Long, Speaker> expectedValue) {
            if (expectedException == null) {
                assertEquals(expectedValue, YamlUtils.listToMap(speakers, keyExtractor));
            } else {
                assertThrows(expectedException, () -> YamlUtils.listToMap(speakers, keyExtractor));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("findSpeakerDuplicates method tests")
    class FindSpeakerDuplicatesTest {
        private Stream<Arguments> data() {
            Company company0 = new Company(0, List.of(new LocaleItem("en", "company0")));

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem("en", "name0")));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setName(List.of(new LocaleItem("en", "name0")));

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);
            speaker2.setName(List.of(new LocaleItem("en", "name2")));
            speaker2.setCompanies(List.of(company0));

            Speaker speaker3 = new Speaker();
            speaker3.setId(3);
            speaker3.setName(List.of(new LocaleItem("en", "name2")));
            speaker3.setCompanies(List.of(company0));

            return Stream.of(
                    arguments(Collections.emptyList(), false),
                    arguments(List.of(speaker0, speaker1), true),
                    arguments(List.of(speaker2, speaker3), true)
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
        YamlUtils.clearOutputDirectory();
    }

    @AfterEach
    void tearDown() throws IOException {
        YamlUtils.clearOutputDirectory();
    }

    @Test
    void clearOutputDirectory() throws IOException {
        try (MockedStatic<FileUtils> mockedStatic = Mockito.mockStatic(FileUtils.class)) {
            YamlUtils.clearOutputDirectory();

            mockedStatic.verify(() -> FileUtils.deleteDirectory(YamlUtils.OUTPUT_DIRECTORY_NAME), VerificationModeFactory.times(1));
        }
    }

    @Test
    void save() {
        assertDoesNotThrow(() -> YamlUtils.save(new EventTypeList(Collections.emptyList()), "event-types.yml"));
    }
}
