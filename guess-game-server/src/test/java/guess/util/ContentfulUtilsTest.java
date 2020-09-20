package guess.util;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.contentful.ContentfulIncludes;
import guess.domain.source.contentful.ContentfulLink;
import guess.domain.source.contentful.ContentfulResponse;
import guess.domain.source.contentful.ContentfulSys;
import guess.domain.source.contentful.asset.ContentfulAsset;
import guess.domain.source.contentful.asset.ContentfulAssetFields;
import guess.domain.source.contentful.asset.ContentfulAssetFieldsFile;
import guess.domain.source.contentful.city.ContentfulCity;
import guess.domain.source.contentful.city.ContentfulCityFields;
import guess.domain.source.contentful.event.ContentfulEvent;
import guess.domain.source.contentful.event.ContentfulEventFields;
import guess.domain.source.contentful.event.ContentfulEventResponse;
import guess.domain.source.contentful.eventtype.ContentfulEventType;
import guess.domain.source.contentful.eventtype.ContentfulEventTypeFields;
import guess.domain.source.contentful.eventtype.ContentfulEventTypeResponse;
import guess.domain.source.contentful.locale.ContentfulLocale;
import guess.domain.source.contentful.locale.ContentfulLocaleResponse;
import guess.domain.source.contentful.speaker.ContentfulSpeaker;
import guess.domain.source.contentful.speaker.ContentfulSpeakerFields;
import guess.domain.source.contentful.speaker.ContentfulSpeakerResponse;
import guess.domain.source.extract.ExtractPair;
import guess.domain.source.extract.ExtractSet;
import mockit.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("ContentfulUtils class tests")
class ContentfulUtilsTest {
    @Test
    void getLocales(@Mocked RestTemplate restTemplateMock) throws URISyntaxException {
        new Expectations() {{
            ContentfulLocale locale0 = new ContentfulLocale();
            locale0.setCode("en");

            ContentfulLocale locale1 = new ContentfulLocale();
            locale1.setCode("ru-RU");

            ContentfulLocaleResponse response = new ContentfulLocaleResponse();
            response.setItems(List.of(locale0, locale1));

            restTemplateMock.getForObject(withAny(new URI("https://valid.com")), ContentfulLocaleResponse.class);
            result = response;
        }};

        assertEquals(List.of("en", "ru-RU"), ContentfulUtils.getLocales());
    }

