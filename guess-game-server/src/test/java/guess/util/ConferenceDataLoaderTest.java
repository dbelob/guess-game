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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("ConferenceDataLoader class tests")
class ConferenceDataLoaderTest {
    @Test
    void loadEventTypes() {
        new MockUp<YamlUtils>() {
            @Mock
            SourceInformation readSourceInformation() throws SpeakerDuplicatedException, IOException {
                return new SourceInformation(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
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
                    arguments(JPOINT_CONFERENCE, EVENT_DATE, EVENT_CODE, LoadSettings.defaultSettings(),
                            new SourceInformation(
                                    List.of(place0),
                                    List.of(eventType0),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    List.of(speaker0),
                                    Collections.emptyList()),
                            event0,
                            List.of(talk0),
                            List.of(speaker0)),
                    arguments(JPOINT_CONFERENCE, LocalDate.of(2020, 6, 30), EVENT_CODE, LoadSettings.defaultSettings(),
                            new SourceInformation(
                                    List.of(place0),
                                    List.of(eventType0),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
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
                                    LoadSettings loadSettings, SourceInformation sourceInformation, Event contentfulEvent,
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
                                            LoadSettings loadSettings) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
                    invocation.proceed(conference, startDate, conferenceCode, loadSettings);
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

            assertDoesNotThrow(() -> ConferenceDataLoader.loadTalksSpeakersEvent(conference, startDate, conferenceCode, loadSettings));
        }
    }

    @Test
    void loadTalksSpeakersEventWithoutInvalidTalksSetAndKnownSpeakerIdsMap() {
        new MockUp<ConferenceDataLoader>() {
            @Mock
            void loadTalksSpeakersEvent(Conference conference, LocalDate startDate, String conferenceCode,
                                        LoadSettings loadSettings) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
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
        private Speaker createSpeaker(String twitter) {
            Speaker speaker = new Speaker();
            speaker.setTwitter(twitter);

            return speaker;
        }

        private Stream<Arguments> data() {
            final String RESOURCE_SPEAKER_TWITTER = "resourceSpeakerTwitter";
            final String TARGET_SPEAKER_TWITTER = "targetSpeakerTwitter";

            return Stream.of(
                    arguments(createSpeaker(null), createSpeaker(null), null),
                    arguments(createSpeaker(""), createSpeaker(null), ""),
                    arguments(createSpeaker(TARGET_SPEAKER_TWITTER), createSpeaker(null), TARGET_SPEAKER_TWITTER),
                    arguments(createSpeaker(null), createSpeaker(""), null),
                    arguments(createSpeaker(""), createSpeaker(""), ""),
                    arguments(createSpeaker(TARGET_SPEAKER_TWITTER), createSpeaker(""), TARGET_SPEAKER_TWITTER),
                    arguments(createSpeaker(null), createSpeaker(RESOURCE_SPEAKER_TWITTER), RESOURCE_SPEAKER_TWITTER),
                    arguments(createSpeaker(""), createSpeaker(RESOURCE_SPEAKER_TWITTER), RESOURCE_SPEAKER_TWITTER),
                    arguments(createSpeaker(TARGET_SPEAKER_TWITTER), createSpeaker(RESOURCE_SPEAKER_TWITTER), TARGET_SPEAKER_TWITTER)
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
        private Speaker createSpeaker(String gitHub) {
            Speaker speaker = new Speaker();
            speaker.setGitHub(gitHub);

            return speaker;
        }

        private Stream<Arguments> data() {
            final String RESOURCE_SPEAKER_GIT_HUB = "resourceSpeakerGitHub";
            final String TARGET_SPEAKER_GIT_HUB = "targetSpeakerGitHub";

            return Stream.of(
                    arguments(createSpeaker(null), createSpeaker(null), null),
                    arguments(createSpeaker(""), createSpeaker(null), ""),
                    arguments(createSpeaker(TARGET_SPEAKER_GIT_HUB), createSpeaker(null), TARGET_SPEAKER_GIT_HUB),
                    arguments(createSpeaker(null), createSpeaker(""), null),
                    arguments(createSpeaker(""), createSpeaker(""), ""),
                    arguments(createSpeaker(TARGET_SPEAKER_GIT_HUB), createSpeaker(""), TARGET_SPEAKER_GIT_HUB),
                    arguments(createSpeaker(null), createSpeaker(RESOURCE_SPEAKER_GIT_HUB), RESOURCE_SPEAKER_GIT_HUB),
                    arguments(createSpeaker(""), createSpeaker(RESOURCE_SPEAKER_GIT_HUB), RESOURCE_SPEAKER_GIT_HUB),
                    arguments(createSpeaker(TARGET_SPEAKER_GIT_HUB), createSpeaker(RESOURCE_SPEAKER_GIT_HUB), TARGET_SPEAKER_GIT_HUB)
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
            new MockUp<ContentfulUtils>() {
                @Mock
                boolean needUpdate(Talk a, Talk b) {
                    return ((a.getId() == 0) && (b.getId() == 0));
                }
            };

            new MockUp<ConferenceDataLoader>() {
                @Mock
                LoadResult<List<Talk>> getTalkLoadResult(Invocation invocation, List<Talk> talks, Event resourceEvent,
                                                         List<Event> resourceEvents, AtomicLong lastTalksId) {
                    return invocation.proceed(talks, resourceEvent, resourceEvents, lastTalksId);
                }

                @Mock
                Talk findResourceTalk(Talk talk,
                                      Map<String, Set<Talk>> resourceRuNameTalks,
                                      Map<String, Set<Talk>> resourceEnNameTalks) {
                    return ((talk.getId() == 0) || (talk.getId() == 1)) ? talk : null;
                }

                @Mock
                boolean needDeleteTalk(List<Talk> talks, Talk resourceTalk, List<Event> resourceEvents, Event resourceEvent) {
                    return true;
                }
            };

            assertEquals(expected, ConferenceDataLoader.getTalkLoadResult(talks, resourceEvent, resourceEvents, lasTalksId));
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
            event0.setEventType(eventType0);
            event0.setStartDate(LocalDate.of(2020, 10, 3));
            event0.setTalks(List.of(talk0));

            Event event1 = new Event();
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
            new MockUp<LocalizationUtils>() {
                @Mock
                String getString(List<LocaleItem> localeItems, Language language) {
                    return "";
                }
            };

            assertEquals(expected, ConferenceDataLoader.needDeleteTalk(talks, resourceTalk, resourceEvents, resourceEvent));
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
            new MockUp<ContentfulUtils>() {
                @Mock
                boolean needUpdate(Place a, Place b) {
                    return ((a.getId() == 0) && (b.getId() == 0));
                }
            };

            assertEquals(expected, ConferenceDataLoader.getPlaceLoadResult(place, resourcePlace, lastPlaceId));
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
            new MockUp<ContentfulUtils>() {
                @Mock
                boolean needUpdate(Event a, Event b) {
                    return ((a.getId() == 0) && (b.getId() == 0));
                }
            };

            assertEquals(expected, ConferenceDataLoader.getEventLoadResult(event, resourceEvent));
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
        void saveFiles(LoadResult<List<Company>> companyLoadResult, SpeakerLoadResult speakerLoadResult, LoadResult<List<Talk>> talkLoadResult,
                       LoadResult<Place> placeLoadResult, LoadResult<Event> eventLoadResult) {
            new MockUp<ConferenceDataLoader>() {
                @Mock
                void saveFiles(Invocation invocation, SpeakerLoadResult speakerLoadResult, LoadResult<List<Talk>> talkLoadResult,
                               LoadResult<Place> placeLoadResult, LoadResult<Event> eventLoadResult) throws IOException, NoSuchFieldException {
                    invocation.proceed(speakerLoadResult, talkLoadResult, placeLoadResult, eventLoadResult);
                }

                @Mock
                void saveCompanies(LoadResult<List<Company>> companyLoadResult) throws IOException, NoSuchFieldException {
                    // Nothing
                }

                @Mock
                void saveImages(SpeakerLoadResult speakerLoadResult) throws IOException {
                    // Nothing
                }

                @Mock
                void saveSpeakers(SpeakerLoadResult speakerLoadResult) throws IOException, NoSuchFieldException {
                    // Nothing
                }

                @Mock
                void saveTalks(LoadResult<List<Talk>> talkLoadResult) throws IOException, NoSuchFieldException {
                    // Nothing
                }

                @Mock
                void savePlaces(LoadResult<Place> placeLoadResult) throws IOException, NoSuchFieldException {
                    // Nothing
                }

                @Mock
                void saveEvents(LoadResult<Event> eventLoadResult) throws IOException, NoSuchFieldException {
                    // Nothing
                }
            };

            assertDoesNotThrow(() -> ConferenceDataLoader.saveFiles(companyLoadResult, speakerLoadResult, talkLoadResult, placeLoadResult, eventLoadResult));
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
            new MockUp<ConferenceDataLoader>() {
                @Mock
                void saveImages(Invocation invocation, SpeakerLoadResult speakerLoadResult) throws IOException {
                    invocation.proceed(speakerLoadResult);
                }

                @Mock
                void logAndCreateSpeakerImages(List<UrlFilename> urlFilenames, String logMessage) throws IOException {
                    // Nothing
                }
            };

            assertDoesNotThrow(() -> ConferenceDataLoader.saveImages(speakerLoadResult));
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
                            new ArrayList<>(List.of(speaker0)),
                            new ArrayList<>(List.of(speaker1))),
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
            new MockUp<ConferenceDataLoader>() {
                @Mock
                void saveSpeakers(Invocation invocation, SpeakerLoadResult speakerLoadResult) throws IOException, NoSuchFieldException {
                    invocation.proceed(speakerLoadResult);
                }

                @Mock
                void logAndDumpSpeakers(List<Speaker> speakers, String logMessage, String filename) throws IOException, NoSuchFieldException {
                    // Nothing
                }
            };

            assertDoesNotThrow(() -> ConferenceDataLoader.saveSpeakers(speakerLoadResult));
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
                    new ArrayList<>(List.of(talk0)),
                    new ArrayList<>(List.of(talk1)),
                    new ArrayList<>(List.of(talk2)));

            return Stream.of(
                    arguments(talkLoadResult0),
                    arguments(talkLoadResult1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void saveTalks(LoadResult<List<Talk>> talkLoadResult) {
            new MockUp<ConferenceDataLoader>() {
                @Mock
                void saveTalks(Invocation invocation, LoadResult<List<Talk>> talkLoadResult) throws IOException, NoSuchFieldException {
                    invocation.proceed(talkLoadResult);
                }

                @Mock
                void logAndDumpTalks(List<Talk> talks, String logMessage, String filename) throws IOException, NoSuchFieldException {
                    // Nothing
                }
            };

            assertDoesNotThrow(() -> ConferenceDataLoader.saveTalks(talkLoadResult));
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
        void savePlaces(LoadResult<Place> placeLoadResult) {
            new MockUp<ConferenceDataLoader>() {
                @Mock
                void savePlaces(Invocation invocation, LoadResult<Place> placeLoadResult) throws IOException, NoSuchFieldException {
                    invocation.proceed(placeLoadResult);
                }

                @Mock
                void dumpPlace(Place place, String filename) throws IOException, NoSuchFieldException {
                    // Nothing
                }
            };

            assertDoesNotThrow(() -> ConferenceDataLoader.savePlaces(placeLoadResult));
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
            void saveEvents(LoadResult<Event> eventLoadResult) {
                new MockUp<ConferenceDataLoader>() {
                    @Mock
                    void saveEvents(Invocation invocation, LoadResult<Event> eventLoadResult) throws IOException, NoSuchFieldException {
                        invocation.proceed(eventLoadResult);
                    }

                    @Mock
                    void dumpEvent(Event event, String filename) throws IOException, NoSuchFieldException {
                        // Nothing
                    }
                };

                assertDoesNotThrow(() -> ConferenceDataLoader.saveEvents(eventLoadResult));
            }
        }
    }

    @Test
    void logAndDumpEventTypes(@Mocked LocalizationUtils localizationUtilsMock, @Mocked YamlUtils yamlUtilsMock) {
        assertDoesNotThrow(() -> ConferenceDataLoader.logAndDumpEventTypes(List.of(new EventType()), "{}", "filename"));
    }

    @Test
    void logAndCreateSpeakerImages(@Mocked ImageUtils imageUtilsMock) {
        assertDoesNotThrow(() -> ConferenceDataLoader.logAndCreateSpeakerImages(List.of(new UrlFilename("url", "filename")), "{}"));
    }

    @Test
    void logAndDumpSpeakers(@Mocked YamlUtils yamlUtilsMock) {
        assertDoesNotThrow(() -> ConferenceDataLoader.logAndDumpSpeakers(List.of(new Speaker()), "{}", "filename"));
    }

    @Test
    void logAndDumpTalks(@Mocked YamlUtils yamlUtilsMock) {
        assertDoesNotThrow(() -> ConferenceDataLoader.logAndDumpTalks(List.of(new Talk()), "{}", "filename"));
    }

    @Test
    void dumpPlace(@Mocked YamlUtils yamlUtilsMock) {
        assertDoesNotThrow(() -> ConferenceDataLoader.dumpPlace(new Place(), "filename"));
    }

    @Test
    void dumpEvent(@Mocked YamlUtils yamlUtilsMock) {
        assertDoesNotThrow(() -> ConferenceDataLoader.dumpEvent(new Event(), "filename"));
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
            final String COMPANY_NAME0 = "Компания0";
            final String COMPANY_NAME1 = "Компания1";
            final String COMPANY_NAME2 = "Компания2";
            final String COMPANY_NAME3 = "Компания3";
            final String COMPANY_NAME4 = "Компания4";
            final String COMPANY_NAME5 = "Компания5";

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME0)));
            speaker0.setCompany(List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME0)));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME1)));
            speaker1.setCompany(List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME1)));

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);
            speaker2.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME2)));
            speaker2.setCompany(List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME2)));

            Speaker speaker3 = new Speaker();
            speaker3.setId(3);
            speaker3.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME3)));
            speaker3.setCompany(List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME3)));

            Speaker speaker4 = new Speaker();
            speaker4.setId(4);
            speaker4.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME4)));
            speaker4.setCompany(List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME4)));

            Speaker speaker5 = new Speaker();
            speaker5.setId(5);
            speaker5.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), SPEAKER_NAME5)));
            speaker5.setCompany(List.of(new LocaleItem(Language.RUSSIAN.getCode(), COMPANY_NAME5)));

            NameCompany nameCompany0 = new NameCompany(SPEAKER_NAME0, COMPANY_NAME0);
            NameCompany nameCompany1 = new NameCompany(SPEAKER_NAME1, COMPANY_NAME1);

            SpeakerLoadMaps speakerLoadMaps = new SpeakerLoadMaps(
                    Map.of(nameCompany0, 0L, nameCompany1, 1L),
                    Map.of(0L, speaker0),
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyMap());

            return Stream.of(
                    arguments(speaker0, speakerLoadMaps, null),
                    arguments(speaker1, speakerLoadMaps, NullPointerException.class),
                    arguments(speaker2, speakerLoadMaps, null),
                    arguments(speaker3, speakerLoadMaps, null),
                    arguments(speaker4, speakerLoadMaps, null),
                    arguments(speaker5, speakerLoadMaps, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void findResourceSpeaker(Speaker speaker, SpeakerLoadMaps speakerLoadMaps, Class<? extends Throwable> expectedException) {
            new MockUp<LocalizationUtils>() {
                @Mock
                String getString(List<LocaleItem> localeItems, Language language) {
                    return ((localeItems != null) && !localeItems.isEmpty()) ? localeItems.get(0).getText() : null;
                }
            };

            new MockUp<ConferenceDataLoader>() {
                @Mock
                Speaker findResourceSpeaker(Invocation invocation, Speaker speaker, SpeakerLoadMaps speakerLoadMaps) {
                    return invocation.proceed(speaker, speakerLoadMaps);
                }

                @Mock
                Speaker findResourceSpeakerByNameCompany(Speaker speaker, Map<NameCompany, Speaker> resourceNameCompanySpeakers, Language language) {
                    return (((speaker.getId() == 2) && Language.ENGLISH.equals(language)) ||
                            ((speaker.getId() == 3) && Language.RUSSIAN.equals(language))) ? speaker : null;
                }

                @Mock
                Speaker findResourceSpeakerByName(Speaker speaker, Map<String, Set<Speaker>> resourceNameSpeakers, Language language) {
                    return (((speaker.getId() == 4) && Language.ENGLISH.equals(language) ||
                            (speaker.getId() == 5) && Language.RUSSIAN.equals(language))) ? speaker : null;
                }
            };

            if (expectedException == null) {
                assertDoesNotThrow(() -> ConferenceDataLoader.findResourceSpeaker(speaker, speakerLoadMaps));
            } else {
                assertThrows(expectedException, () -> ConferenceDataLoader.findResourceSpeaker(speaker, speakerLoadMaps));
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
            new MockUp<ConferenceDataLoader>() {
                @Mock
                Talk findResourceTalk(Invocation invocation, Talk talk,
                                      Map<String, Set<Talk>> resourceRuNameTalks,
                                      Map<String, Set<Talk>> resourceEnNameTalks) {
                    return invocation.proceed(talk, resourceRuNameTalks, resourceEnNameTalks);
                }

                @Mock
                Talk findResourceTalkByName(Talk talk, Map<String, Set<Talk>> resourceNameTalks, Language language) {
                    return (talk.getId() == 0) ? talk : null;
                }
            };

            assertEquals(expected, ConferenceDataLoader.findResourceTalk(talk, resourceRuNameTalks, resourceEnNameTalks));
        }
    }

    @Test
    void findResourceSpeakerByNameCompany() {
        Speaker speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
        speaker0.setCompany(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company0")));

        NameCompany nameCompany0 = new NameCompany("Name0", "Company0");

        Map<NameCompany, Speaker> resourceNameCompanySpeakers0 = Map.of(nameCompany0, speaker0);

        new MockUp<LocalizationUtils>() {
            @Mock
            String getString(List<LocaleItem> localeItems, Language language) {
                return ((localeItems != null) && !localeItems.isEmpty()) ? localeItems.get(0).getText() : null;
            }
        };

        assertEquals(speaker0, ConferenceDataLoader.findResourceSpeakerByNameCompany(speaker0, resourceNameCompanySpeakers0, Language.ENGLISH));
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
                    arguments(speaker1, resourceNameSpeakers0, Language.ENGLISH, null, null),
                    arguments(speaker2, resourceNameSpeakers0, Language.ENGLISH, null, IllegalStateException.class),
                    arguments(speaker3, resourceNameSpeakers0, Language.ENGLISH, null, null),
                    arguments(speaker0, resourceNameSpeakers0, Language.ENGLISH, speaker0, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void findResourceSpeakerByName(Speaker speaker, Map<String, Set<Speaker>> resourceNameSpeakers, Language language,
                                       Speaker expected, Class<? extends Throwable> expectedException) {
            new MockUp<LocalizationUtils>() {
                @Mock
                String getString(List<LocaleItem> localeItems, Language language) {
                    return ((localeItems != null) && !localeItems.isEmpty()) ? localeItems.get(0).getText() : null;
                }
            };

            if (expectedException == null) {
                assertEquals(expected, ConferenceDataLoader.findResourceSpeakerByName(speaker, resourceNameSpeakers, language));
            } else {
                assertThrows(expectedException, () -> ConferenceDataLoader.findResourceSpeakerByName(speaker, resourceNameSpeakers, language));
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
        void findResourceTalkByName(Talk talk, Map<String, Set<Talk>> resourceNameTalks, Language language,
                                    Talk expected, Class<? extends Throwable> expectedException) {
            new MockUp<LocalizationUtils>() {
                @Mock
                String getString(List<LocaleItem> localeItems, Language language) {
                    return ((localeItems != null) && !localeItems.isEmpty()) ? localeItems.get(0).getText() : null;
                }
            };

            if (expectedException == null) {
                assertEquals(expected, ConferenceDataLoader.findResourceTalkByName(talk, resourceNameTalks, language));
            } else {
                assertThrows(expectedException, () -> ConferenceDataLoader.findResourceTalkByName(talk, resourceNameTalks, language));
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
        void findResourcePlaceByCityVenueAddress(Place place, Map<CityVenueAddress, Place> resourceCityVenueAddressPlaces,
                                                 Language language, Place expected) {
            new MockUp<LocalizationUtils>() {
                @Mock
                String getString(List<LocaleItem> localeItems, Language language) {
                    return ((localeItems != null) && !localeItems.isEmpty()) ? localeItems.get(0).getText() : null;
                }
            };

            assertEquals(expected, ConferenceDataLoader.findResourcePlaceByCityVenueAddress(place, resourceCityVenueAddressPlaces, language));
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
            new MockUp<ConferenceDataLoader>() {
                @Mock
                Place findResourcePlace(Invocation invocation, Place place,
                                        Map<CityVenueAddress, Place> resourceRuCityVenueAddressPlaces,
                                        Map<CityVenueAddress, Place> resourceEnCityVenueAddressPlaces) {
                    return invocation.proceed(place, resourceRuCityVenueAddressPlaces, resourceEnCityVenueAddressPlaces);
                }

                @Mock
                Place findResourcePlaceByCityVenueAddress(Place place, Map<CityVenueAddress, Place> resourceCityVenueAddressPlaces,
                                                          Language language) {
                    if (place != null) {
                        return (place.getId() == 0) ? place : null;
                    } else {
                        return null;
                    }
                }
            };

            assertEquals(expected, ConferenceDataLoader.findResourcePlace(place, resourceRuCityVenueAddressPlaces, resourceEnCityVenueAddressPlaces));
        }
    }

    @Test
    void fixVenueAddress() {
        new MockUp<ConferenceDataLoader>() {
            @Mock
            List<LocaleItem> fixVenueAddress(Invocation invocation, Place place) {
                return invocation.proceed(place);
            }

            @Mock
            String getFixedVenueAddress(String city, String venueAddress, List<FixingVenueAddress> fixingVenueAddresses) {
                return "";
            }
        };

        assertDoesNotThrow(() -> ConferenceDataLoader.fixVenueAddress(new Place()));
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
    void main() {
        assertDoesNotThrow(() -> ConferenceDataLoader.main(new String[]{}));
    }

    @Test
    void classDeclaration() {
        assertDoesNotThrow(ConferenceDataLoader::new);
    }
}
