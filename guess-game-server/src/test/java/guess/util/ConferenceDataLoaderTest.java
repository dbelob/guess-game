package guess.util;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.Identifier;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.image.UrlFilename;
import guess.domain.source.load.LoadResult;
import guess.domain.source.load.SpeakerLoadMaps;
import guess.domain.source.load.SpeakerLoadResult;
import guess.util.yaml.YamlUtils;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("ConferenceDataLoader class tests")
class ConferenceDataLoaderTest {
    @Test
    void loadEventTypes() {
        new MockUp<YamlUtils>() {
            @Mock
            SourceInformation readSourceInformation() throws SpeakerDuplicatedException, IOException {
                return new SourceInformation(Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
                );
            }
        };

        new MockUp<ContentfulUtils>() {
            @Mock
            List<EventType> getEventTypes() {
                return Collections.emptyList();
            }
        };

        new MockUp<ConferenceDataLoader>() {
            @Mock
            void loadEventTypes(Invocation invocation) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
                invocation.proceed();
            }

            @Mock
            List<EventType> getConferences(List<EventType> eventTypes) {
                return Collections.emptyList();
            }

            @Mock
            Map<Conference, EventType> getResourceEventTypeMap(List<EventType> eventTypes) {
                return Collections.emptyMap();
            }

            @Mock
            <T extends Identifier> long getLastId(List<T> entities) {
                return 42;
            }

            @Mock
            LoadResult<List<EventType>> getEventTypeLoadResult(List<EventType> eventTypes, Map<Conference,
                    EventType> eventTypeMap, AtomicLong lastEventTypeId) {
                return new LoadResult<>(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
            }

            @Mock
            void saveEventTypes(LoadResult<List<EventType>> loadResult) throws IOException, NoSuchFieldException {
                // Nothing
            }
        };

        assertDoesNotThrow(ConferenceDataLoader::loadEventTypes);
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

    @Test
    void getEventTypeLoadResult() {
        new MockUp<ContentfulUtils>() {
            @Mock
            boolean needUpdate(EventType a, EventType b) {
                return (Conference.JPOINT.equals(a.getConference()) && Conference.JPOINT.equals(b.getConference()));
            }
        };

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
                            new ArrayList<>(List.of(new EventType())))),
                    arguments(new LoadResult<>(
                            Collections.emptyList(),
                            List.of(new EventType()),
                            new ArrayList<>(List.of(new EventType()))))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void saveEventTypes(LoadResult<List<EventType>> loadResult, @Mocked YamlUtils yamlUtilsMock) {
            new MockUp<ConferenceDataLoader>() {
                @Mock
                void saveEventTypes(Invocation invocation, LoadResult<List<EventType>> loadResult) throws IOException, NoSuchFieldException {
                    invocation.proceed(loadResult);
                }

                @Mock
                void logAndDumpEventTypes(List<EventType> eventTypes, String logMessage, String filename) throws IOException, NoSuchFieldException {
                    // Nothing
                }
            };

            assertDoesNotThrow(() -> ConferenceDataLoader.saveEventTypes(loadResult));
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

            Event event0 = new Event();
            event0.setId(0);
            event0.setStartDate(EVENT_DATE);
            event0.setPlace(place0);

            EventType eventType0 = new EventType();
            eventType0.setId(0);
            eventType0.setConference(JPOINT_CONFERENCE);
            eventType0.setEvents(List.of(event0));

            Talk talk0 = new Talk();
            talk0.setId(0);

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            return Stream.of(
                    arguments(JPOINT_CONFERENCE, EVENT_DATE, EVENT_CODE, Collections.emptyMap(), Collections.emptySet(),
                            new SourceInformation(
                                    List.of(place0),
                                    List.of(eventType0),
                                    Collections.emptyList(),
                                    List.of(speaker0),
                                    Collections.emptyList()),
                            event0,
                            List.of(talk0),
                            List.of(speaker0)),
                    arguments(JPOINT_CONFERENCE, LocalDate.of(2020, 6, 30), EVENT_CODE, Collections.emptyMap(), Collections.emptySet(),
                            new SourceInformation(
                                    List.of(place0),
                                    List.of(eventType0),
                                    Collections.emptyList(),
                                    List.of(speaker0),
                                    Collections.emptyList()),
                            event0,
                            List.of(talk0),
                            List.of(speaker0))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void loadTalksSpeakersEvent(Conference conference, LocalDate startDate, String conferenceCode,
                                    Map<NameCompany, Long> knownSpeakerIdsMap, Set<String> invalidTalksSet,
                                    SourceInformation sourceInformation, Event contentfulEvent,
                                    List<Talk> contentfulTalks, List<Speaker> talkSpeakers) {
            new MockUp<YamlUtils>() {
                @Mock
                SourceInformation readSourceInformation() throws SpeakerDuplicatedException, IOException {
                    return sourceInformation;
                }
            };

            new MockUp<LocalizationUtils>() {
                @Mock
                String getString(List<LocaleItem> localeItems, Language language) {
                    return "";
                }
            };

            new MockUp<ContentfulUtils>() {
                @Mock
                Event getEvent(Conference conference, LocalDate startDate) {
                    return contentfulEvent;
                }

                @Mock
                List<Talk> getTalks(Conference conference, String conferenceCode) {
                    return contentfulTalks;
                }
            };

            new MockUp<ConferenceDataLoader>() {
                @Mock
                void loadTalksSpeakersEvent(Invocation invocation, Conference conference, LocalDate startDate, String conferenceCode,
                                            Map<NameCompany, Long> knownSpeakerIdsMap, Set<String> invalidTalksSet) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
                    invocation.proceed(conference, startDate, conferenceCode, knownSpeakerIdsMap, invalidTalksSet);
                }

                @Mock
                List<Talk> deleteInvalidTalks(List<Talk> talks, Set<String> invalidTalksSet) {
                    return talks;
                }

                @Mock
                List<Talk> deleteOpeningAndClosingTalks(List<Talk> talks) {
                    return talks;
                }

                @Mock
                List<Talk> deleteTalkDuplicates(List<Talk> talks) {
                    return talks;
                }

                @Mock
                List<Speaker> getTalkSpeakers(List<Talk> talks) {
                    return talkSpeakers;
                }

                @Mock
                SpeakerLoadResult getSpeakerLoadResult(List<Speaker> speakers,
                                                       SpeakerLoadMaps speakerLoadMaps,
                                                       AtomicLong lastSpeakerId) throws IOException {
                    return new SpeakerLoadResult(
                            new LoadResult<>(
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList()),
                            new LoadResult<>(
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList()));
                }

                @Mock
                void fillSpeakerIds(List<Talk> talks) {
                    // Nothing
                }

                @Mock
                <T extends Identifier> long getLastId(List<T> entities) {
                    return 42;
                }

                @Mock
                LoadResult<List<Talk>> getTalkLoadResult(List<Talk> talks, Event resourceEvent, List<Event> resourceEvents,
                                                         AtomicLong lasTalksId) {
                    return new LoadResult<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList());
                }

                @Mock
                List<LocaleItem> fixVenueAddress(Place place) {
                    return Collections.emptyList();
                }

                @Mock
                Place findResourcePlace(Place place,
                                        Map<CityVenueAddress, Place> resourceRuCityVenueAddressPlaces,
                                        Map<CityVenueAddress, Place> resourceEnCityVenueAddressPlaces) {
                    return place;
                }

                @Mock
                LoadResult<Place> getPlaceLoadResult(Place place, Place resourcePlace, AtomicLong lastPlaceId) {
                    return new LoadResult<>(
                            null,
                            null,
                            null);
                }

                @Mock
                LoadResult<Event> getEventLoadResult(Event event, Event resourceEvent) {
                    return new LoadResult<>(
                            null,
                            null,
                            null);
                }

                @Mock
                void saveFiles(SpeakerLoadResult speakerLoadResult, LoadResult<List<Talk>> talkLoadResult,
                               LoadResult<Place> placeLoadResult, LoadResult<Event> eventLoadResult) throws IOException, NoSuchFieldException {
                    // Nothing
                }
            };

            assertDoesNotThrow(() -> ConferenceDataLoader.loadTalksSpeakersEvent(conference, startDate, conferenceCode, knownSpeakerIdsMap, invalidTalksSet));
        }
    }

    @Test
    void loadTalksSpeakersEventWithoutInvalidTalksSet() {
        new MockUp<ConferenceDataLoader>() {
            @Mock
            void loadTalksSpeakersEvent(Conference conference, LocalDate startDate, String conferenceCode,
                                        Map<NameCompany, Long> knownSpeakerIdsMap,
                                        Set<String> invalidTalksSet) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
                // Nothing
            }

            @Mock
            void loadTalksSpeakersEvent(Invocation invocation, Conference conference, LocalDate startDate, String conferenceCode,
                                        Map<NameCompany, Long> knownSpeakerIdsMap) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
                invocation.proceed(conference, startDate, conferenceCode, knownSpeakerIdsMap);
            }
        };

        assertDoesNotThrow(() -> ConferenceDataLoader.loadTalksSpeakersEvent(
                Conference.JPOINT,
                LocalDate.of(2020, 6, 29),
                "2020-jpoint",
                Collections.emptyMap()));
    }

    @Test
    void loadTalksSpeakersEventWithoutInvalidTalksSetAndKnownSpeakerIdsMap() {
        new MockUp<ConferenceDataLoader>() {
            @Mock
            void loadTalksSpeakersEvent(Conference conference, LocalDate startDate, String conferenceCode,
                                        Map<NameCompany, Long> knownSpeakerIdsMap,
                                        Set<String> invalidTalksSet) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
                // Nothing
            }

            @Mock
            void loadTalksSpeakersEvent(Invocation invocation, Conference conference, LocalDate startDate, String conferenceCode) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
                invocation.proceed(conference, startDate, conferenceCode);
            }
        };

        assertDoesNotThrow(() -> ConferenceDataLoader.loadTalksSpeakersEvent(
                Conference.JPOINT,
                LocalDate.of(2020, 6, 29),
                "2020-jpoint"));
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
        void deleteInvalidTalks(List<Talk> talks, Set<String> invalidTalksSet, List<Talk> expected) {
            new MockUp<LocalizationUtils>() {
                @Mock
                String getString(List<LocaleItem> localeItems, Language language) {
                    return ((localeItems != null) && !localeItems.isEmpty()) ? localeItems.get(0).getText() : null;
                }
            };

            assertEquals(expected, ConferenceDataLoader.deleteInvalidTalks(talks, invalidTalksSet));
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
            talk4.setName(List.of(new LocaleItem("en", "name4")));

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyList()),
                    arguments(List.of(talk0), Collections.emptyList()),
                    arguments(List.of(talk1), Collections.emptyList()),
                    arguments(List.of(talk2), Collections.emptyList()),
                    arguments(List.of(talk3), Collections.emptyList()),
                    arguments(List.of(talk0, talk1), Collections.emptyList()),
                    arguments(List.of(talk0, talk1, talk2), Collections.emptyList()),
                    arguments(List.of(talk0, talk1, talk2, talk3), Collections.emptyList()),
                    arguments(List.of(talk4), List.of(talk4)),
                    arguments(List.of(talk0, talk4), List.of(talk4)),
                    arguments(List.of(talk0, talk1, talk4), List.of(talk4)),
                    arguments(List.of(talk0, talk1, talk2, talk4), List.of(talk4)),
                    arguments(List.of(talk0, talk1, talk2, talk3, talk4), List.of(talk4))
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
    @DisplayName("getSpeakerLoadResult method tests")
    class GetSpeakerLoadResultTest {
        final String PHOTO_FILE_NAME0 = "0000.jpg";
        final String PHOTO_FILE_NAME1 = "0001.jpg";
        final String PHOTO_FILE_NAME2 = "http://valid.com/2.jpg";
        final String DESTINATION_FILE_NAME0 = "guess-game-web/src/assets/images/speakers/0000.jpg";

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
            new MockUp<ImageUtils>() {
                @Mock
                boolean needUpdate(String sourceUrl, String destinationFileName) throws IOException {
                    return DESTINATION_FILE_NAME0.equals(destinationFileName);
                }
            };

            new MockUp<ContentfulUtils>() {
                @Mock
                boolean needUpdate(Speaker a, Speaker b) {
                    return ((a.getId() == 0) && (b.getId() == 0));
                }
            };

            new MockUp<ConferenceDataLoader>() {
                @Mock
                SpeakerLoadResult getSpeakerLoadResult(Invocation invocation, List<Speaker> speakers, SpeakerLoadMaps speakerLoadMaps,
                                                       AtomicLong lastSpeakerId) throws IOException {
                    return invocation.proceed(speakers, speakerLoadMaps, lastSpeakerId);
                }

                @Mock
                Speaker findResourceSpeaker(Speaker speaker, SpeakerLoadMaps speakerLoadMaps) {
                    return ((speaker.getId() == 0) || (speaker.getId() == 1)) ? speaker : null;
                }

                @Mock
                void fillSpeakerTwitter(Speaker targetSpeaker, Speaker resourceSpeaker) {
                    // Nothing
                }

                @Mock
                void fillSpeakerGitHub(Speaker targetSpeaker, Speaker resourceSpeaker) {
                    // Nothing
                }

                @Mock
                void fillSpeakerJavaChampion(Speaker targetSpeaker, Speaker resourceSpeaker) {
                    // Nothing
                }

                @Mock
                void fillSpeakerMvp(Speaker targetSpeaker, Speaker resourceSpeaker) {
                    // Nothing
                }
            };

            assertEquals(expected, ConferenceDataLoader.getSpeakerLoadResult(speakers, speakerLoadMaps, lastSpeakerId));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("fillSpeakerTwitter method tests")
    class FillSpeakerTwitterTest {
        private Stream<Arguments> data() {
            final String RESOURCE_SPEAKER_TWITTER = "resourceSpeakerTwitter";
            final String TARGET_SPEAKER_TWITTER = "targetSpeakerTwitter";

            Speaker resourceSpeaker0 = new Speaker();

            Speaker resourceSpeaker1 = new Speaker();
            resourceSpeaker1.setTwitter("");

            Speaker resourceSpeaker2 = new Speaker();
            resourceSpeaker2.setTwitter(RESOURCE_SPEAKER_TWITTER);

            Speaker targetSpeaker0 = new Speaker();

            Speaker targetSpeaker1 = new Speaker();
            targetSpeaker1.setTwitter("");

            Speaker targetSpeaker2 = new Speaker();
            targetSpeaker2.setTwitter(TARGET_SPEAKER_TWITTER);

            return Stream.of(
                    arguments(targetSpeaker0, resourceSpeaker0, null),
                    arguments(targetSpeaker1, resourceSpeaker0, ""),
                    arguments(targetSpeaker2, resourceSpeaker0, TARGET_SPEAKER_TWITTER),
                    arguments(targetSpeaker0, resourceSpeaker1, null),
                    arguments(targetSpeaker1, resourceSpeaker1, ""),
                    arguments(targetSpeaker2, resourceSpeaker1, TARGET_SPEAKER_TWITTER),
                    arguments(targetSpeaker0, resourceSpeaker2, RESOURCE_SPEAKER_TWITTER),
                    arguments(targetSpeaker1, resourceSpeaker2, RESOURCE_SPEAKER_TWITTER),
                    arguments(targetSpeaker2, resourceSpeaker2, TARGET_SPEAKER_TWITTER)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void fillSpeakerTwitter(Speaker targetSpeaker, Speaker resourceSpeaker,
                                String expected) {
            ConferenceDataLoader.fillSpeakerTwitter(targetSpeaker, resourceSpeaker);

            assertEquals(expected, targetSpeaker.getTwitter());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("fillSpeakerGitHub method tests")
    class FillSpeakerGitHubTest {
        private Stream<Arguments> data() {
            final String RESOURCE_SPEAKER_GIT_HUB = "resourceSpeakerGitHub";
            final String TARGET_SPEAKER_GIT_HUB = "targetSpeakerGitHub";

            Speaker resourceSpeaker0 = new Speaker();

            Speaker resourceSpeaker1 = new Speaker();
            resourceSpeaker1.setGitHub("");

            Speaker resourceSpeaker2 = new Speaker();
            resourceSpeaker2.setGitHub(RESOURCE_SPEAKER_GIT_HUB);

            Speaker targetSpeaker0 = new Speaker();

            Speaker targetSpeaker1 = new Speaker();
            targetSpeaker1.setGitHub("");

            Speaker targetSpeaker2 = new Speaker();
            targetSpeaker2.setGitHub(TARGET_SPEAKER_GIT_HUB);

            return Stream.of(
                    arguments(targetSpeaker0, resourceSpeaker0, null),
                    arguments(targetSpeaker1, resourceSpeaker0, ""),
                    arguments(targetSpeaker2, resourceSpeaker0, TARGET_SPEAKER_GIT_HUB),
                    arguments(targetSpeaker0, resourceSpeaker1, null),
                    arguments(targetSpeaker1, resourceSpeaker1, ""),
                    arguments(targetSpeaker2, resourceSpeaker1, TARGET_SPEAKER_GIT_HUB),
                    arguments(targetSpeaker0, resourceSpeaker2, RESOURCE_SPEAKER_GIT_HUB),
                    arguments(targetSpeaker1, resourceSpeaker2, RESOURCE_SPEAKER_GIT_HUB),
                    arguments(targetSpeaker2, resourceSpeaker2, TARGET_SPEAKER_GIT_HUB)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void fillSpeakerTwitter(Speaker targetSpeaker, Speaker resourceSpeaker,
                                String expected) {
            ConferenceDataLoader.fillSpeakerGitHub(targetSpeaker, resourceSpeaker);

            assertEquals(expected, targetSpeaker.getGitHub());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("fillSpeakerJavaChampion method tests")
    class FillSpeakerJavaChampionTest {
        private Stream<Arguments> data() {
            Speaker resourceSpeaker0 = new Speaker();
            resourceSpeaker0.setJavaChampion(false);

            Speaker resourceSpeaker1 = new Speaker();
            resourceSpeaker1.setJavaChampion(true);

            Speaker targetSpeaker0 = new Speaker();
            targetSpeaker0.setJavaChampion(false);

            Speaker targetSpeaker1 = new Speaker();
            targetSpeaker1.setJavaChampion(true);

            return Stream.of(
                    arguments(targetSpeaker0, resourceSpeaker0, false),
                    arguments(targetSpeaker1, resourceSpeaker0, true),
                    arguments(targetSpeaker0, resourceSpeaker1, true),
                    arguments(targetSpeaker1, resourceSpeaker1, true)
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
}
