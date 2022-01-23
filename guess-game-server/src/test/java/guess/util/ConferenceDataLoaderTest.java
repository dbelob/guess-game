package guess.util;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.image.UrlFilename;
import guess.domain.source.load.LoadResult;
import guess.domain.source.load.LoadSettings;
import guess.domain.source.load.SpeakerLoadMaps;
import guess.domain.source.load.SpeakerLoadResult;
import guess.util.yaml.YamlUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("ConferenceDataLoader class tests")
class ConferenceDataLoaderTest {
    @Test
    void loadSpaceTags() {
        try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
            final String CODE1 = "code1";
            final String CODE2 = "code2";
            final String CODE3 = "code3";
            final String CODE4 = "code4";

            mockedStatic.when(() -> ContentfulUtils.getTags(Mockito.anyString()))
                    .thenReturn(Map.of(
                            ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO,
                            List.of(CODE1, CODE2, CODE3, CODE4)));
            assertDoesNotThrow(() -> ConferenceDataLoader.loadSpaceTags(null));
        }
    }

    @Test
    void loadEventTypes() {
        try (MockedStatic<YamlUtils> yamlUtilsMockedStatic = Mockito.mockStatic(YamlUtils.class);
             MockedStatic<ContentfulUtils> contentfulUtilsMockedStatic = Mockito.mockStatic(ContentfulUtils.class);
             MockedStatic<ConferenceDataLoader> conferenceDataLoaderMockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
            yamlUtilsMockedStatic.when(YamlUtils::readSourceInformation)
                    .thenReturn(new SourceInformation(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                            Collections.emptyList(),
                            new SourceInformation.SpeakerInformation(
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList()
                            ),
                            Collections.emptyList()
                    ));
            contentfulUtilsMockedStatic.when(ContentfulUtils::getEventTypes)
                    .thenReturn(Collections.emptyList());
            conferenceDataLoaderMockedStatic.when(ConferenceDataLoader::loadEventTypes)
                    .thenCallRealMethod();
            conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getConferences(Mockito.anyList()))
                    .thenReturn(Collections.emptyList());
            conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getResourceEventTypeMap(Mockito.anyList()))
                    .thenReturn(Collections.emptyMap());
            conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getLastId(Mockito.anyList()))
                    .thenReturn(42L);
            conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getEventTypeLoadResult(Mockito.anyList(), Mockito.anyMap(), Mockito.any()))
                    .thenReturn(new LoadResult<>(Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));

            assertDoesNotThrow(ConferenceDataLoader::loadEventTypes);
        }
    }

    @Test
    void getConferences() {
        EventType eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);

        EventType eventType1 = new EventType();
        eventType1.setId(1);

        EventType eventType2 = new EventType();
        eventType2.setId(2);
        eventType2.setConference(Conference.JOKER);

        assertEquals(List.of(eventType0, eventType2), ConferenceDataLoader.getConferences(List.of(eventType0, eventType1, eventType2)));
    }

    @Test
    void getResourceEventTypeMap() {
        EventType eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);

        EventType eventType1 = new EventType();
        eventType1.setId(1);

        EventType eventType2 = new EventType();
        eventType2.setId(2);
        eventType2.setConference(Conference.JOKER);

        Map<Conference, EventType> expected = new HashMap<>();
        expected.put(Conference.JPOINT, eventType0);
        expected.put(null, eventType1);
        expected.put(Conference.JOKER, eventType2);

        assertEquals(expected, ConferenceDataLoader.getResourceEventTypeMap(List.of(eventType0, eventType1, eventType2)));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getLastId method tests")
    class GetLastIdTest {
        private Stream<Arguments> data() {
            EventType eventType0 = new EventType();
            eventType0.setId(0);

            EventType eventType1 = new EventType();
            eventType1.setId(1);

            EventType eventType2 = new EventType();
            eventType2.setId(2);

            return Stream.of(
                    arguments(Collections.emptyList(), -1),
                    arguments(List.of(eventType0), 0),
                    arguments(List.of(eventType0, eventType1), 1),
                    arguments(List.of(eventType0, eventType1, eventType2), 2),
                    arguments(List.of(eventType1, eventType0), 1),
                    arguments(List.of(eventType1, eventType0, eventType2), 2)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getLastId(List<EventType> entities, long expected) {
            assertEquals(expected, ConferenceDataLoader.getLastId(entities));
        }
    }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getFirstId method tests")
    class GetFirstIdTest {
        private Stream<Arguments> data() {
            EventType eventType0 = new EventType();
            eventType0.setId(42);

            EventType eventType1 = new EventType();
            eventType1.setId(43);

            EventType eventType2 = new EventType();
            eventType2.setId(44);

            return Stream.of(
                    arguments(Collections.emptyList(), 0),
                    arguments(List.of(eventType0), 42),
                    arguments(List.of(eventType0, eventType1), 42),
                    arguments(List.of(eventType0, eventType1, eventType2), 42),
                    arguments(List.of(eventType1, eventType0), 42),
                    arguments(List.of(eventType1, eventType0, eventType2), 42),
                    arguments(List.of(eventType2), 44),
                    arguments(List.of(eventType1, eventType2), 43)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getFirstId(List<EventType> entities, long expected) {
            assertEquals(expected, ConferenceDataLoader.getFirstId(entities));
        }
    }

    @Test
    void getEventTypeLoadResult() {
        try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
            mockedStatic.when(() -> ContentfulUtils.needUpdate(Mockito.any(EventType.class), Mockito.any(EventType.class)))
                    .thenAnswer(
                            (Answer<Boolean>) invocation -> {
                                Object[] args = invocation.getArguments();
                                EventType a = (EventType) args[0];
                                EventType b = (EventType) args[1];

                                return (Conference.JPOINT.equals(a.getConference()) && Conference.JPOINT.equals(b.getConference()));
                            }
                    );

            EventType eventType0 = new EventType();
            eventType0.setId(0);

            EventType eventType1 = new EventType();
            eventType1.setId(1);
            eventType1.setConference(Conference.JOKER);

            EventType eventType2 = new EventType();
            eventType2.setId(2);
            eventType2.setConference(Conference.JPOINT);

            EventType eventType3 = new EventType();
            eventType3.setId(3);
            eventType3.setConference(Conference.DOT_NEXT);

            Map<Conference, EventType> eventTypeMap = new HashMap<>(Map.of(Conference.JPOINT, eventType2,
                    Conference.DOT_NEXT, eventType3));

            LoadResult<List<EventType>> expected = new LoadResult<>(
                    Collections.emptyList(),
                    List.of(eventType0, eventType1),
                    List.of(eventType2));

            assertEquals(expected, ConferenceDataLoader.getEventTypeLoadResult(
                    List.of(eventType0, eventType1, eventType2, eventType3),
                    eventTypeMap,
                    new AtomicLong(-1)));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("saveEventTypes method tests")
    class SaveEventTypesTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList())),
                    arguments(new LoadResult<>(
                            Collections.emptyList(),
                            List.of(new EventType()),
                            Collections.emptyList())),
                    arguments(new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            List.of(new EventType()))),
                    arguments(new LoadResult<>(
                            Collections.emptyList(),
                            List.of(new EventType()),
                            List.of(new EventType())))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void saveEventTypes(LoadResult<List<EventType>> loadResult) {
            try (MockedStatic<ConferenceDataLoader> conferenceDataLoaderMockedStatic = Mockito.mockStatic(ConferenceDataLoader.class);
                 MockedStatic<YamlUtils> yamlUtilsMockedStatic = Mockito.mockStatic(YamlUtils.class)) {
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.saveEventTypes(Mockito.any()))
                        .thenCallRealMethod();

                assertDoesNotThrow(() -> ConferenceDataLoader.saveEventTypes(loadResult));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("loadTalksSpeakersEvent method tests")
    class LoadTalksSpeakersEventTest {
        private Stream<Arguments> data() {
            final Conference JPOINT_CONFERENCE = Conference.JPOINT;
            final LocalDate EVENT_DATE = LocalDate.of(2020, 6, 29);
            final String EVENT_CODE = "2020-jpoint";

            Place place0 = new Place();

            Organizer organizer0 = new Organizer();

            Event event0 = new Event();
            event0.setId(0);
            event0.setStartDate(EVENT_DATE);
            event0.setPlace(place0);

            EventType eventType0 = new EventType();
            eventType0.setId(0);
            eventType0.setConference(JPOINT_CONFERENCE);
            eventType0.setOrganizer(organizer0);
            eventType0.setEvents(List.of(event0));

            Talk talk0 = new Talk();
            talk0.setId(0);

            Company company0 = new Company(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company0")));

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
            speaker0.setCompanies(List.of(company0));

            return Stream.of(
                    arguments(JPOINT_CONFERENCE, EVENT_DATE, EVENT_CODE, LoadSettings.defaultSettings(),
                            new SourceInformation(
                                    List.of(place0),
                                    List.of(organizer0),
                                    List.of(eventType0),
                                    Collections.emptyList(),
                                    new SourceInformation.SpeakerInformation(
                                            Collections.emptyList(),
                                            Collections.emptyList(),
                                            Collections.emptyList(),
                                            List.of(speaker0)
                                    ),
                                    Collections.emptyList()),
                            event0,
                            List.of(talk0),
                            List.of(speaker0),
                            List.of(company0),
                            Map.of("name0", company0)),
                    arguments(JPOINT_CONFERENCE, LocalDate.of(2020, 6, 30), EVENT_CODE, LoadSettings.defaultSettings(),
                            new SourceInformation(
                                    List.of(place0),
                                    List.of(organizer0),
                                    List.of(eventType0),
                                    Collections.emptyList(),
                                    new SourceInformation.SpeakerInformation(
                                            Collections.emptyList(),
                                            Collections.emptyList(),
                                            Collections.emptyList(),
                                            List.of(speaker0)
                                    ),
                                    Collections.emptyList()),
                            event0,
                            List.of(talk0),
                            List.of(speaker0),
                            List.of(company0),
                            Map.of("name0", company0))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void loadTalksSpeakersEvent(Conference conference, LocalDate startDate, String conferenceCode,
                                    LoadSettings loadSettings, SourceInformation sourceInformation, Event contentfulEvent,
                                    List<Talk> contentfulTalks, List<Speaker> talkSpeakers, List<Company> speakerCompanies,
                                    Map<String, Company> resourceLowerNameCompanyMap) {
            try (MockedStatic<YamlUtils> yamlUtilsMockedStatic = Mockito.mockStatic(YamlUtils.class);
                 MockedStatic<LocalizationUtils> localizationUtilsMockedStatic = Mockito.mockStatic(LocalizationUtils.class);
                 MockedStatic<ContentfulUtils> contentfulUtilsMockedStatic = Mockito.mockStatic(ContentfulUtils.class);
                 MockedStatic<ConferenceDataLoader> conferenceDataLoaderMockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                yamlUtilsMockedStatic.when(YamlUtils::readSourceInformation)
                        .thenReturn(sourceInformation);
                localizationUtilsMockedStatic.when(() -> LocalizationUtils.getString(Mockito.nullable(List.class), Mockito.any(Language.class)))
                        .thenReturn("");
                contentfulUtilsMockedStatic.when(() -> ContentfulUtils.getEvent(Mockito.any(Conference.class), Mockito.any(LocalDate.class)))
                        .thenReturn(contentfulEvent);
                contentfulUtilsMockedStatic.when(() -> ContentfulUtils.getTalks(Mockito.any(Conference.class), Mockito.anyString(), Mockito.anyBoolean()))
                        .thenReturn(contentfulTalks);
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.loadTalksSpeakersEvent(
                                Mockito.any(Conference.class), Mockito.any(LocalDate.class), Mockito.anyString(), Mockito.any(LoadSettings.class)))
                        .thenCallRealMethod();
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.deleteInvalidTalks(Mockito.anyList(), Mockito.anySet()))
                        .thenAnswer(
                                (Answer<List<Talk>>) invocation -> {
                                    Object[] args = invocation.getArguments();

                                    return (List<Talk>) args[0];
                                }
                        );
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.deleteOpeningAndClosingTalks(Mockito.anyList()))
                        .thenAnswer(
                                (Answer<List<Talk>>) invocation -> {
                                    Object[] args = invocation.getArguments();

                                    return (List<Talk>) args[0];
                                }
                        );
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.deleteTalkDuplicates(Mockito.anyList()))
                        .thenAnswer(
                                (Answer<List<Talk>>) invocation -> {
                                    Object[] args = invocation.getArguments();

                                    return (List<Talk>) args[0];
                                }
                        );
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getTalkSpeakers(Mockito.anyList()))
                        .thenReturn(talkSpeakers);
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getSpeakerCompanies(Mockito.anyList()))
                        .thenReturn(speakerCompanies);
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getResourceLowerNameCompanyMap(Mockito.anyList()))
                        .thenReturn(resourceLowerNameCompanyMap);
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getLastId(Mockito.anyList()))
                        .thenReturn(42L);
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getCompanyLoadResult(
                                Mockito.anyList(), Mockito.anyMap(), Mockito.any()))
                        .thenReturn(new LoadResult<>(
                                Collections.emptyList(),
                                Collections.emptyList(),
                                Collections.emptyList()));
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getResourceNameCompanySpeakerMap(Mockito.anyList()))
                        .thenReturn(Collections.emptyMap());
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getResourceNameSpeakersMap(Mockito.anyList()))
                        .thenReturn(Collections.emptyMap());
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getSpeakerLoadResult(
                                Mockito.anyList(), Mockito.any(SpeakerLoadMaps.class), Mockito.any()))
                        .thenReturn(new SpeakerLoadResult(
                                new LoadResult<>(
                                        Collections.emptyList(),
                                        Collections.emptyList(),
                                        Collections.emptyList()),
                                new LoadResult<>(
                                        Collections.emptyList(),
                                        Collections.emptyList(),
                                        Collections.emptyList())));
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getTalkLoadResult(
                                Mockito.anyList(), Mockito.any(Event.class), Mockito.anyList(), Mockito.any()))
                        .thenReturn(new LoadResult<>(
                                Collections.emptyList(),
                                Collections.emptyList(),
                                Collections.emptyList()));
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.fixVenueAddress(Mockito.any(Place.class)))
                        .thenReturn(Collections.emptyList());
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.findResourcePlace(
                                Mockito.any(Place.class), Mockito.anyMap(), Mockito.anyMap()))
                        .thenAnswer(
                                (Answer<Place>) invocation -> {
                                    Object[] args = invocation.getArguments();

                                    return (Place) args[0];
                                }
                        );
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getPlaceLoadResult(
                                Mockito.any(Place.class), Mockito.any(Place.class), Mockito.any()))
                        .thenReturn(new LoadResult<>(
                                null,
                                null,
                                null));
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getEventLoadResult(Mockito.any(Event.class), Mockito.any(Event.class)))
                        .thenReturn(new LoadResult<>(
                                null,
                                null,
                                null));

                assertDoesNotThrow(() -> ConferenceDataLoader.loadTalksSpeakersEvent(conference, startDate, conferenceCode, loadSettings));
            }
        }
    }

    @Test
    void loadTalksSpeakersEventWithoutInvalidTalksSetAndKnownSpeakerIdsMap() {
        try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
            mockedStatic.when(() -> ConferenceDataLoader.loadTalksSpeakersEvent(
                            Mockito.any(Conference.class), Mockito.any(LocalDate.class), Mockito.anyString()))
                    .thenCallRealMethod();

            assertDoesNotThrow(() -> ConferenceDataLoader.loadTalksSpeakersEvent(
                    Conference.JPOINT,
                    LocalDate.of(2020, 6, 29),
                    "2020-jpoint"));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("deleteInvalidTalks method tests")
    class DeleteInvalidTalksTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setId(0);
            talk0.setName(List.of(new LocaleItem("en", "Name0")));

            Talk talk1 = new Talk();
            talk1.setId(1);
            talk1.setName(List.of(new LocaleItem("en", "Name1")));

            return Stream.of(
                    arguments(List.of(talk0, talk1), Collections.emptySet(), List.of(talk0, talk1)),
                    arguments(List.of(talk0, talk1), Set.of("Name0"), List.of(talk1))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void deleteInvalidTalks(List<Talk> talks, Set<String> invalidTalksSet, List<Talk> expected) {
            try (MockedStatic<LocalizationUtils> mockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
                mockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                        .thenAnswer(
                                (Answer<String>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    List<LocaleItem> localeItems = (List<LocaleItem>) args[0];

                                    return ((localeItems != null) && !localeItems.isEmpty()) ? localeItems.get(0).getText() : null;
                                }
                        );

                assertEquals(expected, ConferenceDataLoader.deleteInvalidTalks(talks, invalidTalksSet));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("deleteOpeningAndClosingTalks method tests")
    class DeleteOpeningAndClosingTalksTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setId(0);
            talk0.setName(List.of(new LocaleItem("en", "Conference opening")));

            Talk talk1 = new Talk();
            talk1.setId(1);
            talk1.setName(List.of(new LocaleItem("en", "Conference closing")));

            Talk talk2 = new Talk();
            talk2.setId(2);
            talk2.setName(List.of(new LocaleItem("en", "School opening")));

            Talk talk3 = new Talk();
            talk3.setId(3);
            talk3.setName(List.of(new LocaleItem("en", "School closing")));

            Talk talk4 = new Talk();
            talk4.setId(4);
            talk4.setName(List.of(new LocaleItem("ru", "Открытие")));

            Talk talk5 = new Talk();
            talk5.setId(5);
            talk5.setName(List.of(new LocaleItem("ru", "Закрытие")));

            Talk talk6 = new Talk();
            talk6.setId(6);
            talk6.setName(List.of(new LocaleItem("ru", "Открытие конференции")));

            Talk talk7 = new Talk();
            talk7.setId(7);
            talk7.setName(List.of(new LocaleItem("ru", "Закрытие конференции")));

            Talk talk8 = new Talk();
            talk8.setId(8);
            talk8.setName(List.of(new LocaleItem("en", "name8")));

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyList()),
                    arguments(List.of(talk0), Collections.emptyList()),
                    arguments(List.of(talk1), Collections.emptyList()),
                    arguments(List.of(talk2), Collections.emptyList()),
                    arguments(List.of(talk3), Collections.emptyList()),
                    arguments(List.of(talk4), Collections.emptyList()),
                    arguments(List.of(talk5), Collections.emptyList()),
                    arguments(List.of(talk6), Collections.emptyList()),
                    arguments(List.of(talk7), Collections.emptyList()),
                    arguments(List.of(talk0, talk1), Collections.emptyList()),
                    arguments(List.of(talk0, talk1, talk2), Collections.emptyList()),
                    arguments(List.of(talk0, talk1, talk2, talk3), Collections.emptyList()),
                    arguments(List.of(talk8), List.of(talk8)),
                    arguments(List.of(talk0, talk8), List.of(talk8)),
                    arguments(List.of(talk0, talk1, talk8), List.of(talk8)),
                    arguments(List.of(talk0, talk1, talk2, talk8), List.of(talk8)),
                    arguments(List.of(talk0, talk1, talk2, talk3, talk8), List.of(talk8))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void deleteOpeningAndClosingTalks(List<Talk> talks, List<Talk> expected) {
            assertEquals(expected, ConferenceDataLoader.deleteOpeningAndClosingTalks(talks));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("deleteTalkDuplicates method tests")
    class DeleteTalkDuplicatesTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setId(0);
            talk0.setName(List.of(new LocaleItem("ru", "name0")));
            talk0.setTalkDay(1L);
            talk0.setTrack(1L);
            talk0.setTrackTime(LocalTime.of(10, 0));

            Talk talk1 = new Talk();
            talk1.setId(1);
            talk1.setName(List.of(new LocaleItem("ru", "name0")));
            talk1.setTalkDay(2L);

            Talk talk2 = new Talk();
            talk2.setId(2);
            talk2.setName(List.of(new LocaleItem("ru", "name0")));
            talk2.setTalkDay(1L);
            talk2.setTrack(2L);

            Talk talk3 = new Talk();
            talk3.setId(3);
            talk3.setName(List.of(new LocaleItem("ru", "name0")));
            talk3.setTalkDay(1L);
            talk3.setTrack(1L);
            talk3.setTrackTime(LocalTime.of(10, 30));

            Talk talk4 = new Talk();
            talk4.setId(4);
            talk4.setName(List.of(new LocaleItem("ru", "name0")));
            talk4.setTalkDay(1L);
            talk4.setTrack(1L);
            talk4.setTrackTime(LocalTime.of(11, 0));

            Talk talk5 = new Talk();
            talk5.setId(5);
            talk5.setName(List.of(new LocaleItem("ru", "name5")));

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyList()),
                    arguments(List.of(talk0), List.of(talk0)),
                    arguments(List.of(talk1, talk0), List.of(talk0)),
                    arguments(List.of(talk0, talk1), List.of(talk0)),
                    arguments(List.of(talk1, talk2, talk0), List.of(talk0)),
                    arguments(List.of(talk1, talk0, talk2), List.of(talk0)),
                    arguments(List.of(talk1, talk2, talk3, talk0), List.of(talk0)),
                    arguments(List.of(talk0, talk5), List.of(talk0, talk5)),
                    arguments(List.of(talk1, talk2, talk3, talk0, talk4), List.of(talk0)),
                    arguments(List.of(talk1, talk2, talk3, talk0, talk5), List.of(talk0, talk5))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void deleteTalkDuplicates(List<Talk> talks, List<Talk> expected) {
            assertEquals(expected, ConferenceDataLoader.deleteTalkDuplicates(talks));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getInvalidCompanyNames method tests")
    class GetInvalidCompanyNamesTest {
        private Stream<Arguments> data() {
            final String COMPANY_NAME0 = "EPAM Systems";
            final String COMPANY_NAME1 = "CROC";

            final String SYNONYM0 = "EPAM";
            final String SYNONYM1 = "KROK";
            final String SYNONYM2 = "INVALID0";
            final String SYNONYM3 = "INVALID1";
            final String SYNONYM4 = "INVALID3";

            CompanySynonyms companySynonyms0 = new CompanySynonyms();
            companySynonyms0.setName(COMPANY_NAME0);
            companySynonyms0.setSynonyms(List.of(SYNONYM0));

            CompanySynonyms companySynonyms1 = new CompanySynonyms();
            companySynonyms1.setName(COMPANY_NAME1);
            companySynonyms1.setSynonyms(List.of(SYNONYM1));

            CompanySynonyms companySynonyms2 = new CompanySynonyms();
            companySynonyms2.setName(null);
            companySynonyms2.setSynonyms(List.of(SYNONYM2, SYNONYM3));

            CompanySynonyms companySynonyms3 = new CompanySynonyms();
            companySynonyms3.setName("");
            companySynonyms3.setSynonyms(List.of(SYNONYM4));

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptySet()),
                    arguments(List.of(companySynonyms0, companySynonyms1), Collections.emptySet()),
                    arguments(List.of(companySynonyms2), Set.of(SYNONYM2, SYNONYM3)),
                    arguments(List.of(companySynonyms3), Set.of(SYNONYM4)),
                    arguments(List.of(companySynonyms2, companySynonyms3), Set.of(SYNONYM2, SYNONYM3, SYNONYM4)),
                    arguments(List.of(companySynonyms0, companySynonyms1, companySynonyms2, companySynonyms3), Set.of(SYNONYM2, SYNONYM3, SYNONYM4))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getInvalidCompanyNames(List<CompanySynonyms> companySynonymsList, Set<String> expected) {
            assertEquals(expected, ConferenceDataLoader.getInvalidCompanyNames(companySynonymsList));
        }
    }

    @Test
    void deleteInvalidSpeakerCompanies() {
        final String NAME0 = "Name0";
        final String NAME1 = "Invalid";

        Company company0 = new Company();
        company0.setId(0L);
        company0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), NAME0)));

        Company company1 = new Company();
        company1.setId(1L);
        company1.setName(Collections.emptyList());

        Company company2 = new Company();
        company2.setId(2L);

        Company company3 = new Company();
        company3.setId(3L);
        company3.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), NAME1)));

        Speaker speaker0 = new Speaker();
        speaker0.setId(0L);
        speaker0.setCompanyIds(new ArrayList<>());
        speaker0.setCompanies(new ArrayList<>());

        Speaker speaker1 = new Speaker();
        speaker1.setId(1L);
        speaker1.setCompanyIds(new ArrayList<>(List.of(0L)));
        speaker1.setCompanies(new ArrayList<>(List.of(company0)));

        Speaker speaker2 = new Speaker();
        speaker2.setId(2L);
        speaker2.setCompanyIds(new ArrayList<>(List.of(0L, 1L, 2L)));
        speaker2.setCompanies(new ArrayList<>(List.of(company0, company1, company2)));

        Speaker speaker3 = new Speaker();
        speaker3.setId(3L);
        speaker3.setCompanyIds(new ArrayList<>(List.of(1L, 2L)));
        speaker3.setCompanies(new ArrayList<>(List.of(company1, company2)));

        Speaker speaker4 = new Speaker();
        speaker4.setId(4L);
        speaker4.setCompanyIds(new ArrayList<>(List.of(3L)));
        speaker4.setCompanies(new ArrayList<>(List.of(company3)));

        List<Speaker> speakers = List.of(speaker0, speaker1, speaker2, speaker3, speaker4);

        Set<String> invalidCompanyNames = Set.of(NAME1);

        Predicate<Company> invalidCompanyPredicate = c -> {
            if ((c.getName() == null) || c.getName().isEmpty()) {
                return true;
            } else {
                return c.getName().stream()
                        .map(LocaleItem::getText)
                        .anyMatch(invalidCompanyNames::contains);
            }
        };
        List<Company> oldCompanies = speakers.stream()
                .flatMap(s -> s.getCompanies().stream())
                .toList();
        long oldTotalCompanyCount = oldCompanies.size();
        long oldInvalidCompanyCount = oldCompanies.stream()
                .filter(invalidCompanyPredicate)
                .count();
        long oldValidCompanyCount = oldTotalCompanyCount - oldInvalidCompanyCount;

        assertTrue(oldTotalCompanyCount > 0);
        assertTrue(oldInvalidCompanyCount > 0);

        ConferenceDataLoader.deleteInvalidSpeakerCompanies(speakers, invalidCompanyNames);

        List<Company> newCompanies = speakers.stream()
                .flatMap(s -> s.getCompanies().stream())
                .toList();
        long newTotalCompanyCount = newCompanies.size();
        long newInvalidCompanyCount = newCompanies.stream()
                .filter(invalidCompanyPredicate)
                .count();
        long newValidCompanyCount = newTotalCompanyCount - newInvalidCompanyCount;

        assertTrue(newTotalCompanyCount > 0);
        assertEquals(0, newInvalidCompanyCount);
        assertEquals(oldValidCompanyCount, newValidCompanyCount);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("splitCompanyGroupNames method tests")
    class SplitCompanyGroupNamesTest {
        private Stream<Arguments> data() {
            final String NAME0 = "Name0";
            final String NAME1 = "Name1";
            final String NAME2 = "Name0, Name1";

            Company company0 = new Company();
            company0.setId(0);
            company0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), NAME0)));

            Company company1 = new Company();
            company1.setId(1);
            company1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), NAME1)));

            Company company2 = new Company();
            company2.setId(2);
            company2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), NAME2)));

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setCompanies(List.of(company0));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setCompanies(List.of(company1));

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);
            speaker2.setCompanies(List.of(company2));

            CompanyGroup companyGroup0 = new CompanyGroup();
            companyGroup0.setName(NAME2);
            companyGroup0.setItems(List.of(NAME0, NAME1));

            List<CompanyGroup> companyGroups0 = Collections.emptyList();
            List<CompanyGroup> companyGroups1 = List.of(companyGroup0);

            return Stream.of(
                    arguments(List.of(speaker0, speaker1, speaker2), companyGroups0, new AtomicLong(0)),
                    arguments(List.of(speaker0, speaker1, speaker2), companyGroups1, new AtomicLong(0))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void splitCompanyGroupNames(List<Speaker> speakers, List<CompanyGroup> companyGroups, AtomicLong firstCompanyId) {
            assertDoesNotThrow(() -> ConferenceDataLoader.splitCompanyGroupNames(speakers, companyGroups, firstCompanyId));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getTalkSpeakers method tests")
    class GetTalkSpeakersTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Talk talk0 = new Talk();
            talk0.setSpeakers(List.of(speaker0));

            Talk talk1 = new Talk();
            talk1.setSpeakers(List.of(speaker0, speaker1));

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyList()),
                    arguments(List.of(talk0), List.of(speaker0)),
                    arguments(List.of(talk1), List.of(speaker0, speaker1)),
                    arguments(List.of(talk0, talk1), List.of(speaker0, speaker1))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getTalkSpeakers(List<Talk> talks, List<Speaker> expected) {
            assertEquals(expected, ConferenceDataLoader.getTalkSpeakers(talks));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakerCompanies method tests")
    class GetSpeakerCompaniesTest {
        private Stream<Arguments> data() {
            Company company0 = new Company(0, Collections.emptyList());
            Company company1 = new Company(1, Collections.emptyList());
            Company company2 = new Company(2, Collections.emptyList());

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setCompanies(List.of(company0, company1));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setCompanies(List.of(company0, company2));

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyList()),
                    arguments(List.of(speaker0), List.of(company0, company1)),
                    arguments(List.of(speaker1), List.of(company0, company2)),
                    arguments(List.of(speaker0, speaker1), List.of(company0, company1, company2))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakerCompanies(List<Speaker> speakers, List<Company> expected) {
            List<Company> actual = ConferenceDataLoader.getSpeakerCompanies(speakers);

            assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getResourceLowerNameCompanyMap method tests")
    class GetResourceLowerNameCompanyMapTest {
        private Stream<Arguments> data() {
            Company company0 = new Company(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company0")));
            Company company1 = new Company(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company1")));
            Company company2 = new Company(2, List.of(new LocaleItem(Language.RUSSIAN.getCode(), "КОМПАНИЯ2")));
            Company company3 = new Company(3, List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), "company3"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Компания3")
            ));

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyMap()),
                    arguments(List.of(company0), Map.of("company0", company0)),
                    arguments(List.of(company1), Map.of("company1", company1)),
                    arguments(List.of(company0, company1), Map.of("company0", company0, "company1", company1)),
                    arguments(List.of(company2), Map.of("компания2", company2)),
                    arguments(List.of(company3), Map.of("company3", company3, "компания3", company3)),
                    arguments(List.of(company2, company3), Map.of("компания2", company2, "company3", company3, "компания3", company3))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getResourceLowerNameCompanyMap(List<Company> companies, Map<String, Company> expected) {
            assertEquals(expected, ConferenceDataLoader.getResourceLowerNameCompanyMap(companies));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("addLowerSynonymsToCompanyMap method tests")
    class AddLowerSynonymsToCompanyMapTest {
        private Stream<Arguments> data() {
            final String COMPANY_NAME0 = "EPAM Systems";
            final String COMPANY_NAME1 = "CROC";

            final String SYNONYM0 = "EPAM";
            final String SYNONYM1 = "KROK";

            Company company0 = new Company(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), COMPANY_NAME0)));
            Company company1 = new Company(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), COMPANY_NAME1)));

            CompanySynonyms companySynonyms0 = new CompanySynonyms();
            companySynonyms0.setName(COMPANY_NAME0);
            companySynonyms0.setSynonyms(List.of(SYNONYM0));

            CompanySynonyms companySynonyms1 = new CompanySynonyms();
            companySynonyms1.setName(COMPANY_NAME1);
            companySynonyms1.setSynonyms(List.of(SYNONYM1));

            CompanySynonyms companySynonyms2 = new CompanySynonyms();
            companySynonyms2.setName(COMPANY_NAME1);
            companySynonyms2.setSynonyms(List.of(COMPANY_NAME1));

            CompanySynonyms companySynonyms3 = new CompanySynonyms();
            companySynonyms3.setName(null);
            companySynonyms3.setSynonyms(List.of(SYNONYM1));

            CompanySynonyms companySynonyms4 = new CompanySynonyms();
            companySynonyms4.setName("");
            companySynonyms4.setSynonyms(List.of(SYNONYM1));

            Map<String, Company> companyMap0 = new HashMap<>();
            companyMap0.put(COMPANY_NAME0.toLowerCase(), company0);
            companyMap0.put(COMPANY_NAME1.toLowerCase(), company1);

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyMap(), null, Collections.emptyMap()),
                    arguments(List.of(companySynonyms0), new HashMap<>(companyMap0), null, Map.of(
                            COMPANY_NAME0.toLowerCase(), company0,
                            COMPANY_NAME1.toLowerCase(), company1,
                            SYNONYM0.toLowerCase(), company0)),
                    arguments(List.of(companySynonyms1), new HashMap<>(companyMap0), null, Map.of(
                            COMPANY_NAME0.toLowerCase(), company0,
                            COMPANY_NAME1.toLowerCase(), company1,
                            SYNONYM1.toLowerCase(), company1)),
                    arguments(List.of(companySynonyms0, companySynonyms1), new HashMap<>(companyMap0), null, Map.of(
                            COMPANY_NAME0.toLowerCase(), company0,
                            COMPANY_NAME1.toLowerCase(), company1,
                            SYNONYM1.toLowerCase(), company1,
                            SYNONYM0.toLowerCase(), company0)),
                    arguments(List.of(companySynonyms0), Collections.emptyMap(), NullPointerException.class, null),
                    arguments(List.of(companySynonyms2), new HashMap<>(companyMap0), IllegalArgumentException.class, null),
                    arguments(List.of(companySynonyms0, companySynonyms3), new HashMap<>(companyMap0), null, Map.of(
                            COMPANY_NAME0.toLowerCase(), company0,
                            COMPANY_NAME1.toLowerCase(), company1,
                            SYNONYM0.toLowerCase(), company0)),
                    arguments(List.of(companySynonyms0, companySynonyms4), new HashMap<>(companyMap0), null, Map.of(
                            COMPANY_NAME0.toLowerCase(), company0,
                            COMPANY_NAME1.toLowerCase(), company1,
                            SYNONYM0.toLowerCase(), company0)),
                    arguments(List.of(companySynonyms0, companySynonyms3, companySynonyms4), new HashMap<>(companyMap0), null, Map.of(
                            COMPANY_NAME0.toLowerCase(), company0,
                            COMPANY_NAME1.toLowerCase(), company1,
                            SYNONYM0.toLowerCase(), company0))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void addLowerSynonymsToCompanyMap(List<CompanySynonyms> companySynonymsList, Map<String, Company> companyMap,
                                          Class<? extends Throwable> expectedException, Map<String, Company> expectedValue) {
            if (expectedException == null) {
                ConferenceDataLoader.addLowerSynonymsToCompanyMap(companySynonymsList, companyMap);

                assertEquals(expectedValue, companyMap);
            } else {
                assertThrows(expectedException, () -> ConferenceDataLoader.addLowerSynonymsToCompanyMap(companySynonymsList, companyMap));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getCompanyLoadResult method tests")
    class GetCompanyLoadResultTest {
        Company createCompany(long id, String name) {
            return new Company(id, List.of(new LocaleItem(Language.ENGLISH.getCode(), name)));
        }

        private Stream<Arguments> data() {
            final String COMPANY_NAME0 = "EPAM Systems";
            final String COMPANY_NAME1 = "CROC";

            Map<String, Company> resourceCompanyMap0 = new HashMap<>();
            resourceCompanyMap0.put(COMPANY_NAME0.toLowerCase(), createCompany(0, COMPANY_NAME0));

            LoadResult<List<Company>> loadResult0 = new LoadResult<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            LoadResult<List<Company>> loadResult1 = new LoadResult<>(
                    Collections.emptyList(),
                    List.of(createCompany(1, COMPANY_NAME1)),
                    Collections.emptyList()
            );

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyMap(), new AtomicLong(-1), loadResult0),
                    arguments(List.of(
                                    createCompany(-1, COMPANY_NAME0)),
                            resourceCompanyMap0, new AtomicLong(0), loadResult0),
                    arguments(List.of(
                                    createCompany(-2, COMPANY_NAME1)),
                            resourceCompanyMap0, new AtomicLong(0), loadResult1),
                    arguments(List.of(
                                    createCompany(-1, COMPANY_NAME0),
                                    createCompany(-2, COMPANY_NAME1)),
                            resourceCompanyMap0, new AtomicLong(0), loadResult1),
                    arguments(List.of(
                                    createCompany(-1, COMPANY_NAME0),
                                    createCompany(-2, COMPANY_NAME1),
                                    createCompany(-3, COMPANY_NAME1)),
                            resourceCompanyMap0, new AtomicLong(0), loadResult1),
                    arguments(List.of(
                                    createCompany(-1, COMPANY_NAME0),
                                    createCompany(-2, COMPANY_NAME1),
                                    new Company(-3, Collections.emptyList())),
                            resourceCompanyMap0, new AtomicLong(0), loadResult1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void getCompanyLoadResult(List<Company> companies, Map<String, Company> resourceCompanyMap, AtomicLong lastCompanyId,
                                  LoadResult<List<Company>> expected) {
            try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                mockedStatic.when(() -> ConferenceDataLoader.getCompanyLoadResult(Mockito.anyList(), Mockito.anyMap(), Mockito.any(AtomicLong.class)))
                        .thenCallRealMethod();
                mockedStatic.when(() -> ConferenceDataLoader.findResourceCompany(Mockito.any(Company.class), Mockito.anyMap()))
                        .thenAnswer(
                                (Answer<Company>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    Company company = (Company) args[0];
                                    Map<String, Company> rcp = (Map<String, Company>) args[1];

                                    return company.getName().stream()
                                            .map(localItem -> rcp.get(localItem.getText().toLowerCase()))
                                            .filter(Objects::nonNull)
                                            .findFirst()
                                            .orElse(null);
                                }
                        );

                assertEquals(expected, ConferenceDataLoader.getCompanyLoadResult(companies, resourceCompanyMap, lastCompanyId));
            }
        }
    }

    @Test
    void fillCompanyIds() {
        Company company0 = new Company();
        company0.setId(0);

        Company company1 = new Company();
        company1.setId(1);

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setCompanies(List.of(company0));

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setCompanies(List.of(company0, company1));

        Speaker speaker2 = new Speaker();
        speaker1.setId(2);

        List<Long> expectedCompanyIds0 = List.of(0L);
        List<Long> expectedCompanyIds1 = List.of(0L, 1L);

        assertTrue(speaker0.getCompanyIds().isEmpty());
        assertTrue(speaker1.getCompanyIds().isEmpty());
        assertTrue(speaker2.getCompanyIds().isEmpty());

        ConferenceDataLoader.fillCompanyIds(List.of(speaker0, speaker1));

        List<Long> actualCompanyIds0 = speaker0.getCompanyIds();
        List<Long> actualCompanyIds1 = speaker1.getCompanyIds();

        assertTrue(expectedCompanyIds0.containsAll(actualCompanyIds0) && actualCompanyIds0.containsAll(expectedCompanyIds0));
        assertTrue(expectedCompanyIds1.containsAll(actualCompanyIds1) && actualCompanyIds1.containsAll(expectedCompanyIds1));
        assertTrue(speaker2.getCompanyIds().isEmpty());
    }

    @Test
    void getResourceNameCompanySpeakerMap() {
        final String SPEAKER_NAME0 = "Name0";
        final String SPEAKER_NAME1 = "Name1";
        final String SPEAKER_NAME2 = "Name2";

        final String COMPANY_NAME0 = "EPAM Systems";
        final String COMPANY_NAME1 = "CROC";

        Company company0 = new Company(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), COMPANY_NAME0)));
        Company company1 = new Company(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), COMPANY_NAME1)));

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), SPEAKER_NAME0)));
        speaker0.setCompanies(List.of(company0));

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), SPEAKER_NAME1)));
        speaker1.setCompanies(List.of(company0, company1));

        Speaker speaker2 = new Speaker();
        speaker2.setId(2);
        speaker2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), SPEAKER_NAME2)));

        Map<NameCompany, Speaker> expected = new HashMap<>();
        expected.put(new NameCompany(SPEAKER_NAME0, company0), speaker0);
        expected.put(new NameCompany(SPEAKER_NAME1, company0), speaker1);
        expected.put(new NameCompany(SPEAKER_NAME1, company1), speaker1);

        assertEquals(expected, ConferenceDataLoader.getResourceNameCompanySpeakerMap(List.of(speaker0, speaker1, speaker2)));
    }

    @Test
    void getResourceNameSpeakersMap() {
        final String SPEAKER_NAME0 = "Name0";
        final String SPEAKER_NAME1 = "Name1";

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), SPEAKER_NAME0)));

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), SPEAKER_NAME1)));

        Speaker speaker2 = new Speaker();
        speaker2.setId(2);
        speaker2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), SPEAKER_NAME1)));

        Map<String, Set<Speaker>> expected = new HashMap<>();
        expected.put(SPEAKER_NAME0, Set.of(speaker0));
        expected.put(SPEAKER_NAME1, Set.of(speaker1, speaker2));

        assertEquals(expected, ConferenceDataLoader.getResourceNameSpeakersMap(List.of(speaker0, speaker1, speaker2)));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakerLoadResult method tests")
    class GetSpeakerLoadResultTest {
        final String PHOTO_FILE_NAME0 = "0000.jpg";
        final String PHOTO_FILE_NAME1 = "0001.jpg";
        final String PHOTO_FILE_NAME2 = "http://valid.com/2.jpg";

        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setPhotoFileName(PHOTO_FILE_NAME0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setPhotoFileName(PHOTO_FILE_NAME1);

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);
            speaker2.setPhotoFileName(PHOTO_FILE_NAME2);

            SpeakerLoadMaps speakerLoadMaps = new SpeakerLoadMaps(
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyMap());

            SpeakerLoadResult speakerLoadResult0 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()),
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()));

            SpeakerLoadResult speakerLoadResult1 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            List.of(speaker2),
                            Collections.emptyList()),
                    new LoadResult<>(
                            Collections.emptyList(),
                            List.of(new UrlFilename(PHOTO_FILE_NAME2, "0000.jpg")),
                            Collections.emptyList()));

            SpeakerLoadResult speakerLoadResult2 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            List.of(speaker0)),
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            List.of(new UrlFilename(PHOTO_FILE_NAME0, PHOTO_FILE_NAME0))));

            SpeakerLoadResult speakerLoadResult3 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()),
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()));

            return Stream.of(
                    arguments(Collections.emptyList(), speakerLoadMaps, new AtomicLong(-1), speakerLoadResult0),
                    arguments(List.of(speaker2), speakerLoadMaps, new AtomicLong(-1), speakerLoadResult1),
                    arguments(List.of(speaker0), speakerLoadMaps, new AtomicLong(-1), speakerLoadResult2),
                    arguments(List.of(speaker1), speakerLoadMaps, new AtomicLong(-1), speakerLoadResult3)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakerLoadResult(List<Speaker> speakers, SpeakerLoadMaps speakerLoadMaps, AtomicLong lastSpeakerId,
                                  SpeakerLoadResult expected) throws IOException {
            try (MockedStatic<ContentfulUtils> contentfulUtilsMockedStatic = Mockito.mockStatic(ContentfulUtils.class);
                 MockedStatic<ConferenceDataLoader> conferenceDataLoaderMockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                contentfulUtilsMockedStatic.when(() -> ContentfulUtils.needPhotoUpdate(
                                Mockito.nullable(ZonedDateTime.class), Mockito.nullable(ZonedDateTime.class), Mockito.nullable(String.class), Mockito.nullable(String.class)))
                        .thenAnswer(
                                (Answer<Boolean>) invocation -> {
                                    Object[] args = invocation.getArguments();

                                    return PHOTO_FILE_NAME0.equals(args[3]);
                                }
                        );
                contentfulUtilsMockedStatic.when(() -> ContentfulUtils.needUpdate(Mockito.any(Speaker.class), Mockito.any(Speaker.class)))
                        .thenAnswer(
                                (Answer<Boolean>) invocation -> {
                                    Object[] args = invocation.getArguments();

                                    return ((((Speaker) args[0]).getId() == 0) && (((Speaker) args[1]).getId() == 0));
                                }
                        );
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getSpeakerLoadResult(Mockito.anyList(), Mockito.any(SpeakerLoadMaps.class), Mockito.any(AtomicLong.class)))
                        .thenCallRealMethod();
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.findResourceSpeaker(Mockito.any(Speaker.class), Mockito.any(SpeakerLoadMaps.class)))
                        .thenAnswer(
                                (Answer<Speaker>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    Speaker speaker = (Speaker) args[0];

                                    return ((speaker.getId() == 0) || (speaker.getId() == 1)) ? speaker : null;
                                }
                        );

                assertEquals(expected, ConferenceDataLoader.getSpeakerLoadResult(speakers, speakerLoadMaps, lastSpeakerId));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("fillStringAttributeValue method tests")
    class FillStringAttributeValueTest {
        private Speaker createSpeaker(String twitter) {
            Speaker speaker = new Speaker();
            speaker.setTwitter(twitter);

            return speaker;
        }

        private Stream<Arguments> data() {
            final String RESOURCE_SPEAKER_TWITTER = "resourceSpeakerTwitter";
            final String TARGET_SPEAKER_TWITTER = "targetSpeakerTwitter";

            Speaker targetSpeaker0 = createSpeaker(null);
            Supplier<String> targetSupplier0 = targetSpeaker0::getTwitter;
            Consumer<String> targetConsumer0 = targetSpeaker0::setTwitter;
            Speaker resourceSpeaker0 = createSpeaker(null);
            Supplier<String> resourceSupplier0 = resourceSpeaker0::getTwitter;

            Speaker targetSpeaker1 = createSpeaker("");
            Supplier<String> targetSupplier1 = targetSpeaker1::getTwitter;
            Consumer<String> targetConsumer1 = targetSpeaker1::setTwitter;
            Speaker resourceSpeaker1 = createSpeaker(null);
            Supplier<String> resourceSupplier1 = resourceSpeaker1::getTwitter;

            Speaker targetSpeaker2 = createSpeaker(TARGET_SPEAKER_TWITTER);
            Supplier<String> targetSupplier2 = targetSpeaker2::getTwitter;
            Consumer<String> targetConsumer2 = targetSpeaker2::setTwitter;
            Speaker resourceSpeaker2 = createSpeaker(null);
            Supplier<String> resourceSupplier2 = resourceSpeaker2::getTwitter;

            Speaker targetSpeaker3 = createSpeaker(null);
            Supplier<String> targetSupplier3 = targetSpeaker3::getTwitter;
            Consumer<String> targetConsumer3 = targetSpeaker3::setTwitter;
            Speaker resourceSpeaker3 = createSpeaker("");
            Supplier<String> resourceSupplier3 = resourceSpeaker3::getTwitter;

            Speaker targetSpeaker4 = createSpeaker("");
            Supplier<String> targetSupplier4 = targetSpeaker4::getTwitter;
            Consumer<String> targetConsumer4 = targetSpeaker4::setTwitter;
            Speaker resourceSpeaker4 = createSpeaker("");
            Supplier<String> resourceSupplier4 = resourceSpeaker4::getTwitter;

            Speaker targetSpeaker5 = createSpeaker(TARGET_SPEAKER_TWITTER);
            Supplier<String> targetSupplier5 = targetSpeaker5::getTwitter;
            Consumer<String> targetConsumer5 = targetSpeaker5::setTwitter;
            Speaker resourceSpeaker5 = createSpeaker("");
            Supplier<String> resourceSupplier5 = resourceSpeaker5::getTwitter;

            Speaker targetSpeaker6 = createSpeaker(null);
            Supplier<String> targetSupplier6 = targetSpeaker6::getTwitter;
            Consumer<String> targetConsumer6 = targetSpeaker6::setTwitter;
            Speaker resourceSpeaker6 = createSpeaker(RESOURCE_SPEAKER_TWITTER);
            Supplier<String> resourceSupplier6 = resourceSpeaker6::getTwitter;

            Speaker targetSpeaker7 = createSpeaker("");
            Supplier<String> targetSupplier7 = targetSpeaker7::getTwitter;
            Consumer<String> targetConsumer7 = targetSpeaker7::setTwitter;
            Speaker resourceSpeaker7 = createSpeaker(RESOURCE_SPEAKER_TWITTER);
            Supplier<String> resourceSupplier7 = resourceSpeaker7::getTwitter;

            Speaker targetSpeaker8 = createSpeaker(TARGET_SPEAKER_TWITTER);
            Supplier<String> targetSupplier8 = targetSpeaker8::getTwitter;
            Consumer<String> targetConsumer8 = targetSpeaker8::setTwitter;
            Speaker resourceSpeaker8 = createSpeaker(RESOURCE_SPEAKER_TWITTER);
            Supplier<String> resourceSupplier8 = resourceSpeaker8::getTwitter;

            return Stream.of(
                    arguments(resourceSupplier0, targetSupplier0, targetConsumer0, null),
                    arguments(resourceSupplier1, targetSupplier1, targetConsumer1, ""),
                    arguments(resourceSupplier2, targetSupplier2, targetConsumer2, TARGET_SPEAKER_TWITTER),
                    arguments(resourceSupplier3, targetSupplier3, targetConsumer3, null),
                    arguments(resourceSupplier4, targetSupplier4, targetConsumer4, ""),
                    arguments(resourceSupplier5, targetSupplier5, targetConsumer5, TARGET_SPEAKER_TWITTER),
                    arguments(resourceSupplier6, targetSupplier6, targetConsumer6, RESOURCE_SPEAKER_TWITTER),
                    arguments(resourceSupplier7, targetSupplier7, targetConsumer7, RESOURCE_SPEAKER_TWITTER),
                    arguments(resourceSupplier8, targetSupplier8, targetConsumer8, TARGET_SPEAKER_TWITTER)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void fillStringAttributeValue(Supplier<String> resourceSupplier, Supplier<String> targetSupplier, Consumer<String> targetConsumer,
                                      String expected) {
            ConferenceDataLoader.fillStringAttributeValue(resourceSupplier, targetSupplier, targetConsumer);

            assertEquals(expected, targetSupplier.get());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("fillSpeakerJavaChampion method tests")
    class FillSpeakerJavaChampionTest {
        private Speaker createSpeaker(boolean javaChampion) {
            Speaker speaker = new Speaker();
            speaker.setJavaChampion(javaChampion);

            return speaker;
        }

        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(createSpeaker(false), createSpeaker(false), false),
                    arguments(createSpeaker(true), createSpeaker(false), true),
                    arguments(createSpeaker(false), createSpeaker(true), true),
                    arguments(createSpeaker(true), createSpeaker(true), true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void fillSpeakerJavaChampion(Speaker targetSpeaker, Speaker resourceSpeaker,
                                     boolean expected) {
            ConferenceDataLoader.fillSpeakerJavaChampion(targetSpeaker, resourceSpeaker);

            assertEquals(expected, targetSpeaker.isJavaChampion());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("fillSpeakerMvp method tests")
    class FillSpeakerMvpTest {
        private Speaker createSpeaker(boolean mvp, boolean mvpReconnect) {
            Speaker speaker = new Speaker();
            speaker.setMvp(mvp);
            speaker.setMvpReconnect(mvpReconnect);

            return speaker;
        }

        private Speaker createSpeaker0() {
            return createSpeaker(false, false);
        }

        private Speaker createSpeaker1() {
            return createSpeaker(false, true);
        }

        private Speaker createSpeaker2() {
            return createSpeaker(true, false);
        }

        private Speaker createSpeaker3() {
            return createSpeaker(true, true);
        }

        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(createSpeaker0(), createSpeaker0(), false, false),
                    arguments(createSpeaker1(), createSpeaker0(), false, true),
                    arguments(createSpeaker2(), createSpeaker0(), true, false),
                    arguments(createSpeaker3(), createSpeaker0(), false, true),
                    arguments(createSpeaker0(), createSpeaker1(), false, true),
                    arguments(createSpeaker1(), createSpeaker1(), false, true),
                    arguments(createSpeaker2(), createSpeaker1(), true, false),
                    arguments(createSpeaker3(), createSpeaker1(), false, true),
                    arguments(createSpeaker0(), createSpeaker2(), true, false),
                    arguments(createSpeaker1(), createSpeaker2(), false, true),
                    arguments(createSpeaker2(), createSpeaker2(), true, false),
                    arguments(createSpeaker3(), createSpeaker2(), false, true),
                    arguments(createSpeaker0(), createSpeaker3(), false, true),
                    arguments(createSpeaker1(), createSpeaker3(), false, true),
                    arguments(createSpeaker2(), createSpeaker3(), true, false),
                    arguments(createSpeaker3(), createSpeaker3(), false, true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void fillSpeakerMvp(Speaker targetSpeaker, Speaker resourceSpeaker,
                            boolean mvpExpected, boolean mvpReconnectExpected) {
            ConferenceDataLoader.fillSpeakerMvp(targetSpeaker, resourceSpeaker);

            assertEquals(mvpExpected, targetSpeaker.isMvp());
            assertEquals(mvpReconnectExpected, targetSpeaker.isMvpReconnect());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("fillUpdatedAt method tests")
    class FillUpdatedAtTest {
        private Speaker createSpeaker(ZonedDateTime photoUpdatedAt) {
            Speaker speaker = new Speaker();
            speaker.setPhotoUpdatedAt(photoUpdatedAt);

            return speaker;
        }

        private Stream<Arguments> data() {
            final ZonedDateTime NOW = ZonedDateTime.now();
            final ZonedDateTime YESTERDAY = NOW.minus(1, ChronoUnit.DAYS);

            return Stream.of(
                    arguments(createSpeaker(null), createSpeaker(null), null),
                    arguments(createSpeaker(null), createSpeaker(NOW), null),
                    arguments(createSpeaker(NOW), createSpeaker(null), NOW),
                    arguments(createSpeaker(NOW), createSpeaker(NOW), NOW),
                    arguments(createSpeaker(YESTERDAY), createSpeaker(NOW), NOW),
                    arguments(createSpeaker(NOW), createSpeaker(YESTERDAY), NOW)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void fillUpdatedAt(Speaker targetSpeaker, Speaker resourceSpeaker, ZonedDateTime expected) {
            ConferenceDataLoader.fillUpdatedAt(targetSpeaker, resourceSpeaker);

            assertEquals(expected, targetSpeaker.getPhotoUpdatedAt());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("fillSpeakerIds method tests")
    class FillSpeakerIdsTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);

            Speaker speaker3 = new Speaker();
            speaker3.setId(3);

            Talk talk0 = new Talk();
            talk0.setSpeakers(List.of(speaker0));

            Talk talk1 = new Talk();
            talk1.setSpeakers(List.of(speaker1, speaker2));

            Talk talk2 = new Talk();
            talk2.setSpeakers(List.of(speaker3));

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyList()),
                    arguments(List.of(talk0), List.of(List.of(0L))),
                    arguments(List.of(talk1), List.of(List.of(1L, 2L))),
                    arguments(List.of(talk1, talk2), List.of(List.of(1L, 2L), List.of(3L)))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void fillSpeakerIds(List<Talk> talks, List<List<Long>> expected) {
            ConferenceDataLoader.fillSpeakerIds(talks);

            for (int i = 0; i < talks.size(); i++) {
                assertEquals(
                        expected.get(i),
                        talks.get(i).getSpeakerIds());
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getTalkLoadResult method tests")
    class GetTalkLoadResultTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setId(0);

            Talk talk1 = new Talk();
            talk1.setId(1);
            talk1.setName(List.of(
                    new LocaleItem(Language.RUSSIAN.getCode(), "Наименование1"),
                    new LocaleItem(Language.ENGLISH.getCode(), "Name1")));

            Talk talk2 = new Talk();
            talk2.setId(2);
            talk2.setName(List.of(
                    new LocaleItem(Language.RUSSIAN.getCode(), "Наименование2"),
                    new LocaleItem(Language.ENGLISH.getCode(), "Name2")));

            Event resourceEvent = new Event();
            resourceEvent.setTalks(List.of(talk1, talk2));

            List<Event> resourceEvents = Collections.emptyList();

            LoadResult<List<Talk>> talkLoadResult0 = new LoadResult<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList());

            LoadResult<List<Talk>> talkLoadResult1 = new LoadResult<>(
                    Collections.emptyList(),
                    List.of(talk0),
                    Collections.emptyList());

            LoadResult<List<Talk>> talkLoadResult2 = new LoadResult<>(
                    List.of(talk1, talk2),
                    List.of(talk2),
                    Collections.emptyList());

            LoadResult<List<Talk>> talkLoadResult3 = new LoadResult<>(
                    List.of(talk1, talk2),
                    Collections.emptyList(),
                    List.of(talk0));

            return Stream.of(
                    arguments(Collections.emptyList(), null, resourceEvents, new AtomicLong(-1), talkLoadResult0),
                    arguments(List.of(talk0), null, resourceEvents, new AtomicLong(-1), talkLoadResult1),
                    arguments(List.of(talk2), resourceEvent, resourceEvents, new AtomicLong(-1), talkLoadResult2),
                    arguments(List.of(talk0, talk1), resourceEvent, resourceEvents, new AtomicLong(-1), talkLoadResult3)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getTalkLoadResult(List<Talk> talks, Event resourceEvent, List<Event> resourceEvents,
                               AtomicLong lasTalksId, LoadResult<List<Talk>> expected) {
            try (MockedStatic<ContentfulUtils> contentfulUtilsMockedStatic = Mockito.mockStatic(ContentfulUtils.class);
                 MockedStatic<ConferenceDataLoader> conferenceDataLoaderMockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                contentfulUtilsMockedStatic.when(() -> ContentfulUtils.needUpdate(Mockito.any(Talk.class), Mockito.any(Talk.class)))
                        .thenAnswer(
                                (Answer<Boolean>) invocation -> {
                                    Object[] args = invocation.getArguments();

                                    return ((((Talk) args[0]).getId() == 0) && (((Talk) args[1]).getId() == 0));
                                }
                        );
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.getTalkLoadResult(
                                Mockito.anyList(), Mockito.nullable(Event.class), Mockito.anyList(), Mockito.any(AtomicLong.class)))
                        .thenCallRealMethod();
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.findResourceTalk(Mockito.any(Talk.class), Mockito.anyMap(), Mockito.anyMap()))
                        .thenAnswer(
                                (Answer<Talk>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    Talk talk = (Talk) args[0];

                                    return ((talk.getId() == 0) || (talk.getId() == 1)) ? talk : null;
                                }
                        );
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.needDeleteTalk(
                                Mockito.anyList(), Mockito.any(Talk.class), Mockito.anyList(), Mockito.any(Event.class)))
                        .thenReturn(true);

                assertEquals(expected, ConferenceDataLoader.getTalkLoadResult(talks, resourceEvent, resourceEvents, lasTalksId));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("needDeleteTalk method tests")
    class NeedDeleteTalkTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setId(0);

            Talk talk1 = new Talk();
            talk1.setId(1);

            EventType eventType0 = new EventType();
            eventType0.setId(0);

            EventType eventType1 = new EventType();
            eventType1.setId(1);

            Event event0 = new Event();
            event0.setId(0);
            event0.setEventType(eventType0);
            event0.setStartDate(LocalDate.of(2020, 10, 3));
            event0.setTalks(List.of(talk0));

            Event event1 = new Event();
            event1.setId(1);
            event1.setEventType(eventType1);
            event1.setStartDate(LocalDate.of(2020, 10, 3));

            return Stream.of(
                    arguments(List.of(talk0), talk0, Collections.emptyList(), null, false),
                    arguments(Collections.emptyList(), talk0, List.of(event0), event0, true),
                    arguments(Collections.emptyList(), talk0, List.of(event0), event1, false)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void needDeleteTalk(List<Talk> talks, Talk resourceTalk, List<Event> resourceEvents, Event resourceEvent,
                            boolean expected) {
            try (MockedStatic<LocalizationUtils> mockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
                mockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                        .thenReturn("");

                assertEquals(expected, ConferenceDataLoader.needDeleteTalk(talks, resourceTalk, resourceEvents, resourceEvent));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getPlaceLoadResult method tests")
    class GetPlaceLoadResultTest {
        private Stream<Arguments> data() {
            Place place0 = new Place();
            place0.setId(0);

            Place place1 = new Place();
            place1.setId(1);

            LoadResult<Place> placeLoadResult0 = new LoadResult<>(
                    null,
                    place0,
                    null);

            LoadResult<Place> placeLoadResult1 = new LoadResult<>(
                    null,
                    null,
                    place0);

            LoadResult<Place> placeLoadResult2 = new LoadResult<>(
                    null,
                    null,
                    null);

            return Stream.of(
                    arguments(place0, null, new AtomicLong(-1), placeLoadResult0),
                    arguments(place0, place0, new AtomicLong(-1), placeLoadResult1),
                    arguments(place0, place1, new AtomicLong(-1), placeLoadResult2)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getPlaceLoadResult(Place place, Place resourcePlace, AtomicLong lastPlaceId, LoadResult<Place> expected) {
            try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
                mockedStatic.when(() -> ContentfulUtils.needUpdate(Mockito.any(Place.class), Mockito.any(Place.class)))
                        .thenAnswer(
                                (Answer<Boolean>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    Place a = (Place) args[0];
                                    Place b = (Place) args[1];

                                    return ((a.getId() == 0) && (b.getId() == 0));
                                }
                        );

                assertEquals(expected, ConferenceDataLoader.getPlaceLoadResult(place, resourcePlace, lastPlaceId));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getEventLoadResult method tests")
    class GetEventLoadResultTest {
        private Stream<Arguments> data() {
            Event event0 = new Event();
            event0.setId(0);

            Event event1 = new Event();
            event1.setId(1);

            LoadResult<Event> eventLoadResult0 = new LoadResult<>(
                    null,
                    event0,
                    null);

            LoadResult<Event> eventLoadResult1 = new LoadResult<>(
                    null,
                    null,
                    event0);

            LoadResult<Event> eventLoadResult2 = new LoadResult<>(
                    null,
                    null,
                    null);

            return Stream.of(
                    arguments(event0, null, eventLoadResult0),
                    arguments(event0, event0, eventLoadResult1),
                    arguments(event0, event1, eventLoadResult2)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getEventLoadResult(Event event, Event resourceEvent, LoadResult<Event> expected) {
            try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
                mockedStatic.when(() -> ContentfulUtils.needUpdate(Mockito.any(Event.class), Mockito.any(Event.class)))
                        .thenAnswer(
                                (Answer<Boolean>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    Event a = (Event) args[0];
                                    Event b = (Event) args[1];

                                    return ((a.getId() == 0) && (b.getId() == 0));
                                }
                        );

                assertEquals(expected, ConferenceDataLoader.getEventLoadResult(event, resourceEvent));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("fillEventTimeZone method tests")
    class FillEventTimeZoneTest {
        Event createEvent(String timeZone) {
            Event event = new Event();

            if (timeZone != null) {
                event.setTimeZone(timeZone);
            }

            return event;
        }

        private Stream<Arguments> data() {
            final String TIME_ZONE0 = "Europe/Moscow";
            final String TIME_ZONE1 = "Asia/Novosibirsk";

            return Stream.of(
                    arguments(createEvent(null), createEvent(null), null),
                    arguments(createEvent(null), createEvent(""), null),
                    arguments(createEvent(null), createEvent(TIME_ZONE0), TIME_ZONE0),

                    arguments(createEvent(""), createEvent(null), ""),
                    arguments(createEvent(""), createEvent(""), ""),
                    arguments(createEvent(""), createEvent(TIME_ZONE0), TIME_ZONE0),

                    arguments(createEvent(TIME_ZONE1), createEvent(null), TIME_ZONE1),
                    arguments(createEvent(TIME_ZONE1), createEvent(""), TIME_ZONE1),
                    arguments(createEvent(TIME_ZONE1), createEvent(TIME_ZONE0), TIME_ZONE1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void fillEventTimeZone(Event targetEvent, Event resourceEvent, String expected) {
            ConferenceDataLoader.fillEventTimeZone(targetEvent, resourceEvent);

            assertEquals(expected, targetEvent.getTimeZone());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("saveFiles method tests")
    class SaveFilesTest {
        private Stream<Arguments> data() {
            Company company0 = new Company();
            Speaker speaker0 = new Speaker();
            UrlFilename urlFilename0 = new UrlFilename("url0", "filename0");
            Talk talk0 = new Talk();
            Place place0 = new Place();
            Event event0 = new Event();

            LoadResult<List<Company>> companyLoadResult0 = new LoadResult<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList());

            LoadResult<List<Company>> companyLoadResult1 = new LoadResult<>(
                    Collections.emptyList(),
                    List.of(company0),
                    Collections.emptyList());

            SpeakerLoadResult speakerLoadResult0 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()),
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()));

            SpeakerLoadResult speakerLoadResult1 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            List.of(speaker0),
                            Collections.emptyList()),
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()));

            SpeakerLoadResult speakerLoadResult2 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            List.of(speaker0)),
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()));

            SpeakerLoadResult speakerLoadResult3 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()),
                    new LoadResult<>(
                            Collections.emptyList(),
                            List.of(urlFilename0),
                            Collections.emptyList()));

            SpeakerLoadResult speakerLoadResult4 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()),
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            List.of(urlFilename0)));

            LoadResult<List<Talk>> talkLoadResult0 = new LoadResult<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList());

            LoadResult<List<Talk>> talkLoadResult1 = new LoadResult<>(
                    List.of(talk0),
                    Collections.emptyList(),
                    Collections.emptyList());

            LoadResult<List<Talk>> talkLoadResult2 = new LoadResult<>(
                    Collections.emptyList(),
                    List.of(talk0),
                    Collections.emptyList());

            LoadResult<List<Talk>> talkLoadResult3 = new LoadResult<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    List.of(talk0));

            LoadResult<Place> placeLoadResult0 = new LoadResult<>(
                    null,
                    null,
                    null);

            LoadResult<Place> placeLoadResult1 = new LoadResult<>(
                    null,
                    place0,
                    null);

            LoadResult<Place> placeLoadResult2 = new LoadResult<>(
                    null,
                    null,
                    place0);

            LoadResult<Event> eventLoadResult0 = new LoadResult<>(
                    null,
                    null,
                    null);

            LoadResult<Event> eventLoadResult1 = new LoadResult<>(
                    null,
                    event0,
                    null);

            LoadResult<Event> eventLoadResult2 = new LoadResult<>(
                    null,
                    null,
                    event0);

            List<Arguments> argumentsList = new ArrayList<>();

            for (LoadResult<Event> eventLoadResult : List.of(eventLoadResult0, eventLoadResult1, eventLoadResult2)) {
                for (LoadResult<Place> placeLoadResult : List.of(placeLoadResult0, placeLoadResult1, placeLoadResult2)) {
                    for (LoadResult<List<Talk>> talkLoadResult : List.of(talkLoadResult0, talkLoadResult1, talkLoadResult2, talkLoadResult3)) {
                        for (SpeakerLoadResult speakerLoadResult : List.of(speakerLoadResult0, speakerLoadResult1, speakerLoadResult2, speakerLoadResult3, speakerLoadResult4)) {
                            for (LoadResult<List<Company>> companyLoadResult : List.of(companyLoadResult0, companyLoadResult1)) {
                                argumentsList.add(arguments(companyLoadResult, speakerLoadResult, talkLoadResult, placeLoadResult, eventLoadResult));
                            }
                        }
                    }
                }
            }

            return Stream.of(argumentsList.toArray(new Arguments[0]));
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void saveFiles(LoadResult<List<Company>> companyLoadResult, SpeakerLoadResult speakerLoadResult, LoadResult<List<Talk>> talkLoadResult,
                       LoadResult<Place> placeLoadResult, LoadResult<Event> eventLoadResult) {
            try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                mockedStatic.when(() -> ConferenceDataLoader.saveFiles(
                                Mockito.any(LoadResult.class), Mockito.any(SpeakerLoadResult.class), Mockito.any(LoadResult.class), Mockito.any(LoadResult.class), Mockito.any(LoadResult.class)))
                        .thenCallRealMethod();

                assertDoesNotThrow(() -> ConferenceDataLoader.saveFiles(companyLoadResult, speakerLoadResult, talkLoadResult, placeLoadResult, eventLoadResult));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("saveCompanies method tests")
    class SaveCompaniesTest {
        private Stream<Arguments> data() {
            Company company0 = new Company(0, Collections.emptyList());
            Company company1 = new Company(1, Collections.emptyList());

            LoadResult<List<Company>> companyLoadResult0 = new LoadResult<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            LoadResult<List<Company>> companyLoadResult1 = new LoadResult<>(
                    Collections.emptyList(),
                    List.of(company0, company1),
                    Collections.emptyList()
            );

            return Stream.of(
                    arguments(companyLoadResult0),
                    arguments(companyLoadResult1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void saveSpeakers(LoadResult<List<Company>> companyLoadResult) {
            try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                mockedStatic.when(() -> ConferenceDataLoader.saveCompanies(Mockito.any(LoadResult.class)))
                        .thenCallRealMethod();

                assertDoesNotThrow(() -> ConferenceDataLoader.saveCompanies(companyLoadResult));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("saveImages method tests")
    class SaveImagesTest {
        private Stream<Arguments> data() {
            UrlFilename urlFilename0 = new UrlFilename("url0", "filename0");
            UrlFilename urlFilename1 = new UrlFilename("url1", "filename1");

            SpeakerLoadResult speakerLoadResult0 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()),
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()));

            SpeakerLoadResult speakerLoadResult1 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()),
                    new LoadResult<>(
                            Collections.emptyList(),
                            List.of(urlFilename0),
                            List.of(urlFilename1)));

            return Stream.of(
                    arguments(speakerLoadResult0),
                    arguments(speakerLoadResult1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void saveImages(SpeakerLoadResult speakerLoadResult) {
            try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                mockedStatic.when(() -> ConferenceDataLoader.saveImages(Mockito.any(SpeakerLoadResult.class)))
                        .thenCallRealMethod();

                assertDoesNotThrow(() -> ConferenceDataLoader.saveImages(speakerLoadResult));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("saveSpeakers method tests")
    class SaveSpeakersTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            Speaker speaker1 = new Speaker();

            SpeakerLoadResult speakerLoadResult0 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()),
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()));

            SpeakerLoadResult speakerLoadResult1 = new SpeakerLoadResult(
                    new LoadResult<>(
                            Collections.emptyList(),
                            List.of(speaker0),
                            List.of(speaker1)),
                    new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList()));

            return Stream.of(
                    arguments(speakerLoadResult0),
                    arguments(speakerLoadResult1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void saveSpeakers(SpeakerLoadResult speakerLoadResult) {
            try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                mockedStatic.when(() -> ConferenceDataLoader.saveSpeakers(Mockito.any(SpeakerLoadResult.class)))
                        .thenCallRealMethod();

                assertDoesNotThrow(() -> ConferenceDataLoader.saveSpeakers(speakerLoadResult));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("saveTalks method tests")
    class SaveTalksTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            Talk talk1 = new Talk();
            Talk talk2 = new Talk();

            LoadResult<List<Talk>> talkLoadResult0 = new LoadResult<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList());

            LoadResult<List<Talk>> talkLoadResult1 = new LoadResult<>(
                    List.of(talk0),
                    List.of(talk1),
                    List.of(talk2));

            return Stream.of(
                    arguments(talkLoadResult0),
                    arguments(talkLoadResult1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void saveTalks(LoadResult<List<Talk>> talkLoadResult) {
            try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                mockedStatic.when(() -> ConferenceDataLoader.saveTalks(Mockito.any(LoadResult.class)))
                        .thenCallRealMethod();

                assertDoesNotThrow(() -> ConferenceDataLoader.saveTalks(talkLoadResult));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("savePlaces method tests")
    class SavePlacesTest {
        private Stream<Arguments> data() {
            Place place0 = new Place();
            Place place1 = new Place();

            LoadResult<Place> placeLoadResult0 = new LoadResult<>(
                    null,
                    null,
                    null);

            LoadResult<Place> placeLoadResult1 = new LoadResult<>(
                    null,
                    place0,
                    place1);

            return Stream.of(
                    arguments(placeLoadResult0),
                    arguments(placeLoadResult1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void savePlaces(LoadResult<Place> placeLoadResult) {
            try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                mockedStatic.when(() -> ConferenceDataLoader.savePlaces(Mockito.any(LoadResult.class)))
                        .thenCallRealMethod();

                assertDoesNotThrow(() -> ConferenceDataLoader.savePlaces(placeLoadResult));
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("saveEvents method tests")
        class SaveEventsTest {
            private Stream<Arguments> data() {
                Event event0 = new Event();
                Event event1 = new Event();

                LoadResult<Event> eventLoadResult0 = new LoadResult<>(
                        null,
                        null,
                        null);

                LoadResult<Event> eventLoadResult1 = new LoadResult<>(
                        null,
                        event0,
                        event1);

                return Stream.of(
                        arguments(eventLoadResult0),
                        arguments(eventLoadResult1)
                );
            }

            @ParameterizedTest
            @MethodSource("data")
            @SuppressWarnings("unchecked")
            void saveEvents(LoadResult<Event> eventLoadResult) {
                try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                    mockedStatic.when(() -> ConferenceDataLoader.saveEvents(Mockito.any(LoadResult.class)))
                            .thenCallRealMethod();

                    assertDoesNotThrow(() -> ConferenceDataLoader.saveEvents(eventLoadResult));
                }
            }
        }
    }

    @Test
    void logAndSaveEventTypes() {
        try (MockedStatic<LocalizationUtils> localizationUtilsMockedStatic = Mockito.mockStatic(LocalizationUtils.class);
             MockedStatic<YamlUtils> yamlUtilsMockedStatic = Mockito.mockStatic(YamlUtils.class)
        ) {
            localizationUtilsMockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                    .thenReturn("");

            assertDoesNotThrow(() -> ConferenceDataLoader.logAndSaveEventTypes(List.of(new EventType()), "{}", "filename"));
        }
    }

    @Test
    void logAndSaveCompanies() {
        try (MockedStatic<LocalizationUtils> localizationUtilsMockedStatic = Mockito.mockStatic(LocalizationUtils.class);
             MockedStatic<YamlUtils> yamlUtilsMockedStatic = Mockito.mockStatic(YamlUtils.class)
        ) {
            localizationUtilsMockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                    .thenReturn("");

            assertDoesNotThrow(() -> ConferenceDataLoader.logAndSaveCompanies(List.of(new Company()), "{}", "filename"));
        }
    }

    @Test
    void logAndCreateSpeakerImages() {
        try (MockedStatic<ImageUtils> mockedStatic = Mockito.mockStatic(ImageUtils.class)) {
            assertDoesNotThrow(() -> ConferenceDataLoader.logAndCreateSpeakerImages(List.of(new UrlFilename("url", "filename")), "{}"));
        }
    }

    @Test
    void logAndSaveSpeakers() {
        try (MockedStatic<LocalizationUtils> localizationUtilsMockedStatic = Mockito.mockStatic(LocalizationUtils.class);
             MockedStatic<YamlUtils> yamlUtilsMockedStatic = Mockito.mockStatic(YamlUtils.class)
        ) {
            localizationUtilsMockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                    .thenReturn("");

            assertDoesNotThrow(() -> ConferenceDataLoader.logAndSaveSpeakers(List.of(new Speaker()), "{}", "filename"));
        }
    }

    @Test
    void logAndSaveTalks() {
        try (MockedStatic<LocalizationUtils> localizationUtilsMockedStatic = Mockito.mockStatic(LocalizationUtils.class);
             MockedStatic<YamlUtils> yamlUtilsMockedStatic = Mockito.mockStatic(YamlUtils.class)
        ) {
            localizationUtilsMockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                    .thenReturn("");

            assertDoesNotThrow(() -> ConferenceDataLoader.logAndSaveTalks(List.of(new Talk()), "{}", "filename"));
        }
    }

    @Test
    void savePlace() {
        try (MockedStatic<YamlUtils> mockedStatic = Mockito.mockStatic(YamlUtils.class)) {
            assertDoesNotThrow(() -> ConferenceDataLoader.savePlace(new Place(), "filename"));
        }
    }

    @Test
    void saveEvent() {
        try (MockedStatic<YamlUtils> mockedStatic = Mockito.mockStatic(YamlUtils.class)) {
            assertDoesNotThrow(() -> ConferenceDataLoader.saveEvent(new Event(), "filename"));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("findResourceCompany method tests")
    class FindResourceCompanyTest {
        private Stream<Arguments> data() {
            final String COMPANY_NAME0 = "Company0";
            final String COMPANY_NAME1 = "Company1";

            Company company0 = new Company(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), COMPANY_NAME0)));
            Company company1 = new Company(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), COMPANY_NAME0)));
            Company company2 = new Company(2, List.of(new LocaleItem(Language.ENGLISH.getCode(), COMPANY_NAME1)));

            Map<String, Company> resourceCompanyMap = Map.of(COMPANY_NAME0.toLowerCase(), company0);

            return Stream.of(
                    arguments(company0, resourceCompanyMap, company0),
                    arguments(company1, resourceCompanyMap, company0),
                    arguments(company2, resourceCompanyMap, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void findResourceCompany(Company company, Map<String, Company> resourceCompanyMap, Company expected) {
            assertEquals(expected, ConferenceDataLoader.findResourceCompany(company, resourceCompanyMap));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("findResourceSpeaker method tests")
    class FindResourceSpeakerTest {
        private Stream<Arguments> data() {
            final String SPEAKER_NAME0 = "Имя0";
            final String SPEAKER_NAME1 = "Имя1";
            final String SPEAKER_NAME2 = "Имя2";
            final String SPEAKER_NAME3 = "Имя3";
            final String SPEAKER_NAME4 = "Имя4";
            final String SPEAKER_NAME5 = "Имя5";
            final String SPEAKER_NAME6 = "Имя6";
            final String SPEAKER_NAME7 = "Имя7";
            final String SPEAKER_NAME8 = "Имя8";
            final String COMPANY_NAME0 = "Компания0";
            final String COMPANY_NAME1 = "Компания1";
            final String COMPANY_NAME2 = "Компания2";
            final String COMPANY_NAME3 = "Компания3";
            final String COMPANY_NAME4 = "Компания4";
            final String COMPANY_NAME5 = "Компания5";

            Company company0 = new Company(0, List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME0)));
            Company company1 = new Company(1, List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME1)));
            Company company2 = new Company(2, List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME2)));
            Company company3 = new Company(3, List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME3)));
            Company company4 = new Company(4, List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME4)));
            Company company5 = new Company(5, List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME5)));

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME0)));
            speaker0.setCompanies(List.of(company0));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME1)));
            speaker1.setCompanies(List.of(company1));

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);
            speaker2.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME2)));
            speaker2.setCompanies(List.of(company2));

            Speaker speaker3 = new Speaker();
            speaker3.setId(3);
            speaker3.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME3)));
            speaker3.setCompanies(List.of(company3));

            Speaker speaker4 = new Speaker();
            speaker4.setId(4);
            speaker4.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME4)));
            speaker4.setCompanies(List.of(company4));

            Speaker speaker5 = new Speaker();
            speaker5.setId(5);
            speaker5.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME5)));
            speaker5.setCompanies(List.of(company5));

            Speaker speaker6 = new Speaker();
            speaker6.setId(6);
            speaker6.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME6)));
            speaker6.setCompanies(Collections.singletonList(null));

            Speaker speaker7 = new Speaker();
            speaker7.setId(7);
            speaker7.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME7)));
            speaker7.setCompanies(null);

            Speaker speaker8 = new Speaker();
            speaker8.setId(8);
            speaker8.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME8)));
            speaker8.setCompanies(Collections.emptyList());

            NameCompany nameCompany0 = new NameCompany(SPEAKER_NAME0, company0);
            NameCompany nameCompany1 = new NameCompany(SPEAKER_NAME1, company1);
            NameCompany nameCompany6 = new NameCompany(SPEAKER_NAME6, null);
            NameCompany nameCompany7 = new NameCompany(SPEAKER_NAME7, null);
            NameCompany nameCompany8 = new NameCompany(SPEAKER_NAME8, null);

            SpeakerLoadMaps speakerLoadMaps = new SpeakerLoadMaps(
                    Map.of(nameCompany0, 0L, nameCompany1, 1L, nameCompany6, 6L, nameCompany7, 7L, nameCompany8, 8L),
                    Map.of(0L, speaker0, 7L, speaker7, 8L, speaker8),
                    Collections.emptyMap(),
                    Collections.emptyMap());

            return Stream.of(
                    arguments(speaker0, speakerLoadMaps, null),
                    arguments(speaker1, speakerLoadMaps, NullPointerException.class),
                    arguments(speaker2, speakerLoadMaps, null),
                    arguments(speaker3, speakerLoadMaps, null),
                    arguments(speaker4, speakerLoadMaps, null),
                    arguments(speaker5, speakerLoadMaps, null),
                    arguments(speaker6, speakerLoadMaps, NullPointerException.class),
                    arguments(speaker7, speakerLoadMaps, null),
                    arguments(speaker8, speakerLoadMaps, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void findResourceSpeaker(Speaker speaker, SpeakerLoadMaps speakerLoadMaps, Class<? extends Throwable> expectedException) {
            try (MockedStatic<ConferenceDataLoader> conferenceDataLoaderMockedStatic = Mockito.mockStatic(ConferenceDataLoader.class);
                 MockedStatic<LocalizationUtils> localizationUtilsMockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.findResourceSpeaker(Mockito.any(Speaker.class), Mockito.any(SpeakerLoadMaps.class)))
                        .thenCallRealMethod();
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.findResourceSpeakerByNameCompany(Mockito.any(Speaker.class), Mockito.anyMap()))
                        .thenAnswer(
                                (Answer<Speaker>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    Speaker localSpeaker = (Speaker) args[0];

                                    return ((localSpeaker.getId() == 2) || (localSpeaker.getId() == 3)) ? localSpeaker : null;
                                }
                        );
                conferenceDataLoaderMockedStatic.when(() -> ConferenceDataLoader.findResourceSpeakerByName(Mockito.any(Speaker.class), Mockito.anyMap()))
                        .thenAnswer(
                                (Answer<Speaker>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    Speaker localSpeaker = (Speaker) args[0];

                                    return ((localSpeaker.getId() == 4) || (localSpeaker.getId() == 5)) ? localSpeaker : null;
                                }
                        );
                localizationUtilsMockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                        .thenAnswer(
                                (Answer<String>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    List<LocaleItem> localeItems = (List<LocaleItem>) args[0];

                                    return ((localeItems != null) && !localeItems.isEmpty()) ? localeItems.get(0).getText() : null;
                                }
                        );

                if (expectedException == null) {
                    assertDoesNotThrow(() -> ConferenceDataLoader.findResourceSpeaker(speaker, speakerLoadMaps));
                } else {
                    assertThrows(expectedException, () -> ConferenceDataLoader.findResourceSpeaker(speaker, speakerLoadMaps));
                }
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("findResourceTalk method tests")
    class FindResourceTalkTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setId(0);

            Talk talk1 = new Talk();
            talk1.setId(1);

            return Stream.of(
                    arguments(talk0, Collections.emptyMap(), Collections.emptyMap(), talk0),
                    arguments(talk1, Collections.emptyMap(), Collections.emptyMap(), null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void findResourceTalk(Talk talk, Map<String, Set<Talk>> resourceRuNameTalks, Map<String, Set<Talk>> resourceEnNameTalks,
                              Talk expected) {
            try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                mockedStatic.when(() -> ConferenceDataLoader.findResourceTalk(Mockito.any(Talk.class), Mockito.anyMap(), Mockito.anyMap()))
                        .thenCallRealMethod();
                mockedStatic.when(() -> ConferenceDataLoader.findResourceTalkByName(Mockito.any(Talk.class), Mockito.anyMap(), Mockito.any(Language.class)))
                        .thenAnswer(
                                (Answer<Talk>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    Talk localTalk = (Talk) args[0];

                                    return (localTalk.getId() == 0) ? localTalk : null;
                                }
                        );

                assertEquals(expected, ConferenceDataLoader.findResourceTalk(talk, resourceRuNameTalks, resourceEnNameTalks));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("findResourceSpeakerByNameCompany method tests")
    class FindResourceSpeakerByNameCompanyTest {
        private Stream<Arguments> data() {
            Company company0 = new Company(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company0")));
            Company company1 = new Company(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company1")));

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
            speaker0.setCompanies(List.of(company0));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));
            speaker1.setCompanies(List.of(company1));

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);
            speaker2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name2")));

            NameCompany nameCompany0 = new NameCompany("Name0", company0);
            Map<NameCompany, Speaker> resourceNameCompanySpeakers0 = Map.of(nameCompany0, speaker0);

            return Stream.of(
                    arguments(speaker0, resourceNameCompanySpeakers0, speaker0),
                    arguments(speaker1, resourceNameCompanySpeakers0, null),
                    arguments(speaker2, resourceNameCompanySpeakers0, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void findResourceSpeakerByNameCompany(Speaker speaker, Map<NameCompany, Speaker> resourceNameCompanySpeakers,
                                              Speaker expected) {
            assertEquals(expected, ConferenceDataLoader.findResourceSpeakerByNameCompany(speaker, resourceNameCompanySpeakers));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("findResourceSpeakerByName method tests")
    class FindResourceSpeakerByNameTest {
        private Stream<Arguments> data() {
            final String SPEAKER_NAME0 = "Name0";
            final String SPEAKER_NAME1 = "Name1";
            final String SPEAKER_NAME2 = "Name2";
            final String SPEAKER_NAME3 = "Name3";

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), SPEAKER_NAME0)));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), SPEAKER_NAME1)));

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);
            speaker2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), SPEAKER_NAME2)));

            Speaker speaker3 = new Speaker();
            speaker3.setId(3);
            speaker3.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), SPEAKER_NAME3)));

            Map<String, Set<Speaker>> resourceNameSpeakers0 = new HashMap<>();
            resourceNameSpeakers0.put(SPEAKER_NAME0, Set.of(speaker0));
            resourceNameSpeakers0.put(SPEAKER_NAME2, Collections.emptySet());
            resourceNameSpeakers0.put(SPEAKER_NAME3, Set.of(speaker0, speaker3));

            return Stream.of(
                    arguments(speaker1, resourceNameSpeakers0, null, null),
                    arguments(speaker2, resourceNameSpeakers0, null, IllegalStateException.class),
                    arguments(speaker3, resourceNameSpeakers0, null, null),
                    arguments(speaker0, resourceNameSpeakers0, speaker0, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void findResourceSpeakerByName(Speaker speaker, Map<String, Set<Speaker>> resourceNameSpeakers,
                                       Speaker expected, Class<? extends Throwable> expectedException) {
            try (MockedStatic<LocalizationUtils> mockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
                mockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                        .thenAnswer(
                                (Answer<String>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    List<LocaleItem> localeItems = (List<LocaleItem>) args[0];

                                    return ((localeItems != null) && !localeItems.isEmpty()) ? localeItems.get(0).getText() : null;
                                }
                        );

                if (expectedException == null) {
                    assertEquals(expected, ConferenceDataLoader.findResourceSpeakerByName(speaker, resourceNameSpeakers));
                } else {
                    assertThrows(expectedException, () -> ConferenceDataLoader.findResourceSpeakerByName(speaker, resourceNameSpeakers));
                }
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("findResourceTalkByName method tests")
    class FindResourceTalkByNameTest {
        final String TALK_NAME0 = "Name0";
        final String TALK_NAME1 = "Name1";
        final String TALK_NAME2 = "Name2";
        final String TALK_NAME3 = "Name3";

        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setId(0);
            talk0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), TALK_NAME0)));

            Talk talk1 = new Talk();
            talk1.setId(1);
            talk1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), TALK_NAME1)));

            Talk talk2 = new Talk();
            talk2.setId(2);
            talk2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), TALK_NAME2)));

            Talk talk3 = new Talk();
            talk3.setId(3);
            talk3.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), TALK_NAME3)));

            Map<String, Set<Talk>> resourceNameTalks0 = new HashMap<>();
            resourceNameTalks0.put(TALK_NAME0, Set.of(talk0));
            resourceNameTalks0.put(TALK_NAME2, Collections.emptySet());
            resourceNameTalks0.put(TALK_NAME3, Set.of(talk0, talk3));

            return Stream.of(
                    arguments(talk1, resourceNameTalks0, Language.ENGLISH, null, null),
                    arguments(talk2, resourceNameTalks0, Language.ENGLISH, null, IllegalStateException.class),
                    arguments(talk3, resourceNameTalks0, Language.ENGLISH, null, null),
                    arguments(talk0, resourceNameTalks0, Language.ENGLISH, talk0, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void findResourceTalkByName(Talk talk, Map<String, Set<Talk>> resourceNameTalks, Language language,
                                    Talk expected, Class<? extends Throwable> expectedException) {
            try (MockedStatic<LocalizationUtils> mockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
                mockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                        .thenAnswer(
                                (Answer<String>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    List<LocaleItem> localeItems = (List<LocaleItem>) args[0];

                                    return ((localeItems != null) && !localeItems.isEmpty()) ? localeItems.get(0).getText() : null;
                                }
                        );

                if (expectedException == null) {
                    assertEquals(expected, ConferenceDataLoader.findResourceTalkByName(talk, resourceNameTalks, language));
                } else {
                    assertThrows(expectedException, () -> ConferenceDataLoader.findResourceTalkByName(talk, resourceNameTalks, language));
                }
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("findResourcePlaceByCityVenueAddress method tests")
    class FindResourcePlaceByCityVenueAddressTest {
        final String CITY0 = "City0";
        final String CITY1 = "City1";
        final String VENUE_ADDRESS0 = "Venue Address0";
        final String VENUE_ADDRESS1 = "Venue Address1";

        private Stream<Arguments> data() {
            Place place0 = new Place();
            place0.setId(0);
            place0.setCity(List.of(new LocaleItem(Language.ENGLISH.getCode(), CITY0)));
            place0.setVenueAddress(List.of(new LocaleItem(Language.ENGLISH.getCode(), VENUE_ADDRESS0)));

            Place place1 = new Place();
            place1.setId(1);
            place1.setCity(List.of(new LocaleItem(Language.ENGLISH.getCode(), CITY1)));
            place1.setVenueAddress(List.of(new LocaleItem(Language.ENGLISH.getCode(), VENUE_ADDRESS1)));

            Map<CityVenueAddress, Place> resourceCityVenueAddressPlaces0 = Map.of(
                    new CityVenueAddress(CITY0, VENUE_ADDRESS0), place0);

            return Stream.of(
                    arguments(place0, resourceCityVenueAddressPlaces0, Language.ENGLISH, place0),
                    arguments(place1, resourceCityVenueAddressPlaces0, Language.ENGLISH, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void findResourcePlaceByCityVenueAddress(Place place, Map<CityVenueAddress, Place> resourceCityVenueAddressPlaces,
                                                 Language language, Place expected) {
            try (MockedStatic<LocalizationUtils> mockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
                mockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                        .thenAnswer(
                                (Answer<String>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    List<LocaleItem> localeItems = (List<LocaleItem>) args[0];

                                    return ((localeItems != null) && !localeItems.isEmpty()) ? localeItems.get(0).getText() : null;
                                }
                        );

                assertEquals(expected, ConferenceDataLoader.findResourcePlaceByCityVenueAddress(place, resourceCityVenueAddressPlaces, language));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("findResourcePlace method tests")
    class FindResourcePlaceTest {
        private final Place place0;
        private final Place place1;

        public FindResourcePlaceTest() {
            place0 = new Place();
            place0.setId(0);

            place1 = new Place();
            place1.setId(1);
        }

        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(place0, Collections.emptyMap(), Collections.emptyMap(), place0),
                    arguments(place1, Collections.emptyMap(), Collections.emptyMap(), null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void findResourcePlace(Place place, Map<CityVenueAddress, Place> resourceRuCityVenueAddressPlaces,
                               Map<CityVenueAddress, Place> resourceEnCityVenueAddressPlaces, Place expected) {
            try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
                mockedStatic.when(() -> ConferenceDataLoader.findResourcePlace(Mockito.any(Place.class), Mockito.anyMap(), Mockito.anyMap()))
                        .thenCallRealMethod();
                mockedStatic.when(() -> ConferenceDataLoader.findResourcePlaceByCityVenueAddress(Mockito.any(Place.class), Mockito.anyMap(), Mockito.any(Language.class)))
                        .thenAnswer(
                                (Answer<Place>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    Place localPlace = (Place) args[0];

                                    if (localPlace != null) {
                                        return (localPlace.getId() == 0) ? localPlace : null;
                                    } else {
                                        return null;
                                    }
                                }
                        );

                assertEquals(expected, ConferenceDataLoader.findResourcePlace(place, resourceRuCityVenueAddressPlaces, resourceEnCityVenueAddressPlaces));
            }
        }
    }

    @Test
    void fixVenueAddress() {
        try (MockedStatic<ConferenceDataLoader> mockedStatic = Mockito.mockStatic(ConferenceDataLoader.class)) {
            mockedStatic.when(() -> ConferenceDataLoader.fixVenueAddress(Mockito.any(Place.class)))
                    .thenCallRealMethod();
            mockedStatic.when(() -> ConferenceDataLoader.getFixedVenueAddress(Mockito.anyString(), Mockito.anyString(), Mockito.anyList()))
                    .thenReturn("");

            assertDoesNotThrow(() -> ConferenceDataLoader.fixVenueAddress(new Place()));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getFixedVenueAddress method tests")
    class GetFixedVenueAddressTest {
        private Stream<Arguments> data() {
            final String CITY = "City";
            final String VENUE_ADDRESS = "Venue Address";
            final String VALID_VENUE_ADDRESS = "Valid Venue Address";

            List<FixingVenueAddress> fixingVenueAddresses0 = List.of(
                    new FixingVenueAddress("", "", VALID_VENUE_ADDRESS));

            List<FixingVenueAddress> fixingVenueAddresses1 = List.of(
                    new FixingVenueAddress(CITY, "", VALID_VENUE_ADDRESS));

            List<FixingVenueAddress> fixingVenueAddresses2 = List.of(
                    new FixingVenueAddress("", VENUE_ADDRESS, VALID_VENUE_ADDRESS));

            List<FixingVenueAddress> fixingVenueAddresses3 = List.of(
                    new FixingVenueAddress(CITY, VENUE_ADDRESS, VALID_VENUE_ADDRESS));

            return Stream.of(
                    arguments(CITY, VENUE_ADDRESS, Collections.emptyList(), VENUE_ADDRESS),
                    arguments(CITY, VENUE_ADDRESS, fixingVenueAddresses0, VENUE_ADDRESS),
                    arguments(CITY, VENUE_ADDRESS, fixingVenueAddresses1, VENUE_ADDRESS),
                    arguments(CITY, VENUE_ADDRESS, fixingVenueAddresses2, VENUE_ADDRESS),
                    arguments(CITY, VENUE_ADDRESS, fixingVenueAddresses3, VALID_VENUE_ADDRESS)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getFixedVenueAddress(String city, String venueAddress, List<FixingVenueAddress> fixingVenueAddresses,
                                  String expected) {
            assertEquals(expected, ConferenceDataLoader.getFixedVenueAddress(city, venueAddress, fixingVenueAddresses));
        }
    }

    @Test
    void checkVideoLinks() {
        try (MockedStatic<YamlUtils> mockedStatic = Mockito.mockStatic(YamlUtils.class)) {
            LocalDate now = LocalDate.now();
            LocalDate yesterday = now.minusDays(1);
            LocalDate tomorrow = now.plusDays(1);

            EventType eventType0 = new EventType();

            EventType eventType1 = new EventType();
            eventType1.setConference(Conference.JOKER);

            Event event0 = new Event();
            event0.setEventType(eventType0);
            event0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));

            Event event1 = new Event();
            event1.setEventType(eventType1);
            event1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));
            event1.setStartDate(now);

            Event event2 = new Event();
            event2.setEventType(eventType1);
            event2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name2")));
            event2.setStartDate(tomorrow);

            Event event3 = new Event();
            event3.setEventType(eventType1);
            event3.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name3")));
            event3.setStartDate(yesterday);

            Event event4 = new Event();
            event4.setEventType(eventType1);
            event4.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name4")));
            event4.setStartDate(yesterday);

            Event event5 = new Event();
            event5.setEventType(eventType1);
            event5.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name5")));
            event5.setStartDate(yesterday);

            Event event6 = new Event();
            event6.setEventType(eventType1);
            event6.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name6")));
            event6.setStartDate(yesterday);

            Talk talk0 = new Talk();

            Talk talk1 = new Talk();
            talk1.setVideoLinks(Collections.emptyList());

            Talk talk2 = new Talk();
            talk2.setVideoLinks(List.of("Link0"));

            Talk talk3 = new Talk();
            talk3.setVideoLinks(List.of("Link0"));

            Talk talk4 = new Talk();
            talk4.setVideoLinks(List.of("Link0"));

            event2.setTalks(List.of(talk0));
            event3.setTalks(List.of(talk1));
            event4.setTalks(List.of(talk2));
            event5.setTalks(List.of(talk1, talk2));
            event6.setTalks(List.of(talk1, talk2, talk3, talk4));

            mockedStatic.when(YamlUtils::readSourceInformation)
                    .thenReturn(new SourceInformation(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                            List.of(event0, event1, event2, event3, event4, event5, event6),
                            new SourceInformation.SpeakerInformation(
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList()
                            ),
                            Collections.emptyList()
                    ));

            assertDoesNotThrow(ConferenceDataLoader::checkVideoLinks);
        }
    }

    @Test
    void checkCompanies() {
        try (MockedStatic<YamlUtils> mockedStatic = Mockito.mockStatic(YamlUtils.class)) {
            Company company0 = new Company(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
            Company company1 = new Company(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")), "");
            Company company2 = new Company(2, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name2")), " ");
            Company company3 = new Company(3, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name3")), "https://site1.com");
            Company company4 = new Company(4, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name4")), "https://site2.com");
            Company company5 = new Company(5, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name5")), "https://site2.com");

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
            speaker0.setCompanies(List.of(company0));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));
            speaker1.setCompanies(Collections.emptyList());

            mockedStatic.when(YamlUtils::readSourceInformation)
                    .thenReturn(new SourceInformation(Collections.emptyList(), Collections.emptyList(),
                            Collections.emptyList(), Collections.emptyList(),
                            new SourceInformation.SpeakerInformation(
                                    List.of(company0, company1, company2, company3, company4, company5),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    List.of(speaker0, speaker1)
                            ),
                            Collections.emptyList()
                    ));

            assertDoesNotThrow(ConferenceDataLoader::checkCompanies);
        }
    }

    @Test
    void main() {
        assertDoesNotThrow(() -> ConferenceDataLoader.main(new String[]{}));
    }
}