    @Test
    void getEventTypes(@Mocked RestTemplate restTemplateMock) throws URISyntaxException {
        new Expectations() {{
            ContentfulEventTypeResponse response = new ContentfulEventTypeResponse();
            response.setItems(List.of(new ContentfulEventType(), new ContentfulEventType()));

            restTemplateMock.getForObject(withAny(new URI("https://valid.com")), ContentfulEventTypeResponse.class);
            result = response;
        }};

        new MockUp<ContentfulUtils>() {
            @Mock
            List<EventType> getEventTypes(Invocation invocation) {
                return invocation.proceed();
            }

            @Mock
            EventType createEventType(ContentfulEventType contentfulEventType, AtomicLong id) {
                return new EventType();
            }
        };

        assertEquals(2, ContentfulUtils.getEventTypes().size());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("createEventType method tests")
    class CreateEventTypeTest {
        private Stream<Arguments> data() {
            final String VK_LINK = "https://vk.com";
            final String TWITTER_LINK = "https://twitter.com";
            final String FACEBOOK_LINK = "https://twitter.com";
            final String YOUTUBE_LINK = "https://youtube.com";
            final String TELEGRAM_LINK = "https://telegram.org";

            ContentfulEventTypeFields contentfulEventTypeFields0 = new ContentfulEventTypeFields();
            contentfulEventTypeFields0.setEventName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "Name0"));
            contentfulEventTypeFields0.setEventDescriptions(Collections.emptyMap());
            contentfulEventTypeFields0.setSiteLink(Collections.emptyMap());

            ContentfulEventTypeFields contentfulEventTypeFields1 = new ContentfulEventTypeFields();
            contentfulEventTypeFields1.setEventName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "Name1"));
            contentfulEventTypeFields1.setEventDescriptions(Collections.emptyMap());
            contentfulEventTypeFields1.setSiteLink(Collections.emptyMap());
            contentfulEventTypeFields1.setVkLink(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, VK_LINK));

            ContentfulEventTypeFields contentfulEventTypeFields2 = new ContentfulEventTypeFields();
            contentfulEventTypeFields2.setEventName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "Name2"));
            contentfulEventTypeFields2.setEventDescriptions(Collections.emptyMap());
            contentfulEventTypeFields2.setSiteLink(Collections.emptyMap());
            contentfulEventTypeFields2.setTwLink(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, TWITTER_LINK));

            ContentfulEventTypeFields contentfulEventTypeFields3 = new ContentfulEventTypeFields();
            contentfulEventTypeFields3.setEventName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "Name3"));
            contentfulEventTypeFields3.setEventDescriptions(Collections.emptyMap());
            contentfulEventTypeFields3.setSiteLink(Collections.emptyMap());
            contentfulEventTypeFields3.setFbLink(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, FACEBOOK_LINK));

            ContentfulEventTypeFields contentfulEventTypeFields4 = new ContentfulEventTypeFields();
            contentfulEventTypeFields4.setEventName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "Name3"));
            contentfulEventTypeFields4.setEventDescriptions(Collections.emptyMap());
            contentfulEventTypeFields4.setSiteLink(Collections.emptyMap());
            contentfulEventTypeFields4.setYoutubeLink(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, YOUTUBE_LINK));

            ContentfulEventTypeFields contentfulEventTypeFields5 = new ContentfulEventTypeFields();
            contentfulEventTypeFields5.setEventName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "Name3"));
            contentfulEventTypeFields5.setEventDescriptions(Collections.emptyMap());
            contentfulEventTypeFields5.setSiteLink(Collections.emptyMap());
            contentfulEventTypeFields5.setTelegramLink(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, TELEGRAM_LINK));

            ContentfulEventType contentfulEventType0 = new ContentfulEventType();
            contentfulEventType0.setFields(contentfulEventTypeFields0);

            ContentfulEventType contentfulEventType1 = new ContentfulEventType();
            contentfulEventType1.setFields(contentfulEventTypeFields1);

            ContentfulEventType contentfulEventType2 = new ContentfulEventType();
            contentfulEventType2.setFields(contentfulEventTypeFields2);

            ContentfulEventType contentfulEventType3 = new ContentfulEventType();
            contentfulEventType3.setFields(contentfulEventTypeFields3);

            ContentfulEventType contentfulEventType4 = new ContentfulEventType();
            contentfulEventType4.setFields(contentfulEventTypeFields4);

            ContentfulEventType contentfulEventType5 = new ContentfulEventType();
            contentfulEventType5.setFields(contentfulEventTypeFields5);

            EventType eventType0 = new EventType();
            eventType0.setId(-1);

            EventType eventType1 = new EventType();
            eventType1.setId(-1);
            eventType1.setVkLink(VK_LINK);

            EventType eventType2 = new EventType();
            eventType2.setId(-1);
            eventType2.setTwitterLink(TWITTER_LINK);

            EventType eventType3 = new EventType();
            eventType3.setId(-1);
            eventType3.setFacebookLink(FACEBOOK_LINK);

            EventType eventType4 = new EventType();
            eventType4.setId(-1);
            eventType4.setYoutubeLink(YOUTUBE_LINK);

            EventType eventType5 = new EventType();
            eventType5.setId(-1);
            eventType5.setTelegramLink(TELEGRAM_LINK);

            return Stream.of(
                    arguments(contentfulEventType0, new AtomicLong(-1), eventType0),
                    arguments(contentfulEventType1, new AtomicLong(-1), eventType1),
                    arguments(contentfulEventType2, new AtomicLong(-1), eventType2),
                    arguments(contentfulEventType3, new AtomicLong(-1), eventType3),
                    arguments(contentfulEventType4, new AtomicLong(-1), eventType4),
                    arguments(contentfulEventType5, new AtomicLong(-1), eventType5)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void createEventType(ContentfulEventType contentfulEventType, AtomicLong id, EventType expected) {
            EventType actual = ContentfulUtils.createEventType(contentfulEventType, id);

            assertEquals(expected, actual);
            assertEquals(expected.getVkLink(), actual.getVkLink());
            assertEquals(expected.getTwitterLink(), actual.getTwitterLink());
            assertEquals(expected.getFacebookLink(), actual.getFacebookLink());
            assertEquals(expected.getYoutubeLink(), actual.getYoutubeLink());
            assertEquals(expected.getTelegramLink(), actual.getTelegramLink());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getFirstMapValue method tests")
    class GetFirstMapValueTest {
        private Stream<Arguments> data() {
            Map<String, String> map0 = Map.of("key1", "value1");

            Map<String, String> map1 = new LinkedHashMap<>();
            map1.put("key1", "value1");
            map1.put("key2", "value2");

            Map<String, String> map2 = new LinkedHashMap<>();
            map2.put("key2", "value2");
            map2.put("key1", "value1");

            return Stream.of(
                    arguments(map0, "value1"),
                    arguments(map1, "value1"),
                    arguments(map2, "value2")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getFirstMapValue(Map<String, String> map, String expected) {
            assertEquals(expected, ContentfulUtils.getFirstMapValue(map));
        }
    }

    @Test
    void getEvents(@Mocked RestTemplate restTemplateMock) throws URISyntaxException {
        new Expectations() {{
            ContentfulEventResponse response = new ContentfulEventResponse();
            response.setItems(List.of(new ContentfulEvent(), new ContentfulEvent()));

            restTemplateMock.getForObject(withAny(new URI("https://valid.com")), ContentfulEventResponse.class);
            result = response;
        }};

        new MockUp<ContentfulUtils>() {
            @Mock
            List<Event> getEvents(Invocation invocation, String eventName, LocalDate startDate) {
                return invocation.proceed(eventName, startDate);
            }

            @Mock
            Event createEvent(ContentfulEvent e, Map<String, ContentfulCity> cityMap, Set<String> entryErrorSet) {
                return new Event();
            }

            @Mock
            Map<String, ContentfulCity> getCityMap(ContentfulEventResponse response) {
                return Collections.emptyMap();
            }

            @Mock
            Set<String> getEntryErrorSet(ContentfulResponse<?, ? extends ContentfulIncludes> response) {
                return Collections.emptySet();
            }
        };

        assertEquals(2, ContentfulUtils.getEvents("JPoint", LocalDate.of(2020, 6, 29)).size());
        assertEquals(2, ContentfulUtils.getEvents(null, LocalDate.of(2020, 6, 29)).size());
        assertEquals(2, ContentfulUtils.getEvents("", LocalDate.of(2020, 6, 29)).size());
        assertEquals(2, ContentfulUtils.getEvents("JPoint", null).size());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("createUtcZonedDateTime method tests")
    class CreateUtcZonedDateTimeTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(LocalDate.of(2020, 1, 1), ZonedDateTime.of(2019, 12, 31, 21, 0, 0, 0, ZoneId.of("UTC"))),
                    arguments(LocalDate.of(2020, 12, 31), ZonedDateTime.of(2020, 12, 30, 21, 0, 0, 0, ZoneId.of("UTC")))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void createUtcZonedDateTime(LocalDate localDate, ZonedDateTime expected) {
            assertEquals(expected, ContentfulUtils.createUtcZonedDateTime(localDate));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("createEvent method tests")
    class CreateEventTest {
        private Stream<Arguments> data() {
            final String YOUTUBE_PLAY_LIST = "https://youtube.com";
            final String MAP_COORDINATES = "59.762236, 30.356121";

            ContentfulSys contentfulSys0 = new ContentfulSys();
            contentfulSys0.setId("sys0");

            ContentfulSys contentfulSys1 = new ContentfulSys();
            contentfulSys1.setId("sys1");

            ContentfulSys contentfulSys2 = new ContentfulSys();
            contentfulSys2.setId("sys2");

            ContentfulSys contentfulSys3 = new ContentfulSys();
            contentfulSys3.setId("sys3");

            ContentfulSys contentfulSys4 = new ContentfulSys();
            contentfulSys4.setId("sys4");

            ContentfulLink contentfulLink0 = new ContentfulLink();
            contentfulLink0.setSys(contentfulSys0);

            ContentfulLink contentfulLink1 = new ContentfulLink();
            contentfulLink1.setSys(contentfulSys1);

            ContentfulLink contentfulLink2 = new ContentfulLink();
            contentfulLink2.setSys(contentfulSys2);

            ContentfulLink contentfulLink3 = new ContentfulLink();
            contentfulLink3.setSys(contentfulSys3);

            ContentfulLink contentfulLink4 = new ContentfulLink();
            contentfulLink4.setSys(contentfulSys4);

            ContentfulEventFields contentfulEventFields0 = new ContentfulEventFields();
            contentfulEventFields0.setConferenceName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "Event Name0"));
            contentfulEventFields0.setEventStart(Map.of(ContentfulUtils.ENGLISH_LOCALE, "2020-01-01T00:00+03:00"));
            contentfulEventFields0.setEventCity(Map.of(ContentfulUtils.ENGLISH_LOCALE, contentfulLink0));

            ContentfulEventFields contentfulEventFields1 = new ContentfulEventFields();
            contentfulEventFields1.setConferenceName(Map.of(
                    ContentfulUtils.RUSSIAN_LOCALE, "Наименование события1"));
            contentfulEventFields1.setEventStart(Map.of(ContentfulUtils.ENGLISH_LOCALE, "2020-01-01T00:00+03:00"));
            contentfulEventFields1.setEventCity(Map.of(ContentfulUtils.ENGLISH_LOCALE, contentfulLink1));

            ContentfulEventFields contentfulEventFields2 = new ContentfulEventFields();
            contentfulEventFields2.setConferenceName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "Event Name2"));
            contentfulEventFields2.setEventStart(Map.of(ContentfulUtils.ENGLISH_LOCALE, "2020-01-01T00:00+03:00"));
            contentfulEventFields2.setEventEnd(Map.of(ContentfulUtils.ENGLISH_LOCALE, "2020-01-02T00:00+03:00"));
            contentfulEventFields2.setEventCity(Map.of(ContentfulUtils.ENGLISH_LOCALE, contentfulLink2));

            ContentfulEventFields contentfulEventFields3 = new ContentfulEventFields();
            contentfulEventFields3.setConferenceName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "Event Name3"));
            contentfulEventFields3.setEventStart(Map.of(ContentfulUtils.ENGLISH_LOCALE, "2020-01-01T00:00+03:00"));
            contentfulEventFields3.setEventCity(Map.of(ContentfulUtils.ENGLISH_LOCALE, contentfulLink3));
            contentfulEventFields3.setYoutubePlayList(Map.of(ContentfulUtils.ENGLISH_LOCALE, YOUTUBE_PLAY_LIST));

            ContentfulEventFields contentfulEventFields4 = new ContentfulEventFields();
            contentfulEventFields4.setConferenceName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "Event Name4"));
            contentfulEventFields4.setEventStart(Map.of(ContentfulUtils.ENGLISH_LOCALE, "2020-01-01T00:00+03:00"));
            contentfulEventFields4.setEventCity(Map.of(ContentfulUtils.ENGLISH_LOCALE, contentfulLink4));
            contentfulEventFields4.setAddressLink(Map.of(ContentfulUtils.ENGLISH_LOCALE, MAP_COORDINATES));

            ContentfulEvent contentfulEvent0 = new ContentfulEvent();
            contentfulEvent0.setFields(contentfulEventFields0);

            ContentfulEvent contentfulEvent1 = new ContentfulEvent();
            contentfulEvent1.setFields(contentfulEventFields1);

            ContentfulEvent contentfulEvent2 = new ContentfulEvent();
            contentfulEvent2.setFields(contentfulEventFields2);

            ContentfulEvent contentfulEvent3 = new ContentfulEvent();
            contentfulEvent3.setFields(contentfulEventFields3);

            ContentfulEvent contentfulEvent4 = new ContentfulEvent();
            contentfulEvent4.setFields(contentfulEventFields4);

            // Events
            Event event0 = new Event();
            event0.setId(-1);
            event0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Event Name0")));
            event0.setStartDate(LocalDate.of(2020, 1, 1));
            event0.setEndDate(LocalDate.of(2020, 1, 1));

            Event event1 = new Event();
            event1.setId(-1);
            event1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Наименование события1")));
            event1.setStartDate(LocalDate.of(2020, 1, 1));
            event1.setEndDate(LocalDate.of(2020, 1, 1));

            Event event2 = new Event();
            event2.setId(-1);
            event2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Event Name2")));
            event2.setStartDate(LocalDate.of(2020, 1, 1));
            event2.setEndDate(LocalDate.of(2020, 1, 2));

            Event event3 = new Event();
            event3.setId(-1);
            event3.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Event Name3")));
            event3.setStartDate(LocalDate.of(2020, 1, 1));
            event3.setEndDate(LocalDate.of(2020, 1, 1));
            event3.setYoutubeLink(YOUTUBE_PLAY_LIST);

            Place place4 = new Place();
            place4.setMapCoordinates(MAP_COORDINATES);

            Event event4 = new Event();
            event4.setId(-1);
            event4.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Event Name4")));
            event4.setStartDate(LocalDate.of(2020, 1, 1));
            event4.setEndDate(LocalDate.of(2020, 1, 1));
            event4.setPlace(place4);

            // Cities
            ContentfulCityFields contentfulCityFields0 = new ContentfulCityFields();
            contentfulCityFields0.setCityName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "City Name0"));

            ContentfulCityFields contentfulCityFields1 = new ContentfulCityFields();
            contentfulCityFields1.setCityName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "City Name1"));

            ContentfulCityFields contentfulCityFields2 = new ContentfulCityFields();
            contentfulCityFields2.setCityName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "City Name2"));

            ContentfulCityFields contentfulCityFields3 = new ContentfulCityFields();
            contentfulCityFields3.setCityName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "City Name3"));

            ContentfulCityFields contentfulCityFields4 = new ContentfulCityFields();
            contentfulCityFields4.setCityName(Map.of(
                    ContentfulUtils.ENGLISH_LOCALE, "City Name4"));

            ContentfulCity contentfulCity0 = new ContentfulCity();
            contentfulCity0.setSys(contentfulSys0);
            contentfulCity0.setFields(contentfulCityFields0);

            ContentfulCity contentfulCity1 = new ContentfulCity();
            contentfulCity1.setSys(contentfulSys1);
            contentfulCity1.setFields(contentfulCityFields1);

            ContentfulCity contentfulCity2 = new ContentfulCity();
            contentfulCity2.setSys(contentfulSys1);
            contentfulCity2.setFields(contentfulCityFields2);

            ContentfulCity contentfulCity3 = new ContentfulCity();
            contentfulCity3.setSys(contentfulSys1);
            contentfulCity3.setFields(contentfulCityFields3);

            ContentfulCity contentfulCity4 = new ContentfulCity();
            contentfulCity4.setSys(contentfulSys1);
            contentfulCity4.setFields(contentfulCityFields4);

            Map<String, ContentfulCity> cityMap = Map.of("sys0", contentfulCity0, "sys1", contentfulCity1,
                    "sys2", contentfulCity2, "sys3", contentfulCity3, "sys4", contentfulCity4);
            Set<String> entryErrorSet = Collections.emptySet();

            return Stream.of(
                    arguments(contentfulEvent0, cityMap, entryErrorSet, event0),
                    arguments(contentfulEvent1, cityMap, entryErrorSet, event1),
                    arguments(contentfulEvent2, cityMap, entryErrorSet, event2),
                    arguments(contentfulEvent3, cityMap, entryErrorSet, event3),
                    arguments(contentfulEvent4, cityMap, entryErrorSet, event4)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void createEvent(ContentfulEvent contentfulEvent, Map<String, ContentfulCity> cityMap, Set<String> entryErrorSet, Event expected) {
            Event actual = ContentfulUtils.createEvent(contentfulEvent, cityMap, entryErrorSet);

            assertEquals(expected, actual);
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getEndDate(), actual.getEndDate());
            assertEquals(expected.getYoutubeLink(), actual.getYoutubeLink());

            String expectedMapCoordinates = (expected.getPlace() != null) ? expected.getPlace().getMapCoordinates() : null;
            String actualMapCoordinates = (actual.getPlace() != null) ? actual.getPlace().getMapCoordinates() : null;
            assertEquals(expectedMapCoordinates, actualMapCoordinates);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("createEventLocalDate method tests")
    class CreateEventLocalDateTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments("2020-01-01T00:00+03:00", LocalDate.of(2020, 1, 1)),
                    arguments("2020-12-31T00:00+03:00", LocalDate.of(2020, 12, 31))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void createUtcZonedDateTime(String zonedDateTimeString, LocalDate expected) {
            assertEquals(expected, ContentfulUtils.createEventLocalDate(zonedDateTimeString));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getEvent method tests")
    class GetEventTest {
        private Stream<Arguments> data() {
            Event event0 = new Event(
                    -1L,
                    null,
                    List.of(
                            new LocaleItem(Language.ENGLISH.getCode(), "Event Name0"),
                            new LocaleItem(Language.RUSSIAN.getCode(), "Наименование события0")),
                    new Event.EventDates(
                            LocalDate.of(2016, 12, 7),
                            LocalDate.of(2016, 12, 7)
                    ),
                    new Event.EventLinks(Collections.emptyList(), null),
                    new Place(),
                    Collections.emptyList());

            Event event1 = new Event(
                    -1L,
                    null,
                    List.of(
                            new LocaleItem(Language.ENGLISH.getCode(), "Event Name1"),
                            new LocaleItem(Language.RUSSIAN.getCode(), "Наименование события1")),
                    new Event.EventDates(
                            LocalDate.of(2017, 12, 7),
                            LocalDate.of(2017, 12, 7)
                    ),
                    new Event.EventLinks(Collections.emptyList(), null),
                    new Place(),
                    Collections.emptyList());

            return Stream.of(
                    arguments(Conference.DOT_NEXT, LocalDate.of(2016, 12, 7), Collections.emptyList(), null, event0),
                    arguments(Conference.DOT_NEXT, LocalDate.of(2017, 12, 7), Collections.emptyList(), IllegalStateException.class, null),
                    arguments(Conference.DOT_NEXT, LocalDate.of(2017, 12, 7), List.of(event0, event1), IllegalStateException.class, null),
                    arguments(Conference.DOT_NEXT, LocalDate.of(2017, 12, 7), List.of(event0), null, event0)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getEvent(Conference conference, LocalDate startDate, List<Event> events, Class<? extends Throwable> expectedException, Event expectedEvent) {
            new MockUp<ContentfulUtils>() {
                @Mock
                List<Event> getEvents(String eventName, LocalDate startDate) {
                    return events;
                }

                @Mock
                Event getEvent(Invocation invocation, Conference conference, LocalDate startDate) {
                    return invocation.proceed(conference, startDate);
                }

                @Mock
                Event fixNonexistentEventError(Conference conference, LocalDate startDate) {
                    if (Conference.DOT_NEXT.equals(conference) && LocalDate.of(2016, 12, 7).equals(startDate)) {
                        return new Event(
                                -1L,
                                null,
                                List.of(
                                        new LocaleItem("en", "Event Name0"),
                                        new LocaleItem("ru", "Наименование события0")),
                                new Event.EventDates(
                                        LocalDate.of(2016, 12, 7),
                                        LocalDate.of(2016, 12, 7)
                                ),
                                new Event.EventLinks(Collections.emptyList(), null),
                                new Place(),
                                Collections.emptyList());
                    } else {
                        return null;
                    }
                }
            };

            if (expectedException == null) {
                Event event = ContentfulUtils.getEvent(conference, startDate);

                assertEquals(expectedEvent, event);
                assertEquals(expectedEvent.getName(), event.getName());
                assertEquals(expectedEvent.getStartDate(), event.getStartDate());
                assertEquals(expectedEvent.getEndDate(), event.getEndDate());
            } else {
                assertThrows(expectedException, () -> ContentfulUtils.getEvent(conference, startDate));
            }
        }
    }

    @Test
    void getSpeakers(@Mocked RestTemplate restTemplateMock) throws URISyntaxException {
        new Expectations() {{
            ContentfulSpeakerResponse response = new ContentfulSpeakerResponse();
            response.setItems(List.of(new ContentfulSpeaker(), new ContentfulSpeaker()));

            restTemplateMock.getForObject(withAny(new URI("https://valid.com")), ContentfulSpeakerResponse.class);
            result = response;
        }};

        new MockUp<ContentfulUtils>() {
            @Mock
            List<Speaker> getSpeakers(Invocation invocation, ContentfulUtils.ConferenceSpaceInfo conferenceSpaceInfo, String conferenceCode) {
                return invocation.proceed(conferenceSpaceInfo, conferenceCode);
            }

            @Mock
            Speaker createSpeaker(ContentfulSpeaker contentfulSpeaker, Map<String, ContentfulAsset> assetMap,
                                  Set<String> assetErrorSet, AtomicLong id, boolean checkEnTextExistence) {
                return new Speaker();
            }

            @Mock
            Map<String, ContentfulAsset> getAssetMap(ContentfulResponse<?, ? extends ContentfulIncludes> response) {
                return Collections.emptyMap();
            }

            @Mock
            Set<String> getAssetErrorSet(ContentfulResponse<?, ? extends ContentfulIncludes> response) {
                return Collections.emptySet();
            }
        };

        assertEquals(2, ContentfulUtils.getSpeakers(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "code").size());
        assertEquals(2, ContentfulUtils.getSpeakers(ContentfulUtils.ConferenceSpaceInfo.HOLY_JS_SPACE_INFO, "code").size());
        assertEquals(2, ContentfulUtils.getSpeakers(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, null).size());
        assertEquals(2, ContentfulUtils.getSpeakers(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "").size());
    }

    @Test
    void createSpeaker() {
        new MockUp<ContentfulUtils>() {
            @Mock
            Speaker createSpeaker(Invocation invocation, ContentfulSpeaker contentfulSpeaker, Map<String, ContentfulAsset> assetMap,
                                  Set<String> assetErrorSet, AtomicLong id, boolean checkEnTextExistence) {
                return invocation.proceed(contentfulSpeaker, assetMap, assetErrorSet, id, checkEnTextExistence);
            }

            @Mock
            String extractPhoto(ContentfulLink link, Map<String, ContentfulAsset> assetMap,
                                Set<String> assetErrorSet, String speakerNameEn) {
                return null;
            }

            @Mock
            String extractTwitter(String value) {
                return null;
            }

            @Mock
            String extractGitHub(String value) {
                return null;
            }

            @Mock
            boolean extractBoolean(Boolean value) {
                return true;
            }

            @Mock
            List<LocaleItem> extractLocaleItems(String enText, String ruText, boolean checkEnTextExistence) {
                return Collections.emptyList();
            }
        };

        ContentfulSpeaker contentfulSpeaker = new ContentfulSpeaker();
        contentfulSpeaker.setFields(new ContentfulSpeakerFields());

        Map<String, ContentfulAsset> assetMap = Collections.emptyMap();
        Set<String> assetErrorSet = Collections.emptySet();
        AtomicLong id = new AtomicLong(42);

        Speaker speaker = new Speaker();
        speaker.setId(42);

        assertEquals(42, ContentfulUtils.createSpeaker(contentfulSpeaker, assetMap, assetErrorSet, id, true).getId());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractPhoto method tests")
    class ExtractPhotoTest {
        private static final String ASSET_URL = "https://valid.com";

        private Stream<Arguments> data() {
            ContentfulSys contentfulSys0 = new ContentfulSys();
            contentfulSys0.setId("id0");

            ContentfulSys contentfulSys1 = new ContentfulSys();
            contentfulSys1.setId("id1");

            ContentfulSys contentfulSys2 = new ContentfulSys();
            contentfulSys2.setId("id2");

            ContentfulAssetFields contentfulAssetFields2 = new ContentfulAssetFields();
            contentfulAssetFields2.setFile(new ContentfulAssetFieldsFile());

            ContentfulAsset contentfulAsset2 = new ContentfulAsset();
            contentfulAsset2.setFields(contentfulAssetFields2);

            Map<String, ContentfulAsset> assetMap2 = Map.of("id2", contentfulAsset2);

            ContentfulLink link0 = new ContentfulLink();
            link0.setSys(contentfulSys0);

            ContentfulLink link1 = new ContentfulLink();
            link1.setSys(contentfulSys1);

            ContentfulLink link2 = new ContentfulLink();
            link2.setSys(contentfulSys2);

            return Stream.of(
                    arguments(link0, Collections.emptyMap(), Set.of("id0"), "Name0", null, null),
                    arguments(link1, Collections.emptyMap(), Collections.emptySet(), "Name1", NullPointerException.class, null),
                    arguments(link2, assetMap2, Collections.emptySet(), "Name2", null, ASSET_URL)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractPhoto(ContentfulLink link, Map<String, ContentfulAsset> assetMap, Set<String> assetErrorSet,
                          String speakerNameEn, Class<? extends Throwable> expectedException, String expectedValue) {
            new MockUp<ContentfulUtils>() {
                @Mock
                String extractPhoto(Invocation invocation, ContentfulLink link, Map<String, ContentfulAsset> assetMap,
                                    Set<String> assetErrorSet, String speakerNameEn) {
                    return invocation.proceed(link, assetMap, assetErrorSet, speakerNameEn);
                }

                @Mock
                String extractAssetUrl(String value) {
                    return ASSET_URL;
                }
            };

            if (expectedException == null) {
                assertEquals(expectedValue, ContentfulUtils.extractPhoto(link, assetMap, assetErrorSet, speakerNameEn));
            } else {
                assertThrows(expectedException, () -> ContentfulUtils.extractPhoto(link, assetMap, assetErrorSet, speakerNameEn));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractTwitter method tests")
    class ExtractTwitterTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null),
                    arguments("", ""),
                    arguments(" ", ""),
                    arguments("arungupta", "arungupta"),
                    arguments(" arungupta", "arungupta"),
                    arguments("arungupta ", "arungupta"),
                    arguments(" arungupta ", "arungupta"),
                    arguments("tagir_valeev", "tagir_valeev"),
                    arguments("kuksenk0", "kuksenk0"),
                    arguments("DaschnerS", "DaschnerS"),
                    arguments("@dougqh", "dougqh"),
                    arguments("42", "42"),
                    arguments("@42", "42")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractTwitter(String value, String expected) {
            assertEquals(expected, ContentfulUtils.extractTwitter(value));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractTwitter method tests (with exception)")
    class ExtractTwitterWithExceptionTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments("%"),
                    arguments("%42"),
                    arguments("%dougqh"),
                    arguments("dougqh%"),
                    arguments("dou%gqh")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractTwitter(String value) {
            assertThrows(IllegalArgumentException.class, () -> ContentfulUtils.extractTwitter(value));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractGitHub method tests")
    class ExtractGitHubTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, null),
                    arguments("", null, ""),
                    arguments(" ", null, ""),
                    arguments("cloudkserg", null, "cloudkserg"),
                    arguments(" cloudkserg", null, "cloudkserg"),
                    arguments("cloudkserg ", null, "cloudkserg"),
                    arguments(" cloudkserg ", null, "cloudkserg"),
                    arguments("pjBooms", null, "pjBooms"),
                    arguments("andre487", null, "andre487"),
                    arguments("Marina-Miranovich", null, "Marina-Miranovich"),
                    arguments("https://github.com/inponomarev", null, "inponomarev"),
                    arguments("http://github.com/inponomarev", null, "inponomarev"),
                    arguments("https://niquola.github.io/blog/", null, "niquola"),
                    arguments("http://niquola.github.io/blog/", null, "niquola"),
                    arguments("%", IllegalArgumentException.class, null),
                    arguments("%42", IllegalArgumentException.class, null),
                    arguments("%dougqh", IllegalArgumentException.class, null),
                    arguments("dougqh%", IllegalArgumentException.class, null),
                    arguments("dou%gqh", IllegalArgumentException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractGitHub(String value, Class<? extends Throwable> expectedException, String expectedValue) {
            if (expectedException == null) {
                assertEquals(expectedValue, ContentfulUtils.extractGitHub(value));
            } else {
                assertThrows(IllegalArgumentException.class, () -> ContentfulUtils.extractGitHub(value));
            }
        }
    }

    @Test
    void getSpeakers(@Mocked ContentfulUtils mock) {
        new MockUp<ContentfulUtils>() {
            @Mock
            List<Speaker> getSpeakers(ContentfulUtils.ConferenceSpaceInfo conferenceSpaceInfo, String conferenceCode) {
                return Collections.emptyList();
            }

            @Mock
            List<Speaker> getSpeakers(Invocation invocation, Conference conference, String conferenceCode) {
                return invocation.proceed(conference, conferenceCode);
            }
        };

        assertDoesNotThrow(() -> ContentfulUtils.getSpeakers(Conference.JPOINT, "code"));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractString method tests")
    class ExtractStringTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null),
                    arguments("", ""),
                    arguments(" value0", "value0"),
                    arguments("value1 ", "value1"),
                    arguments(" value2 ", "value2")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractBoolean(String value, String expected) {
            assertEquals(expected, ContentfulUtils.extractString(value));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractBoolean method tests")
    class ExtractBooleanTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, false),
                    arguments(Boolean.TRUE, true),
                    arguments(Boolean.FALSE, false)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractBoolean(Boolean value, boolean expected) {
            assertEquals(expected, ContentfulUtils.extractBoolean(value));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractProperty method tests")
    class ExtractPropertyTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments("abc", new ExtractSet(
                                    List.of(new ExtractPair("([a-z]+)", 1)),
                                    "Invalid property: %s"),
                            "abc"),
                    arguments("abc", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            "abc"),
                    arguments(" abc", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            "abc"),
                    arguments("abc ", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            "abc"),
                    arguments(" abc ", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            "abc")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractProperty(String value, ExtractSet extractSet, String expected) {
            assertEquals(expected, ContentfulUtils.extractProperty(value, extractSet));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractProperty method tests (with exception)")
    class ExtractPropertyWithExceptionTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments("42", new ExtractSet(
                            List.of(new ExtractPair("([a-z]+)", 1)),
                            "Invalid property: %s")),
                    arguments("42", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s")),
                    arguments(" 42", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s")),
                    arguments("42 ", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s")),
                    arguments(" 42 ", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s"))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractProperty(String value, ExtractSet extractSet) {
            assertThrows(IllegalArgumentException.class, () -> ContentfulUtils.extractProperty(value, extractSet));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractLanguage method tests")
    class ExtractLanguageTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null),
                    arguments(Boolean.TRUE, Language.RUSSIAN.getCode()),
                    arguments(Boolean.FALSE, Language.ENGLISH.getCode())
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractLanguage(Boolean value, String expected) {
            assertEquals(expected, ContentfulUtils.extractLanguage(value));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("combineContentfulLinks method tests")
    class CombineContentfulLinksTest {
        private Stream<Arguments> data() {
            ContentfulSys contentfulSys0 = new ContentfulSys();
            contentfulSys0.setId("a");
            ContentfulLink contentfulLink0 = new ContentfulLink();
            contentfulLink0.setSys(contentfulSys0);

            ContentfulSys contentfulSys1 = new ContentfulSys();
            contentfulSys1.setId("b");
            ContentfulLink contentfulLink1 = new ContentfulLink();
            contentfulLink1.setSys(contentfulSys1);

            ContentfulSys contentfulSys2 = new ContentfulSys();
            contentfulSys2.setId("c");
            ContentfulLink contentfulLink2 = new ContentfulLink();
            contentfulLink2.setSys(contentfulSys2);

            return Stream.of(
                    arguments(null, null, Collections.emptyList()),
                    arguments(Collections.emptyList(), null, Collections.emptyList()),
                    arguments(List.of(contentfulLink0), null, List.of(contentfulLink0)),
                    arguments(List.of(contentfulLink0, contentfulLink1), null, List.of(contentfulLink0, contentfulLink1)),
                    arguments(List.of(contentfulLink0), contentfulLink1, List.of(contentfulLink0, contentfulLink1)),
                    arguments(List.of(contentfulLink0), contentfulLink0, List.of(contentfulLink0)),
                    arguments(List.of(contentfulLink0, contentfulLink1), contentfulLink2, List.of(contentfulLink0, contentfulLink1, contentfulLink2)),
                    arguments(List.of(contentfulLink0, contentfulLink0), contentfulLink0, List.of(contentfulLink0))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void combineContentfulLinks(List<ContentfulLink> presentations, ContentfulLink presentation, List<ContentfulLink> expected) {
            assertEquals(expected, ContentfulUtils.combineContentfulLinks(presentations, presentation));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractVideoLinks method tests")
    class ExtractVideoLinksTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, Collections.emptyList()),
                    arguments("value", List.of("value"))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractVideoLinks(String videoLink, List<String> expected) {
            assertEquals(expected, ContentfulUtils.extractVideoLinks(videoLink));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractAssetUrl method tests")
    class ExtractAssetUrlTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null),
                    arguments("", ""),
                    arguments(" ", ""),
                    arguments("//assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"),
                    arguments(" //assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"),
                    arguments("//assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf ", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"),
                    arguments(" //assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf ", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"),
                    arguments("http://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"),
                    arguments("https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractAssetUrl(String value, String expected) {
            assertEquals(expected, ContentfulUtils.extractAssetUrl(value));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractAssetUrl method tests (with exception)")
    class ExtractAssetUrlWithExceptionTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments("abc"),
                    arguments("42")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractAssetUrl(String value) {
            assertThrows(IllegalArgumentException.class, () -> ContentfulUtils.extractAssetUrl(value));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractLocaleItems method tests")
    class ExtractLocaleItemsTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, true, Collections.emptyList()),
                    arguments(null, "", true, Collections.emptyList()),
                    arguments("", null, true, Collections.emptyList()),
                    arguments("", "", true, Collections.emptyList()),
                    arguments("value0", null, true, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "", true, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "value0", true, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "value1", true, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"),
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))),
                    arguments(null, "value1", true, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))),
                    arguments("", "value1", true, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))),
                    arguments(null, null, false, Collections.emptyList()),
                    arguments(null, "", false, Collections.emptyList()),
                    arguments("", null, false, Collections.emptyList()),
                    arguments("", "", false, Collections.emptyList()),
                    arguments("value0", null, false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "", false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "value0", false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "value1", false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"),
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))),
                    arguments(null, "value1", false, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))),
                    arguments("", "value1", false, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1")))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractLocaleItems(String enText, String ruText, boolean checkEnTextExistence, List<LocaleItem> expected) {
            assertEquals(expected, ContentfulUtils.extractLocaleItems(enText, ruText, checkEnTextExistence));
            assertEquals(expected, ContentfulUtils.extractLocaleItems(enText, ruText));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractEventName method tests")
    class ExtractEventNameTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, null),
                    arguments(null, "", null),
                    arguments(null, "abc", null),
                    arguments("abc", "en", "abc"),
                    arguments("Moscow", "en", " Msc"),
                    arguments("Moscow ", "en", " Msc"),
                    arguments(" Moscow ", "en", " Msc"),
                    arguments("abc Moscow", "en", "abc Msc"),
                    arguments("abc Moscow ", "en", "abc Msc"),
                    arguments("Moscow cde", "en", "Moscow cde"),
                    arguments(" Moscow cde", "en", " Moscow cde"),
                    arguments("abc Moscow cde", "en", "abc Moscow cde"),
                    arguments("Piter", "en", " SPb"),
                    arguments("Piter ", "en", " SPb"),
                    arguments(" Piter ", "en", " SPb"),
                    arguments("abc Piter", "en", "abc SPb"),
                    arguments("abc Piter ", "en", "abc SPb"),
                    arguments("Piter cde", "en", "Piter cde"),
                    arguments(" Piter cde", "en", " Piter cde"),
                    arguments("abc Piter cde", "en", "abc Piter cde"),
                    arguments("Moscow", "ru-RU", " Мск"),
                    arguments("Moscow ", "ru-RU", " Мск"),
                    arguments(" Moscow ", "ru-RU", " Мск"),
                    arguments("abc Moscow", "ru-RU", "abc Мск"),
                    arguments("abc Moscow ", "ru-RU", "abc Мск"),
                    arguments("Moscow cde", "ru-RU", "Moscow cde"),
                    arguments(" Moscow cde", "ru-RU", " Moscow cde"),
                    arguments("abc Moscow cde", "ru-RU", "abc Moscow cde"),
                    arguments("Piter", "ru-RU", " СПб"),
                    arguments("Piter ", "ru-RU", " СПб"),
                    arguments(" Piter ", "ru-RU", " СПб"),
                    arguments("abc Piter", "ru-RU", "abc СПб"),
                    arguments("abc Piter ", "ru-RU", "abc СПб"),
                    arguments("Piter cde", "ru-RU", "Piter cde"),
                    arguments(" Piter cde", "ru-RU", " Piter cde"),
                    arguments("abc Piter cde", "ru-RU", "abc Piter cde")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractEventName(String name, String locale, String expected) {
            assertEquals(expected, ContentfulUtils.extractEventName(name, locale));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractEventName method tests")
    class ExtractEventNameWithExceptionTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments("abc", ""),
                    arguments("abc", "unknown")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractEventName(String name, String locale) {
            assertThrows(IllegalArgumentException.class, () -> ContentfulUtils.extractEventName(name, locale));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("fixNonexistentEventError method tests")
    class FixNonexistentEventErrorTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, null),
                    arguments(null, LocalDate.of(2016, 12, 7), null),
                    arguments(Conference.DOT_NEXT, null, null),
                    arguments(Conference.JPOINT, LocalDate.of(2016, 12, 7), null),
                    arguments(Conference.DOT_NEXT, LocalDate.of(2016, 12, 8), null),
                    arguments(Conference.DOT_NEXT, LocalDate.of(2016, 12, 7), new Event(
                            -1L,
                            null,
                            List.of(
                                    new LocaleItem("en", "DotNext 2016 Helsinki"),
                                    new LocaleItem("ru", "DotNext 2016 Хельсинки")),
                            new Event.EventDates(
                                    LocalDate.of(2016, 12, 7),
                                    LocalDate.of(2016, 12, 7)
                            ),
                            new Event.EventLinks(
                                    List.of(
                                            new LocaleItem("en", "https://dotnext-helsinki.com"),
                                            new LocaleItem("ru", "https://dotnext-helsinki.com")),
                                    "https://www.youtube.com/playlist?list=PLtWrKx3nUGBcaA5j9UT6XMnoGM6a2iCE5"
                            ),
                            new Place(
                                    15,
                                    List.of(
                                            new LocaleItem("en", "Helsinki"),
                                            new LocaleItem("ru", "Хельсинки")),
                                    List.of(
                                            new LocaleItem("en", "Microsoft Talo, Keilalahdentie 2-4, 02150 Espoo")),
                                    "60.1704769, 24.8279349"),
                            Collections.emptyList()))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void fixNonexistentEventError(Conference conference, LocalDate startDate, Event expected) {
            Event event = ContentfulUtils.fixNonexistentEventError(conference, startDate);

            assertEquals(expected, event);

            if ((expected != null) && (event != null)) {
                assertEquals(expected.getName(), event.getName());
                assertEquals(expected.getStartDate(), event.getStartDate());
                assertEquals(expected.getEndDate(), event.getEndDate());
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("needUpdate method tests (EventType)")
    class NeedUpdateEventTypeTest {
        private Stream<Arguments> data() {
            EventType eventType0 = new EventType();
            eventType0.setId(0);
            eventType0.setConference(Conference.JPOINT);
            eventType0.setLogoFileName("logoFileName0");
            eventType0.setName(List.of(new LocaleItem("en", "name0")));
            eventType0.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType0.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType0.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType0.setVkLink("vkLink0");
            eventType0.setTwitterLink("twitterLink0");
            eventType0.setFacebookLink("facebookLink0");
            eventType0.setYoutubeLink("youtubeLink0");
            eventType0.setTelegramLink("telegramLink0");

            EventType eventType1 = new EventType();
            eventType1.setId(1);

            EventType eventType2 = new EventType();
            eventType2.setId(0);
            eventType2.setConference(Conference.JOKER);

            EventType eventType3 = new EventType();
            eventType3.setId(0);
            eventType3.setConference(Conference.JPOINT);
            eventType3.setLogoFileName("logoFileName3");

            EventType eventType4 = new EventType();
            eventType4.setId(0);
            eventType4.setConference(Conference.JPOINT);
            eventType4.setLogoFileName("logoFileName0");
            eventType4.setName(List.of(new LocaleItem("en", "name4")));

            EventType eventType5 = new EventType();
            eventType5.setId(0);
            eventType5.setConference(Conference.JPOINT);
            eventType5.setLogoFileName("logoFileName0");
            eventType5.setName(List.of(new LocaleItem("en", "name0")));
            eventType5.setShortDescription(List.of(new LocaleItem("en", "shortDescription5")));

            EventType eventType6 = new EventType();
            eventType6.setId(0);
            eventType6.setConference(Conference.JPOINT);
            eventType6.setLogoFileName("logoFileName0");
            eventType6.setName(List.of(new LocaleItem("en", "name0")));
            eventType6.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType6.setLongDescription(List.of(new LocaleItem("en", "longDescription6")));

            EventType eventType7 = new EventType();
            eventType7.setId(0);
            eventType7.setConference(Conference.JPOINT);
            eventType7.setLogoFileName("logoFileName0");
            eventType7.setName(List.of(new LocaleItem("en", "name0")));
            eventType7.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType7.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType7.setSiteLink(List.of(new LocaleItem("en", "siteLink7")));

            EventType eventType8 = new EventType();
            eventType8.setId(0);
            eventType8.setConference(Conference.JPOINT);
            eventType8.setLogoFileName("logoFileName0");
            eventType8.setName(List.of(new LocaleItem("en", "name0")));
            eventType8.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType8.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType8.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType8.setVkLink("vkLink8");

            EventType eventType9 = new EventType();
            eventType9.setId(0);
            eventType9.setConference(Conference.JPOINT);
            eventType9.setLogoFileName("logoFileName0");
            eventType9.setName(List.of(new LocaleItem("en", "name0")));
            eventType9.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType9.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType9.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType9.setVkLink("vkLink0");
            eventType9.setTwitterLink("twitterLink9");

            EventType eventType10 = new EventType();
            eventType10.setId(0);
            eventType10.setConference(Conference.JPOINT);
            eventType10.setLogoFileName("logoFileName0");
            eventType10.setName(List.of(new LocaleItem("en", "name0")));
            eventType10.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType10.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType10.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType10.setVkLink("vkLink0");
            eventType10.setTwitterLink("twitterLink0");
            eventType10.setFacebookLink("facebookLink10");

            EventType eventType11 = new EventType();
            eventType11.setId(0);
            eventType11.setConference(Conference.JPOINT);
            eventType11.setLogoFileName("logoFileName0");
            eventType11.setName(List.of(new LocaleItem("en", "name0")));
            eventType11.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType11.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType11.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType11.setVkLink("vkLink0");
            eventType11.setTwitterLink("twitterLink0");
            eventType11.setFacebookLink("facebookLink0");
            eventType11.setYoutubeLink("youtubeLink11");

            EventType eventType12 = new EventType();
            eventType12.setId(0);
            eventType12.setConference(Conference.JPOINT);
            eventType12.setLogoFileName("logoFileName0");
            eventType12.setName(List.of(new LocaleItem("en", "name0")));
            eventType12.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType12.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType12.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType12.setVkLink("vkLink0");
            eventType12.setTwitterLink("twitterLink0");
            eventType12.setFacebookLink("facebookLink0");
            eventType12.setYoutubeLink("youtubeLink0");
            eventType12.setTelegramLink("telegramLink12");

            return Stream.of(
                    arguments(eventType0, eventType0, false),
                    arguments(eventType0, eventType1, true),
                    arguments(eventType0, eventType2, true),
                    arguments(eventType0, eventType3, true),
                    arguments(eventType0, eventType4, true),
                    arguments(eventType0, eventType5, true),
                    arguments(eventType0, eventType6, true),
                    arguments(eventType0, eventType7, true),
                    arguments(eventType0, eventType8, true),
                    arguments(eventType0, eventType9, true),
                    arguments(eventType0, eventType10, true),
                    arguments(eventType0, eventType11, true),
                    arguments(eventType0, eventType12, true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void needUpdate(EventType a, EventType b, boolean expected) {
            assertEquals(expected, ContentfulUtils.needUpdate(a, b));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("needUpdate method tests (Place)")
    class NeedUpdatePlaceTest {
        private Stream<Arguments> data() {
            Place place0 = new Place();
            place0.setId(0);
            place0.setCity(List.of(new LocaleItem("en", "city0")));
            place0.setVenueAddress(List.of(new LocaleItem("en", "venueAddress0")));
            place0.setMapCoordinates("mapCoordinates0");

            Place place1 = new Place();
            place1.setId(1);

            Place place2 = new Place();
            place2.setId(0);
            place2.setCity(List.of(new LocaleItem("en", "city2")));

            Place place3 = new Place();
            place3.setId(0);
            place3.setCity(List.of(new LocaleItem("en", "city0")));
            place3.setVenueAddress(List.of(new LocaleItem("en", "venueAddress3")));

            Place place4 = new Place();
            place4.setId(0);
            place4.setCity(List.of(new LocaleItem("en", "city0")));
            place4.setVenueAddress(List.of(new LocaleItem("en", "venueAddress0")));
            place4.setMapCoordinates("mapCoordinates4");

            return Stream.of(
                    arguments(place0, place0, false),
                    arguments(place0, place1, true),
                    arguments(place0, place2, true),
                    arguments(place0, place3, true),
                    arguments(place0, place4, true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void needUpdate(Place a, Place b, boolean expected) {
            assertEquals(expected, ContentfulUtils.needUpdate(a, b));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("needUpdate method tests (Speaker)")
    class NeedUpdateSpeakerTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setPhotoFileName("photoFileName0");
            speaker0.setName(List.of(new LocaleItem("en", "name0")));
            speaker0.setCompany(List.of(new LocaleItem("en", "company0")));
            speaker0.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker0.setTwitter("twitter0");
            speaker0.setGitHub("gitHub0");
            speaker0.setJavaChampion(true);
            speaker0.setMvp(true);
            speaker0.setMvpReconnect(true);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Speaker speaker2 = new Speaker();
            speaker2.setId(0);
            speaker2.setPhotoFileName("photoFileName2");

            Speaker speaker3 = new Speaker();
            speaker3.setId(0);
            speaker3.setPhotoFileName("photoFileName0");
            speaker3.setName(List.of(new LocaleItem("en", "name3")));

            Speaker speaker4 = new Speaker();
            speaker4.setId(0);
            speaker4.setPhotoFileName("photoFileName0");
            speaker4.setName(List.of(new LocaleItem("en", "name0")));
            speaker4.setCompany(List.of(new LocaleItem("en", "company4")));

            Speaker speaker5 = new Speaker();
            speaker5.setId(0);
            speaker5.setPhotoFileName("photoFileName0");
            speaker5.setName(List.of(new LocaleItem("en", "name0")));
            speaker5.setCompany(List.of(new LocaleItem("en", "company0")));
            speaker5.setBio(List.of(new LocaleItem("en", "bio5")));

            Speaker speaker6 = new Speaker();
            speaker6.setId(0);
            speaker6.setPhotoFileName("photoFileName0");
            speaker6.setName(List.of(new LocaleItem("en", "name0")));
            speaker6.setCompany(List.of(new LocaleItem("en", "company0")));
            speaker6.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker6.setTwitter("twitter6");

            Speaker speaker7 = new Speaker();
            speaker7.setId(0);
            speaker7.setPhotoFileName("photoFileName0");
            speaker7.setName(List.of(new LocaleItem("en", "name0")));
            speaker7.setCompany(List.of(new LocaleItem("en", "company0")));
            speaker7.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker7.setTwitter("twitter0");
            speaker7.setGitHub("gitHub7");

            Speaker speaker8 = new Speaker();
            speaker8.setId(0);
            speaker8.setPhotoFileName("photoFileName0");
            speaker8.setName(List.of(new LocaleItem("en", "name0")));
            speaker8.setCompany(List.of(new LocaleItem("en", "company0")));
            speaker8.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker8.setTwitter("twitter0");
            speaker8.setGitHub("gitHub0");
            speaker8.setJavaChampion(false);

            Speaker speaker9 = new Speaker();
            speaker9.setId(0);
            speaker9.setPhotoFileName("photoFileName0");
            speaker9.setName(List.of(new LocaleItem("en", "name0")));
            speaker9.setCompany(List.of(new LocaleItem("en", "company0")));
            speaker9.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker9.setTwitter("twitter0");
            speaker9.setGitHub("gitHub0");
            speaker9.setJavaChampion(true);
            speaker9.setMvp(false);

            Speaker speaker10 = new Speaker();
            speaker10.setId(0);
            speaker10.setPhotoFileName("photoFileName0");
            speaker10.setName(List.of(new LocaleItem("en", "name0")));
            speaker10.setCompany(List.of(new LocaleItem("en", "company0")));
            speaker10.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker10.setTwitter("twitter0");
            speaker10.setGitHub("gitHub0");
            speaker10.setJavaChampion(true);
            speaker10.setMvp(true);
            speaker10.setMvpReconnect(false);

            return Stream.of(
                    arguments(speaker0, speaker0, false),
                    arguments(speaker0, speaker1, true),
                    arguments(speaker0, speaker2, true),
                    arguments(speaker0, speaker3, true),
                    arguments(speaker0, speaker4, true),
                    arguments(speaker0, speaker5, true),
                    arguments(speaker0, speaker6, true),
                    arguments(speaker0, speaker7, true),
                    arguments(speaker0, speaker8, true),
                    arguments(speaker0, speaker9, true),
                    arguments(speaker0, speaker10, true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void needUpdate(Speaker a, Speaker b, boolean expected) {
            assertEquals(expected, ContentfulUtils.needUpdate(a, b));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("needUpdate method tests (Talk)")
    class NeedUpdateTalkTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setId(0);
            talk0.setName(List.of(new LocaleItem("en", "name0")));
            talk0.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            talk0.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            talk0.setTalkDay(1L);
            talk0.setTrackTime(LocalTime.of(10, 0));
            talk0.setTrack(1L);
            talk0.setLanguage("en");
            talk0.setPresentationLinks(List.of("presentationLink0"));
            talk0.setVideoLinks(List.of("videoLink0"));
            talk0.setSpeakerIds(List.of(0L));

            Talk talk1 = new Talk();
            talk1.setId(1);

            Talk talk2 = new Talk();
            talk2.setId(0);
            talk2.setName(List.of(new LocaleItem("en", "name2")));

            Talk talk3 = new Talk();
            talk3.setId(0);
            talk3.setName(List.of(new LocaleItem("en", "name0")));
            talk3.setShortDescription(List.of(new LocaleItem("en", "shortDescription3")));

            Talk talk4 = new Talk();
            talk4.setId(0);
            talk4.setName(List.of(new LocaleItem("en", "name0")));
            talk4.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            talk4.setLongDescription(List.of(new LocaleItem("en", "longDescription4")));

            Talk talk5 = new Talk();
            talk5.setId(0);
            talk5.setName(List.of(new LocaleItem("en", "name0")));
            talk5.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            talk5.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            talk5.setTalkDay(2L);

            Talk talk6 = new Talk();
            talk6.setId(0);
            talk6.setName(List.of(new LocaleItem("en", "name0")));
            talk6.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            talk6.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            talk6.setTalkDay(1L);
            talk6.setTrackTime(LocalTime.of(10, 30));

            Talk talk7 = new Talk();
            talk7.setId(0);
            talk7.setName(List.of(new LocaleItem("en", "name0")));
            talk7.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            talk7.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            talk7.setTalkDay(1L);
            talk7.setTrackTime(LocalTime.of(10, 0));
            talk7.setTrack(2L);

            Talk talk8 = new Talk();
            talk8.setId(0);
            talk8.setName(List.of(new LocaleItem("en", "name0")));
            talk8.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            talk8.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            talk8.setTalkDay(1L);
            talk8.setTrackTime(LocalTime.of(10, 0));
            talk8.setTrack(1L);
            talk8.setLanguage("ru");

            Talk talk9 = new Talk();
            talk9.setId(0);
            talk9.setName(List.of(new LocaleItem("en", "name0")));
            talk9.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            talk9.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            talk9.setTalkDay(1L);
            talk9.setTrackTime(LocalTime.of(10, 0));
            talk9.setTrack(1L);
            talk9.setLanguage("en");
            talk9.setPresentationLinks(List.of("presentationLink9"));

            Talk talk10 = new Talk();
            talk10.setId(0);
            talk10.setName(List.of(new LocaleItem("en", "name0")));
            talk10.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            talk10.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            talk10.setTalkDay(1L);
            talk10.setTrackTime(LocalTime.of(10, 0));
            talk10.setTrack(1L);
            talk10.setLanguage("en");
            talk10.setPresentationLinks(List.of("presentationLink0"));
            talk10.setVideoLinks(List.of("videoLink10"));

            Talk talk11 = new Talk();
            talk11.setId(0);
            talk11.setName(List.of(new LocaleItem("en", "name0")));
            talk11.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            talk11.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            talk11.setTalkDay(1L);
            talk11.setTrackTime(LocalTime.of(10, 0));
            talk11.setTrack(1L);
            talk11.setLanguage("en");
            talk11.setPresentationLinks(List.of("presentationLink0"));
            talk11.setVideoLinks(List.of("videoLink0"));
            talk11.setSpeakerIds(List.of(1L));

            return Stream.of(
                    arguments(talk0, talk0, false),
                    arguments(talk0, talk1, true),
                    arguments(talk0, talk2, true),
                    arguments(talk0, talk3, true),
                    arguments(talk0, talk4, true),
                    arguments(talk0, talk5, true),
                    arguments(talk0, talk6, true),
                    arguments(talk0, talk7, true),
                    arguments(talk0, talk8, true),
                    arguments(talk0, talk9, true),
                    arguments(talk0, talk10, true),
                    arguments(talk0, talk11, true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void needUpdate(Talk a, Talk b, boolean expected) {
            assertEquals(expected, ContentfulUtils.needUpdate(a, b));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("needUpdate method tests (Event)")
    class NeedUpdateEventTest {
        private Stream<Arguments> data() {
            Event event0 = new Event();
            event0.setEventTypeId(0);
            event0.setName(List.of(new LocaleItem("en", "name0")));
            event0.setStartDate(LocalDate.of(2020, 8, 5));
            event0.setEndDate(LocalDate.of(2020, 8, 6));
            event0.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            event0.setYoutubeLink("youtubeLink0");
            event0.setPlaceId(0);
            event0.setTalkIds(List.of(0L));

            Event event1 = new Event();
            event1.setEventTypeId(1);

            Event event2 = new Event();
            event2.setEventTypeId(0);
            event2.setName(List.of(new LocaleItem("en", "name2")));

            Event event3 = new Event();
            event3.setEventTypeId(0);
            event3.setName(List.of(new LocaleItem("en", "name0")));
            event3.setStartDate(LocalDate.of(2020, 8, 6));

            Event event4 = new Event();
            event4.setEventTypeId(0);
            event4.setName(List.of(new LocaleItem("en", "name0")));
            event4.setStartDate(LocalDate.of(2020, 8, 5));
            event4.setEndDate(LocalDate.of(2020, 8, 7));

            Event event5 = new Event();
            event5.setEventTypeId(0);
            event5.setName(List.of(new LocaleItem("en", "name0")));
            event5.setStartDate(LocalDate.of(2020, 8, 5));
            event5.setEndDate(LocalDate.of(2020, 8, 6));
            event5.setSiteLink(List.of(new LocaleItem("en", "siteLink5")));

            Event event6 = new Event();
            event6.setEventTypeId(0);
            event6.setName(List.of(new LocaleItem("en", "name0")));
            event6.setStartDate(LocalDate.of(2020, 8, 5));
            event6.setEndDate(LocalDate.of(2020, 8, 6));
            event6.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            event6.setYoutubeLink("youtubeLink6");

            Event event7 = new Event();
            event7.setEventTypeId(0);
            event7.setName(List.of(new LocaleItem("en", "name0")));
            event7.setStartDate(LocalDate.of(2020, 8, 5));
            event7.setEndDate(LocalDate.of(2020, 8, 6));
            event7.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            event7.setYoutubeLink("youtubeLink0");
            event7.setPlaceId(7);

            Event event8 = new Event();
            event8.setEventTypeId(0);
            event8.setName(List.of(new LocaleItem("en", "name0")));
            event8.setStartDate(LocalDate.of(2020, 8, 5));
            event8.setEndDate(LocalDate.of(2020, 8, 6));
            event8.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            event8.setYoutubeLink("youtubeLink0");
            event8.setPlaceId(0);
            event8.setTalkIds(List.of(8L));

            return Stream.of(
                    arguments(event0, event0, false),
                    arguments(event0, event1, true),
                    arguments(event0, event2, true),
                    arguments(event0, event3, true),
                    arguments(event0, event4, true),
                    arguments(event0, event5, true),
                    arguments(event0, event6, true),
                    arguments(event0, event7, true),
                    arguments(event0, event8, true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void needUpdate(Event a, Event b, boolean expected) {
            assertEquals(expected, ContentfulUtils.needUpdate(a, b));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("equals method tests")
    class EqualsTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, true),
                    arguments(null, List.of(""), false),
                    arguments(List.of(""), null, false),
                    arguments(List.of(""), List.of(""), true),
                    arguments(List.of(""), List.of("a"), false),
                    arguments(List.of("a"), List.of(""), false),
                    arguments(List.of("a"), List.of("a"), true),
                    arguments(List.of("a"), List.of("b"), false),
                    arguments(List.of("a", "b"), List.of("a", "b"), true),
                    arguments(List.of("a"), List.of("a", "b"), false),
                    arguments(List.of("a", "b"), List.of("a"), false)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void needUpdate(List<String> a, List<String> b, boolean expected) {
            assertEquals(expected, ContentfulUtils.equals(a, b));
        }
    }
}
