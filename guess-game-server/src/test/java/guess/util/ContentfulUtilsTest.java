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
import guess.domain.source.contentful.error.ContentfulError;
import guess.domain.source.contentful.error.ContentfulErrorDetails;
import guess.domain.source.contentful.event.ContentfulEvent;
import guess.domain.source.contentful.event.ContentfulEventFields;
import guess.domain.source.contentful.event.ContentfulEventIncludes;
import guess.domain.source.contentful.event.ContentfulEventResponse;
import guess.domain.source.contentful.eventtype.ContentfulEventType;
import guess.domain.source.contentful.eventtype.ContentfulEventTypeFields;
import guess.domain.source.contentful.eventtype.ContentfulEventTypeResponse;
import guess.domain.source.contentful.locale.ContentfulLocale;
import guess.domain.source.contentful.locale.ContentfulLocaleResponse;
import guess.domain.source.contentful.speaker.ContentfulSpeaker;
import guess.domain.source.contentful.speaker.ContentfulSpeakerFields;
import guess.domain.source.contentful.speaker.ContentfulSpeakerResponse;
import guess.domain.source.contentful.talk.ContentfulTalk;
import guess.domain.source.contentful.talk.ContentfulTalkIncludes;
import guess.domain.source.contentful.talk.fields.ContentfulTalkFields;
import guess.domain.source.contentful.talk.fields.ContentfulTalkFieldsCommon;
import guess.domain.source.contentful.talk.response.ContentfulTalkResponse;
import guess.domain.source.contentful.talk.response.ContentfulTalkResponseCommon;
import guess.domain.source.extract.ExtractPair;
import guess.domain.source.extract.ExtractSet;
import guess.domain.source.image.UrlDates;
import mockit.*;
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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("ContentfulUtils class tests")
class ContentfulUtilsTest {
    private static ContentfulTalk<ContentfulTalkFieldsCommon> createContentfulTalk(String conference, String conferences) {
        ContentfulTalkFieldsCommon contentfulTalkFields = new ContentfulTalkFieldsCommon();
        if (conference != null) {
            contentfulTalkFields.setConference(List.of(conference));
        }
        if (conferences != null) {
            contentfulTalkFields.setConferences(List.of(conferences));
        }

        ContentfulTalk<ContentfulTalkFieldsCommon> contentfulTalk = new ContentfulTalk<>();
        contentfulTalk.setFields(contentfulTalkFields);

        return contentfulTalk;
    }

    @Test
    void getTags(@Mocked RestTemplate restTemplateMock) throws URISyntaxException {
        final String CODE1 = "code1";
        final String CODE2 = "code2";
        final String CODE3 = "code3";
        final String CODE4 = "code4";

        new Expectations() {{
            ContentfulTalkResponse<ContentfulTalkFieldsCommon> response = new ContentfulTalkResponseCommon();
            response.setItems(List.of(
                    createContentfulTalk(CODE2, null),
                    createContentfulTalk(null, CODE1),
                    createContentfulTalk(CODE4, null),
                    createContentfulTalk(null, CODE3),
                    createContentfulTalk(null, CODE2)
            ));

            restTemplateMock.getForObject(withAny(new URI("https://valid.com")), ContentfulTalkResponseCommon.class);
            result = response;
        }};

        Map<ContentfulUtils.ConferenceSpaceInfo, List<String>> expected = Map.of(
                ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO,
                List.of(CODE1, CODE2, CODE3, CODE4),
                ContentfulUtils.ConferenceSpaceInfo.HOLY_JS_SPACE_INFO, Collections.emptyList(),
                ContentfulUtils.ConferenceSpaceInfo.DOT_NEXT_SPACE_INFO, Collections.emptyList(),
                ContentfulUtils.ConferenceSpaceInfo.HEISENBUG_SPACE_INFO, Collections.emptyList(),
                ContentfulUtils.ConferenceSpaceInfo.MOBIUS_SPACE_INFO, Collections.emptyList());

        assertEquals(expected, ContentfulUtils.getTags("2021"));
        assertEquals(expected, ContentfulUtils.getTags(""));
        assertEquals(expected, ContentfulUtils.getTags(null));
    }

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
    void getEventTypes() {
        try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
            mockedStatic.when(ContentfulUtils::getEventTypes)
                    .thenCallRealMethod();
            mockedStatic.when(() -> ContentfulUtils.createEventType(Mockito.any(ContentfulEventType.class), Mockito.any(AtomicLong.class)))
                    .thenReturn(new EventType());

            ContentfulEventTypeResponse response = new ContentfulEventTypeResponse();
            response.setItems(List.of(new ContentfulEventType(), new ContentfulEventType()));

            RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
            Mockito.when(restTemplateMock.getForObject(Mockito.any(URI.class), Mockito.any()))
                    .thenReturn(response);

            mockedStatic.when(ContentfulUtils::getRestTemplate)
                    .thenReturn(restTemplateMock);

            assertEquals(2, ContentfulUtils.getEventTypes().size());
        }
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
    @SuppressWarnings("unchecked")
    void getEvents() {
        try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
            mockedStatic.when(() -> ContentfulUtils.getEvents(Mockito.nullable(String.class), Mockito.nullable(LocalDate.class)))
                    .thenCallRealMethod();
            mockedStatic.when(() -> ContentfulUtils.createEvent(Mockito.any(ContentfulEvent.class), Mockito.anyMap(), Mockito.anySet()))
                    .thenReturn(new Event());
            mockedStatic.when(() -> ContentfulUtils.createUtcZonedDateTime(Mockito.any(LocalDate.class)))
                    .thenReturn(ZonedDateTime.now());
            mockedStatic.when(() -> ContentfulUtils.getCityMap(Mockito.any(ContentfulEventResponse.class)))
                    .thenReturn(Collections.emptyMap());
            mockedStatic.when(() -> ContentfulUtils.getErrorSet(Mockito.any(ContentfulResponse.class), Mockito.anyString()))
                    .thenReturn(Collections.emptySet());

            ContentfulEventResponse response = new ContentfulEventResponse();
            response.setItems(List.of(new ContentfulEvent(), new ContentfulEvent()));

            RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
            Mockito.when(restTemplateMock.getForObject(Mockito.any(URI.class), Mockito.any()))
                    .thenReturn(response);

            mockedStatic.when(ContentfulUtils::getRestTemplate)
                    .thenReturn(restTemplateMock);

            assertEquals(2, ContentfulUtils.getEvents("JPoint", LocalDate.of(2020, 6, 29)).size());
            assertEquals(2, ContentfulUtils.getEvents(null, LocalDate.of(2020, 6, 29)).size());
            assertEquals(2, ContentfulUtils.getEvents("", LocalDate.of(2020, 6, 29)).size());
            assertEquals(2, ContentfulUtils.getEvents("JPoint", null).size());
        }
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
                    new Nameable(
                            -1L,
                            List.of(
                                    new LocaleItem(Language.ENGLISH.getCode(), "Event Name0"),
                                    new LocaleItem(Language.RUSSIAN.getCode(), "Наименование события0"))
                    ),
                    null,
                    new Event.EventDates(
                            LocalDate.of(2016, 12, 7),
                            LocalDate.of(2016, 12, 7)
                    ),
                    new Event.EventLinks(Collections.emptyList(), null),
                    new Place(),
                    null,
                    Collections.emptyList());

            Event event1 = new Event(
                    new Nameable(
                            -1L,
                            List.of(
                                    new LocaleItem(Language.ENGLISH.getCode(), "Event Name1"),
                                    new LocaleItem(Language.RUSSIAN.getCode(), "Наименование события1"))
                    ),
                    null,
                    new Event.EventDates(
                            LocalDate.of(2017, 12, 7),
                            LocalDate.of(2017, 12, 7)
                    ),
                    new Event.EventLinks(Collections.emptyList(), null),
                    new Place(),
                    null,
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
            try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
                mockedStatic.when(() -> ContentfulUtils.getEvent(Mockito.any(Conference.class), Mockito.any(LocalDate.class)))
                        .thenCallRealMethod();
                mockedStatic.when(() -> ContentfulUtils.getEvents(Mockito.anyString(), Mockito.any(LocalDate.class)))
                        .thenReturn(events);
                mockedStatic.when(() -> ContentfulUtils.fixNonexistentEventError(Mockito.any(Conference.class), Mockito.any(LocalDate.class)))
                        .thenAnswer(
                                (Answer<Event>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    Conference localConference = (Conference) args[0];
                                    LocalDate localStartDate = (LocalDate) args[1];

                                    if (Conference.DOT_NEXT.equals(localConference) && LocalDate.of(2016, 12, 7).equals(localStartDate)) {
                                        return new Event(
                                                new Nameable(
                                                        -1L,
                                                        List.of(
                                                                new LocaleItem("en", "Event Name0"),
                                                                new LocaleItem("ru", "Наименование события0"))
                                                ),
                                                null,
                                                new Event.EventDates(
                                                        LocalDate.of(2016, 12, 7),
                                                        LocalDate.of(2016, 12, 7)
                                                ),
                                                new Event.EventLinks(Collections.emptyList(), null),
                                                new Place(),
                                                null,
                                                Collections.emptyList());
                                    } else {
                                        return null;
                                    }
                                }
                        );

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
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSpeakersByConferenceSpaceInfo() {
        try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
            mockedStatic.when(() -> ContentfulUtils.getSpeakers(Mockito.any(ContentfulUtils.ConferenceSpaceInfo.class), Mockito.nullable(String.class)))
                    .thenCallRealMethod();
            mockedStatic.when(() -> ContentfulUtils.createSpeaker(
                    Mockito.any(ContentfulSpeaker.class), Mockito.anyMap(), Mockito.anySet(), Mockito.any(AtomicLong.class), Mockito.any(AtomicLong.class), Mockito.anyBoolean()))
                    .thenReturn(new Speaker());
            mockedStatic.when(() -> ContentfulUtils.getAssetMap(Mockito.any(ContentfulResponse.class)))
                    .thenReturn(Collections.emptyMap());
            mockedStatic.when(() -> ContentfulUtils.getErrorSet(Mockito.any(ContentfulResponse.class), Mockito.anyString()))
                    .thenReturn(Collections.emptySet());

            ContentfulSpeakerResponse response = new ContentfulSpeakerResponse();
            response.setItems(List.of(new ContentfulSpeaker(), new ContentfulSpeaker()));

            RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
            Mockito.when(restTemplateMock.getForObject(Mockito.any(URI.class), Mockito.any()))
                    .thenReturn(response);

            mockedStatic.when(ContentfulUtils::getRestTemplate)
                    .thenReturn(restTemplateMock);

            assertEquals(2, ContentfulUtils.getSpeakers(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "code").size());
            assertEquals(2, ContentfulUtils.getSpeakers(ContentfulUtils.ConferenceSpaceInfo.HOLY_JS_SPACE_INFO, "code").size());
            assertEquals(2, ContentfulUtils.getSpeakers(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, null).size());
            assertEquals(2, ContentfulUtils.getSpeakers(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "").size());
        }
    }

    @Test
    void createSpeaker() {
        try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
            mockedStatic.when(() -> ContentfulUtils.createSpeaker(
                    Mockito.any(ContentfulSpeaker.class), Mockito.anyMap(), Mockito.anySet(), Mockito.any(AtomicLong.class),
                    Mockito.any(AtomicLong.class), Mockito.anyBoolean()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> ContentfulUtils.extractPhoto(Mockito.nullable(ContentfulLink.class), Mockito.anyMap(), Mockito.anySet(), Mockito.nullable(String.class)))
                    .thenReturn(new UrlDates(null, null, null));
            mockedStatic.when(() -> ContentfulUtils.extractTwitter(Mockito.nullable(String.class)))
                    .thenReturn(null);
            mockedStatic.when(() -> ContentfulUtils.extractGitHub(Mockito.nullable(String.class)))
                    .thenReturn(null);
            mockedStatic.when(() -> ContentfulUtils.extractBoolean(Mockito.nullable(Boolean.class)))
                    .thenReturn(true);
            mockedStatic.when(() -> ContentfulUtils.extractLocaleItems(Mockito.nullable(String.class), Mockito.nullable(String.class), Mockito.anyBoolean()))
                    .thenReturn(Collections.emptyList());

            ContentfulSpeaker contentfulSpeaker = new ContentfulSpeaker();
            contentfulSpeaker.setFields(new ContentfulSpeakerFields());

            Map<String, ContentfulAsset> assetMap = Collections.emptyMap();
            Set<String> assetErrorSet = Collections.emptySet();
            AtomicLong speakerId = new AtomicLong(42);
            AtomicLong companyId = new AtomicLong(42);

            Speaker speaker = new Speaker();
            speaker.setId(42);

            assertEquals(42, ContentfulUtils.createSpeaker(contentfulSpeaker, assetMap, assetErrorSet, speakerId, companyId, true).getId());
        }
    }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("createCompanies method tests")
    class CreateCompaniesTest {
        ContentfulSpeaker createContentfulSpeaker(String companyEn, String company) {
            ContentfulSpeakerFields contentfulSpeakerFields = new ContentfulSpeakerFields();
            contentfulSpeakerFields.setCompanyEn(companyEn);
            contentfulSpeakerFields.setCompany(company);

            ContentfulSpeaker contentfulSpeaker = new ContentfulSpeaker();
            contentfulSpeaker.setFields(contentfulSpeakerFields);

            return contentfulSpeaker;
        }

        Company company0 = new Company(0, Collections.emptyList());

        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(createContentfulSpeaker(null, null), new AtomicLong(), false, Collections.emptyList()),
                    arguments(createContentfulSpeaker(null, ""), new AtomicLong(), false, Collections.emptyList()),
                    arguments(createContentfulSpeaker("", null), new AtomicLong(), false, Collections.emptyList()),
                    arguments(createContentfulSpeaker("", ""), new AtomicLong(), false, Collections.emptyList()),
                    arguments(createContentfulSpeaker("Company", null), new AtomicLong(), false, List.of(company0)),
                    arguments(createContentfulSpeaker("Company", ""), new AtomicLong(), false, List.of(company0)),
                    arguments(createContentfulSpeaker(null, "Компания"), new AtomicLong(), false, List.of(company0)),
                    arguments(createContentfulSpeaker("", "Компания"), new AtomicLong(), false, List.of(company0)),
                    arguments(createContentfulSpeaker("Company", "Компания"), new AtomicLong(), false, List.of(company0))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void createCompanies(ContentfulSpeaker contentfulSpeaker, AtomicLong companyId, boolean checkEnTextExistence,
                             List<Company> expected) {
            try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
                mockedStatic.when(() -> ContentfulUtils.createCompanies(Mockito.any(ContentfulSpeaker.class), Mockito.any(AtomicLong.class), Mockito.anyBoolean()))
                        .thenCallRealMethod();
                mockedStatic.when(() -> ContentfulUtils.extractLocaleItems(Mockito.nullable(String.class), Mockito.nullable(String.class), Mockito.anyBoolean()))
                        .thenReturn(Collections.emptyList());

                assertEquals(expected, ContentfulUtils.createCompanies(contentfulSpeaker, companyId, checkEnTextExistence));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractPhoto method tests")
    class ExtractPhotoTest {
        private static final String ASSET_URL = "https://valid.com";
        private final ZonedDateTime NOW = ZonedDateTime.now();

        private Stream<Arguments> data() {
            ContentfulSys contentfulSys0 = new ContentfulSys();
            contentfulSys0.setId("id0");

            ContentfulSys contentfulSys1 = new ContentfulSys();
            contentfulSys1.setId("id1");

            ContentfulSys contentfulSys2 = new ContentfulSys();
            contentfulSys2.setId("id2");

            ContentfulSys contentfulSys3 = new ContentfulSys();
            contentfulSys3.setId("id3");
            contentfulSys3.setCreatedAt(NOW);
            contentfulSys3.setUpdatedAt(NOW);

            ContentfulAssetFields contentfulAssetFields2 = new ContentfulAssetFields();
            contentfulAssetFields2.setFile(new ContentfulAssetFieldsFile());

            ContentfulAsset contentfulAsset2 = new ContentfulAsset();
            contentfulAsset2.setFields(contentfulAssetFields2);
            contentfulAsset2.setSys(contentfulSys3);

            Map<String, ContentfulAsset> assetMap2 = Map.of("id2", contentfulAsset2);

            ContentfulLink link0 = new ContentfulLink();
            link0.setSys(contentfulSys0);

            ContentfulLink link1 = new ContentfulLink();
            link1.setSys(contentfulSys1);

            ContentfulLink link2 = new ContentfulLink();
            link2.setSys(contentfulSys2);

            return Stream.of(
                    arguments(link0, Collections.emptyMap(), Set.of("id0"), "Name0", null, new UrlDates(null, null, null)),
                    arguments(link1, Collections.emptyMap(), Collections.emptySet(), "Name1", NullPointerException.class, null),
                    arguments(link2, assetMap2, Collections.emptySet(), "Name2", null, new UrlDates(ASSET_URL, NOW, NOW))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractPhoto(ContentfulLink link, Map<String, ContentfulAsset> assetMap, Set<String> assetErrorSet,
                          String speakerNameEn, Class<? extends Throwable> expectedException, UrlDates expectedValue) {
            try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
                mockedStatic.when(() -> ContentfulUtils.extractPhoto(Mockito.any(ContentfulLink.class), Mockito.anyMap(), Mockito.anySet(), Mockito.anyString()))
                        .thenCallRealMethod();
                mockedStatic.when(() -> ContentfulUtils.extractAssetUrl(Mockito.nullable(String.class)))
                        .thenReturn(ASSET_URL);

                if (expectedException == null) {
                    assertEquals(expectedValue, ContentfulUtils.extractPhoto(link, assetMap, assetErrorSet, speakerNameEn));
                } else {
                    assertThrows(expectedException, () -> ContentfulUtils.extractPhoto(link, assetMap, assetErrorSet, speakerNameEn));
                }
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractTwitter method tests")
    class ExtractTwitterTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, null),
                    arguments("", null, ""),
                    arguments(" ", null, ""),
                    arguments("arungupta", null, "arungupta"),
                    arguments(" arungupta", null, "arungupta"),
                    arguments("arungupta ", null, "arungupta"),
                    arguments(" arungupta ", null, "arungupta"),
                    arguments("tagir_valeev", null, "tagir_valeev"),
                    arguments("kuksenk0", null, "kuksenk0"),
                    arguments("DaschnerS", null, "DaschnerS"),
                    arguments("@dougqh", null, "dougqh"),
                    arguments("42", null, "42"),
                    arguments("@42", null, "42"),
                    arguments("https://twitter.com/_bravit", null, "_bravit"),
                    arguments("%", IllegalArgumentException.class, null),
                    arguments("%42", IllegalArgumentException.class, null),
                    arguments("%dougqh", IllegalArgumentException.class, null),
                    arguments("dougqh%", IllegalArgumentException.class, null),
                    arguments("dou%gqh", IllegalArgumentException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractTwitter(String value, Class<? extends Throwable> expectedException, String expectedValue) {
            if (expectedException == null) {
                assertEquals(expectedValue, ContentfulUtils.extractTwitter(value));
            } else {
                assertThrows(expectedException, () -> ContentfulUtils.extractTwitter(value));
            }
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
                    arguments("https://github.com/Drill4J/realworld-java-and-js-coverage", null, "Drill4J"),
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
    void getSpeakersByConference() {
        try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
            mockedStatic.when(() -> ContentfulUtils.getSpeakers(Mockito.any(Conference.class), Mockito.anyString()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> ContentfulUtils.getSpeakers(Mockito.any(ContentfulUtils.ConferenceSpaceInfo.class), Mockito.anyString()))
                    .thenReturn(Collections.emptyList());

            assertDoesNotThrow(() -> ContentfulUtils.getSpeakers(Conference.JPOINT, "code"));
        }
    }

    private static ContentfulTalk<ContentfulTalkFieldsCommon> createContentfulTalk(Long talkDay, LocalTime trackTime,
                                                                                   Long track, Boolean sdTrack, Boolean demoStage) {
        ContentfulTalkFieldsCommon contentfulTalkFieldsCommon = new ContentfulTalkFieldsCommon();
        contentfulTalkFieldsCommon.setTalkDay(talkDay);
        contentfulTalkFieldsCommon.setTrackTime(trackTime);
        contentfulTalkFieldsCommon.setTrack(track);
        contentfulTalkFieldsCommon.setSdTrack(sdTrack);
        contentfulTalkFieldsCommon.setDemoStage(demoStage);

        ContentfulTalk<ContentfulTalkFieldsCommon> contentfulTalk = new ContentfulTalk<>();
        contentfulTalk.setFields(contentfulTalkFieldsCommon);

        return contentfulTalk;
    }

    @Test
    @SuppressWarnings("unchecked")
    void getTalks() {
        try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
            mockedStatic.when(() -> ContentfulUtils.getTalks(Mockito.any(ContentfulUtils.ConferenceSpaceInfo.class), Mockito.nullable(String.class), Mockito.anyBoolean()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> ContentfulUtils.createTalk(
                    Mockito.any(ContentfulTalk.class), Mockito.anyMap(), Mockito.anySet(), Mockito.anySet(), Mockito.anyMap(), Mockito.any(AtomicLong.class)))
                    .thenReturn(new Talk());
            mockedStatic.when(() -> ContentfulUtils.getSpeakerMap(Mockito.any(ContentfulTalkResponse.class), Mockito.anyMap(), Mockito.anySet()))
                    .thenReturn(Collections.emptyMap());
            mockedStatic.when(() -> ContentfulUtils.getAssetMap(Mockito.any(ContentfulResponse.class)))
                    .thenReturn(Collections.emptyMap());
            mockedStatic.when(() -> ContentfulUtils.getErrorSet(Mockito.any(ContentfulResponse.class), Mockito.anyString()))
                    .thenReturn(Collections.emptySet());
            mockedStatic.when(() -> ContentfulUtils.isValidTalk(Mockito.any(ContentfulTalk.class), Mockito.anyBoolean()))
                    .thenCallRealMethod();

            final Long TALK_DAY = 1L;
            final LocalTime TRACK_TIME = LocalTime.now();
            final Long TRACK = 1L;

            ContentfulTalkResponse<ContentfulTalkFieldsCommon> response = new ContentfulTalkResponseCommon();
            response.setItems(List.of(
                    createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, null, null),
                    createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, null, Boolean.TRUE),
                    createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, null, Boolean.FALSE),
                    createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.TRUE, null),
                    createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.TRUE, Boolean.TRUE),
                    createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.TRUE, Boolean.FALSE),
                    createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.FALSE, null),
                    createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.FALSE, Boolean.TRUE),
                    createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.FALSE, Boolean.FALSE)
            ));

            RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
            Mockito.when(restTemplateMock.getForObject(Mockito.any(URI.class), Mockito.any()))
                    .thenReturn(response);

            mockedStatic.when(ContentfulUtils::getRestTemplate)
                    .thenReturn(restTemplateMock);

            assertEquals(4, ContentfulUtils.getTalks(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "code", true).size());
            assertEquals(4, ContentfulUtils.getTalks(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, null, true).size());
            assertEquals(4, ContentfulUtils.getTalks(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "", true).size());

            assertEquals(9, ContentfulUtils.getTalks(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "code", false).size());
            assertEquals(9, ContentfulUtils.getTalks(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, null, false).size());
            assertEquals(9, ContentfulUtils.getTalks(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "", false).size());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isValidTalk method tests")
    class IsValidTalkTest {
        private Stream<Arguments> data() {
            final Long TALK_DAY = 1L;
            final LocalTime TRACK_TIME = LocalTime.now();
            final Long TRACK = 1L;

            return Stream.of(
                    arguments(createContentfulTalk(null, TRACK_TIME, TRACK, null, null), false, false),
                    arguments(createContentfulTalk(null, TRACK_TIME, TRACK, null, null), true, false),
                    arguments(createContentfulTalk(TALK_DAY, null, TRACK, null, null), false, false),
                    arguments(createContentfulTalk(TALK_DAY, null, TRACK, null, null), true, false),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, null, null, null), false, false),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, null, null, null), true, false),

                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, null, null), false, true),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, null, Boolean.TRUE), false, true),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, null, Boolean.FALSE), false, true),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.TRUE, null), false, true),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.TRUE, Boolean.TRUE), false, true),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.TRUE, Boolean.FALSE), false, true),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.FALSE, null), false, true),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.FALSE, Boolean.TRUE), false, true),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.FALSE, Boolean.FALSE), false, true),

                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, null, null), true, true),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, null, Boolean.TRUE), true, false),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, null, Boolean.FALSE), true, true),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.TRUE, null), true, false),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.TRUE, Boolean.TRUE), true, false),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.TRUE, Boolean.FALSE), true, false),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.FALSE, null), true, true),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.FALSE, Boolean.TRUE), true, false),
                    arguments(createContentfulTalk(TALK_DAY, TRACK_TIME, TRACK, Boolean.FALSE, Boolean.FALSE), true, true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void isValidTalk(ContentfulTalk<? extends ContentfulTalkFields> talk, boolean ignoreDemoStage, boolean expected) {
            assertEquals(expected, ContentfulUtils.isValidTalk(talk, ignoreDemoStage));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void createTalk() {
        try (MockedStatic<ContentfulUtils> mockedStatic = Mockito.mockStatic(ContentfulUtils.class)) {
            mockedStatic.when(() -> ContentfulUtils.createTalk(
                    Mockito.any(ContentfulTalk.class), Mockito.anyMap(), Mockito.anySet(), Mockito.anySet(), Mockito.anyMap(), Mockito.any(AtomicLong.class)))
                    .thenCallRealMethod();
            mockedStatic.when(() -> ContentfulUtils.extractLocaleItems(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                    .thenReturn(Collections.emptyList());
            mockedStatic.when(() -> ContentfulUtils.extractLanguage(Mockito.anyBoolean()))
                    .thenReturn(null);
            mockedStatic.when(() -> ContentfulUtils.extractPresentationLinks(Mockito.anyList(), Mockito.anyMap(), Mockito.anySet(), Mockito.anyString()))
                    .thenReturn(Collections.emptyList());
            mockedStatic.when(() -> ContentfulUtils.combineContentfulLinks(Mockito.anyList(), Mockito.any(ContentfulLink.class)))
                    .thenReturn(Collections.emptyList());
            mockedStatic.when(() -> ContentfulUtils.extractVideoLinks(Mockito.anyString()))
                    .thenReturn(Collections.emptyList());

            ContentfulSys contentfulSys0 = new ContentfulSys();
            contentfulSys0.setId("id0");

            ContentfulSys contentfulSys1 = new ContentfulSys();
            contentfulSys1.setId("id1");

            ContentfulSys contentfulSys2 = new ContentfulSys();
            contentfulSys2.setId("id2");

            ContentfulLink contentfulLink0 = new ContentfulLink();
            contentfulLink0.setSys(contentfulSys0);

            ContentfulLink contentfulLink1 = new ContentfulLink();
            contentfulLink1.setSys(contentfulSys1);

            ContentfulLink contentfulLink2 = new ContentfulLink();
            contentfulLink2.setSys(contentfulSys2);

            ContentfulTalkFieldsCommon contentfulTalkFieldsCommon0 = new ContentfulTalkFieldsCommon();
            contentfulTalkFieldsCommon0.setSpeakers(List.of(contentfulLink0));

            ContentfulTalkFieldsCommon contentfulTalkFieldsCommon1 = new ContentfulTalkFieldsCommon();
            contentfulTalkFieldsCommon1.setSpeakers(List.of(contentfulLink1));

            ContentfulTalkFieldsCommon contentfulTalkFieldsCommon2 = new ContentfulTalkFieldsCommon();
            contentfulTalkFieldsCommon2.setSpeakers(List.of(contentfulLink2));

            ContentfulTalk<ContentfulTalkFieldsCommon> contentfulTalk0 = new ContentfulTalk<>();
            contentfulTalk0.setFields(contentfulTalkFieldsCommon0);

            ContentfulTalk<ContentfulTalkFieldsCommon> contentfulTalk1 = new ContentfulTalk<>();
            contentfulTalk1.setFields(contentfulTalkFieldsCommon1);

            ContentfulTalk<ContentfulTalkFieldsCommon> contentfulTalk2 = new ContentfulTalk<>();
            contentfulTalk2.setFields(contentfulTalkFieldsCommon2);

            Map<String, ContentfulAsset> assetMap = Collections.emptyMap();
            Set<String> entryErrorSet = Set.of("id0");
            Set<String> assetErrorSet = Collections.emptySet();
            Map<String, Speaker> speakerMap = Map.of("id2", new Speaker());

            AtomicLong id0 = new AtomicLong(42);
            AtomicLong id1 = new AtomicLong(43);
            AtomicLong id2 = new AtomicLong(44);

            assertThrows(IllegalArgumentException.class, () -> ContentfulUtils.createTalk(contentfulTalk0, assetMap, entryErrorSet, assetErrorSet, speakerMap, id0));
            assertThrows(NullPointerException.class, () -> ContentfulUtils.createTalk(contentfulTalk1, assetMap, entryErrorSet, assetErrorSet, speakerMap, id1));
            assertEquals(44, ContentfulUtils.createTalk(contentfulTalk2, assetMap, entryErrorSet, assetErrorSet, speakerMap, id2).getId());
        }
    }

    @Test
    void testGetTalks() {
        new MockUp<ContentfulUtils>() {
            @Mock
            List<Talk> getTalks(ContentfulUtils.ConferenceSpaceInfo conferenceSpaceInfo, String conferenceCode, boolean ignoreDemoStage) {
                return Collections.emptyList();
            }

            @Mock
            List<Talk> getTalks(Invocation invocation, Conference conference, String conferenceCode, boolean ignoreDemoStage) {
                return invocation.proceed(conference, conferenceCode, ignoreDemoStage);
            }
        };

        assertDoesNotThrow(() -> ContentfulUtils.getTalks(Conference.JPOINT, "code", true));
        assertDoesNotThrow(() -> ContentfulUtils.getTalks(Conference.JPOINT, "code", false));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakerMap method tests")
    class GetSpeakerMapTest {
        private final Speaker speaker;

        public GetSpeakerMapTest() {
            speaker = new Speaker();
            speaker.setId(42);
        }

        private Stream<Arguments> data() {
            ContentfulSys contentfulSys1 = new ContentfulSys();
            contentfulSys1.setId("id1");

            ContentfulSpeaker contentfulSpeaker1 = new ContentfulSpeaker();
            contentfulSpeaker1.setSys(contentfulSys1);

            ContentfulTalkIncludes contentfulTalkIncludes1 = new ContentfulTalkIncludes();
            contentfulTalkIncludes1.setEntry(List.of(contentfulSpeaker1));

            ContentfulTalkResponseCommon contentfulTalkResponseCommon0 = new ContentfulTalkResponseCommon();

            ContentfulTalkResponseCommon contentfulTalkResponseCommon1 = new ContentfulTalkResponseCommon();
            contentfulTalkResponseCommon1.setIncludes(contentfulTalkIncludes1);

            return Stream.of(
                    arguments(contentfulTalkResponseCommon0, null, null, Collections.emptyMap()),
                    arguments(contentfulTalkResponseCommon1, null, null, Map.of("id1", speaker))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakerMap(ContentfulTalkResponse<? extends ContentfulTalkFields> response,
                           Map<String, ContentfulAsset> assetMap, Set<String> assetErrorSet,
                           Map<String, Speaker> expected) {
            new MockUp<ContentfulUtils>() {
                @Mock
                Speaker createSpeaker(ContentfulSpeaker contentfulSpeaker, Map<String, ContentfulAsset> assetMap,
                                      Set<String> assetErrorSet, AtomicLong speakerId, AtomicLong companyId, boolean checkEnTextExistence) {
                    return speaker;
                }

                @Mock
                Map<String, Speaker> getSpeakerMap(Invocation invocation, ContentfulTalkResponse<? extends ContentfulTalkFields> response,
                                                   Map<String, ContentfulAsset> assetMap, Set<String> assetErrorSet) {
                    return invocation.proceed(response, assetMap, assetErrorSet);
                }
            };

            assertEquals(expected, ContentfulUtils.getSpeakerMap(response, assetMap, assetErrorSet));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getAssetMap method tests")
    class GetAssetMapTest {
        private Stream<Arguments> data() {
            ContentfulSys contentfulSys1 = new ContentfulSys();
            contentfulSys1.setId("id1");

            ContentfulAsset contentfulAsset1 = new ContentfulAsset();
            contentfulAsset1.setSys(contentfulSys1);

            ContentfulIncludes contentfulIncludes1 = new ContentfulIncludes();
            contentfulIncludes1.setAsset(List.of(contentfulAsset1));

            ContentfulSpeakerResponse response0 = new ContentfulSpeakerResponse();

            ContentfulSpeakerResponse response1 = new ContentfulSpeakerResponse();
            response1.setIncludes(contentfulIncludes1);

            return Stream.of(
                    arguments(response0, Collections.emptyMap()),
                    arguments(response1, Map.of("id1", contentfulAsset1))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getAssetMap(ContentfulResponse<?, ? extends ContentfulIncludes> response,
                         Map<String, ContentfulAsset> expected) {
            assertEquals(expected, ContentfulUtils.getAssetMap(response));
        }
    }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getCityMap method tests")
    class GetCityMapTest {
        private Stream<Arguments> data() {
            ContentfulSys contentfulSys1 = new ContentfulSys();
            contentfulSys1.setId("id1");

            ContentfulCity contentfulCity1 = new ContentfulCity();
            contentfulCity1.setSys(contentfulSys1);

            ContentfulEventIncludes contentfulIncludes1 = new ContentfulEventIncludes();
            contentfulIncludes1.setEntry(List.of(contentfulCity1));

            ContentfulEventResponse response0 = new ContentfulEventResponse();

            ContentfulEventResponse response1 = new ContentfulEventResponse();
            response1.setIncludes(contentfulIncludes1);

            return Stream.of(
                    arguments(response0, Collections.emptyMap()),
                    arguments(response1, Map.of("id1", contentfulCity1))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getCityMap(ContentfulEventResponse response, Map<String, ContentfulCity> expected) {
            assertEquals(expected, ContentfulUtils.getCityMap(response));
        }
    }

    private static ContentfulError createContentfulError(boolean isSysNotNull, boolean isDetailsNotNull, String sysId,
                                                         String sysType, String detailsType, String detailsLinkType, String detailsId) {
        ContentfulError contentfulError = new ContentfulError();

        if (isSysNotNull) {
            ContentfulSys sys = new ContentfulSys();
            sys.setId(sysId);
            sys.setType(sysType);

            contentfulError.setSys(sys);
        }

        if (isDetailsNotNull) {
            ContentfulErrorDetails details = new ContentfulErrorDetails();
            details.setType(detailsType);
            details.setLinkType(detailsLinkType);
            details.setId(detailsId);

            contentfulError.setDetails(details);
        }

        return contentfulError;
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getErrorSet method tests")
    class GetErrorSetTest {

        private Stream<Arguments> data() {
            ContentfulEventResponse response0 = new ContentfulEventResponse();

            ContentfulEventResponse response1 = new ContentfulEventResponse();
            response1.setErrors(List.of(
                    createContentfulError(false, false, null, null, null, null, "id0"),
                    createContentfulError(true, false, null, null, null, null, "id1"),
                    createContentfulError(true, false, "notResolvable", null, null, null, "id2"),
                    createContentfulError(true, false, null, "error", null, null, "id3"),
                    createContentfulError(true, false, "notResolvable", "error", null, null, "id4"),
                    createContentfulError(false, true, null, null, null, null, "id5"),
                    createContentfulError(false, true, null, null, "Link", null, "id6"),
                    createContentfulError(false, true, null, null, null, ContentfulUtils.ENTRY_LINK_TYPE, "id7"),
                    createContentfulError(false, true, null, null, "Link", ContentfulUtils.ENTRY_LINK_TYPE, "id8"),
                    createContentfulError(true, true, null, null, null, null, "id10"),
                    createContentfulError(true, true, null, null, null, ContentfulUtils.ENTRY_LINK_TYPE, "id10"),
                    createContentfulError(true, true, null, null, "Link", null, "id11"),
                    createContentfulError(true, true, null, null, "Link", ContentfulUtils.ENTRY_LINK_TYPE, "id12"),
                    createContentfulError(true, true, null, "error", null, null, "id13"),
                    createContentfulError(true, true, null, "error", null, ContentfulUtils.ENTRY_LINK_TYPE, "id14"),
                    createContentfulError(true, true, null, "error", "Link", null, "id15"),
                    createContentfulError(true, true, null, "error", "Link", ContentfulUtils.ENTRY_LINK_TYPE, "id16"),
                    createContentfulError(true, true, "notResolvable", null, null, null, "id17"),
                    createContentfulError(true, true, "notResolvable", null, null, ContentfulUtils.ENTRY_LINK_TYPE, "id18"),
                    createContentfulError(true, true, "notResolvable", null, "Link", null, "id19"),
                    createContentfulError(true, true, "notResolvable", null, "Link", ContentfulUtils.ENTRY_LINK_TYPE, "id20"),
                    createContentfulError(true, true, "notResolvable", "error", null, null, "id21"),
                    createContentfulError(true, true, "notResolvable", "error", null, ContentfulUtils.ENTRY_LINK_TYPE, "id22"),
                    createContentfulError(true, true, "notResolvable", "error", "Link", null, "id23"),
                    createContentfulError(true, true, "notResolvable", "error", "Link", ContentfulUtils.ENTRY_LINK_TYPE, "id24")
            ));

            return Stream.of(
                    arguments(response0, null, Collections.emptySet()),
                    arguments(response1, "", Collections.emptySet()),
                    arguments(response1, ContentfulUtils.ENTRY_LINK_TYPE, Set.of("id24"))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getErrorSet(ContentfulResponse<?, ? extends ContentfulIncludes> response, String linkType, Set<String> expected) {
            assertEquals(expected, ContentfulUtils.getErrorSet(response, linkType));
        }
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
                            null, "abc"),
                    arguments("abc", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            null, "abc"),
                    arguments(" abc", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            null, "abc"),
                    arguments("abc ", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            null, "abc"),
                    arguments(" abc ", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            null, "abc"),
                    arguments("42", new ExtractSet(
                                    List.of(new ExtractPair("([a-z]+)", 1)),
                                    "Invalid property: %s"),
                            IllegalArgumentException.class, null),
                    arguments("42", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            IllegalArgumentException.class, null),
                    arguments(" 42", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            IllegalArgumentException.class, null),
                    arguments("42 ", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            IllegalArgumentException.class, null),
                    arguments(" 42 ", new ExtractSet(
                                    List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                                    "Invalid property: %s"),
                            IllegalArgumentException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractProperty(String value, ExtractSet extractSet, Class<? extends Throwable> expectedException, String expectedValue) {
            if (expectedException == null) {
                assertEquals(expectedValue, ContentfulUtils.extractProperty(value, extractSet));
            } else {
                assertThrows(expectedException, () -> ContentfulUtils.extractProperty(value, extractSet));
            }
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
    @DisplayName("extractPresentationLinks method tests")
    class ExtractPresentationLinksTest {
        private static final String ASSET_URL = "https://valid.com";

        private Stream<Arguments> data() {
            ContentfulSys contentfulSys0 = new ContentfulSys();
            contentfulSys0.setId("id0");

            ContentfulSys contentfulSys1 = new ContentfulSys();
            contentfulSys1.setId("id1");

            ContentfulSys contentfulSys2 = new ContentfulSys();
            contentfulSys2.setId("id2");

            ContentfulLink contentfulLink0 = new ContentfulLink();
            contentfulLink0.setSys(contentfulSys0);

            ContentfulLink contentfulLink1 = new ContentfulLink();
            contentfulLink1.setSys(contentfulSys1);

            ContentfulLink contentfulLink2 = new ContentfulLink();
            contentfulLink2.setSys(contentfulSys2);

            ContentfulAssetFields contentfulAssetFields1 = new ContentfulAssetFields();
            contentfulAssetFields1.setFile(new ContentfulAssetFieldsFile());

            ContentfulAsset contentfulAsset1 = new ContentfulAsset();
            contentfulAsset1.setFields(contentfulAssetFields1);

            Map<String, ContentfulAsset> assetMap = Map.of("id1", contentfulAsset1);
            Set<String> assetErrorSet = Set.of("id0");

            return Stream.of(
                    arguments(null, null, null, null, null, Collections.emptyList()),
                    arguments(List.of(contentfulLink0, contentfulLink1), assetMap, assetErrorSet, "talkNameEn", null, List.of(ASSET_URL)),
                    arguments(List.of(contentfulLink2), assetMap, assetErrorSet, "talkNameEn", NullPointerException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractPresentationLinks(List<ContentfulLink> links, Map<String, ContentfulAsset> assetMap,
                                      Set<String> assetErrorSet, String talkNameEn, Class<? extends Throwable> expectedException,
                                      List<String> expectedValue) {
            new MockUp<ContentfulUtils>() {
                @Mock
                List<String> extractPresentationLinks(Invocation invocation, List<ContentfulLink> links,
                                                      Map<String, ContentfulAsset> assetMap, Set<String> assetErrorSet,
                                                      String talkNameEn) {
                    return invocation.proceed(links, assetMap, assetErrorSet, talkNameEn);
                }

                @Mock
                String extractAssetUrl(String value) {
                    return ASSET_URL;
                }
            };

            if (expectedException == null) {
                assertEquals(expectedValue, ContentfulUtils.extractPresentationLinks(links, assetMap, assetErrorSet, talkNameEn));
            } else {
                assertThrows(expectedException, () -> ContentfulUtils.extractPresentationLinks(links, assetMap, assetErrorSet, talkNameEn));
            }
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
                    arguments(null, null, null),
                    arguments("", null, ""),
                    arguments(" ", null, ""),
                    arguments("//assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf",
                            null, "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"),
                    arguments(" //assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf",
                            null, "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"),
                    arguments("//assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf ",
                            null, "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"),
                    arguments(" //assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf ",
                            null, "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"),
                    arguments("http://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf",
                            null, "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"),
                    arguments("https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf",
                            null, "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"),
                    arguments("abc", IllegalArgumentException.class, null),
                    arguments("42", IllegalArgumentException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractAssetUrl(String value, Class<? extends Throwable> expectedException, String expectedValue) {
            if (expectedException == null) {
                assertEquals(expectedValue, ContentfulUtils.extractAssetUrl(value));
            } else {
                assertThrows(expectedException, () -> ContentfulUtils.extractAssetUrl(value));
            }
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
                    arguments(null, null, null, null),
                    arguments(null, "", null, null),
                    arguments(null, "abc", null, null),
                    arguments("abc", "en", null, "abc"),
                    arguments("Moscow", "en", null, " Msc"),
                    arguments("Moscow ", "en", null, " Msc"),
                    arguments(" Moscow ", "en", null, " Msc"),
                    arguments("abc Moscow", "en", null, "abc Msc"),
                    arguments("abc Moscow ", "en", null, "abc Msc"),
                    arguments("Moscow cde", "en", null, "Moscow cde"),
                    arguments(" Moscow cde", "en", null, " Moscow cde"),
                    arguments("abc Moscow cde", "en", null, "abc Moscow cde"),
                    arguments("Piter", "en", null, " SPb"),
                    arguments("Piter ", "en", null, " SPb"),
                    arguments(" Piter ", "en", null, " SPb"),
                    arguments("abc Piter", "en", null, "abc SPb"),
                    arguments("abc Piter ", "en", null, "abc SPb"),
                    arguments("Piter cde", "en", null, "Piter cde"),
                    arguments(" Piter cde", "en", null, " Piter cde"),
                    arguments("abc Piter cde", "en", null, "abc Piter cde"),
                    arguments("Moscow", "ru-RU", null, " Мск"),
                    arguments("Moscow ", "ru-RU", null, " Мск"),
                    arguments(" Moscow ", "ru-RU", null, " Мск"),
                    arguments("abc Moscow", "ru-RU", null, "abc Мск"),
                    arguments("abc Moscow ", "ru-RU", null, "abc Мск"),
                    arguments("Moscow cde", "ru-RU", null, "Moscow cde"),
                    arguments(" Moscow cde", "ru-RU", null, " Moscow cde"),
                    arguments("abc Moscow cde", "ru-RU", null, "abc Moscow cde"),
                    arguments("Piter", "ru-RU", null, " СПб"),
                    arguments("Piter ", "ru-RU", null, " СПб"),
                    arguments(" Piter ", "ru-RU", null, " СПб"),
                    arguments("abc Piter", "ru-RU", null, "abc СПб"),
                    arguments("abc Piter ", "ru-RU", null, "abc СПб"),
                    arguments("Piter cde", "ru-RU", null, "Piter cde"),
                    arguments(" Piter cde", "ru-RU", null, " Piter cde"),
                    arguments("abc Piter cde", "ru-RU", null, "abc Piter cde"),
                    arguments("abc", "", IllegalArgumentException.class, null),
                    arguments("abc", "unknown", IllegalArgumentException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractEventName(String name, String locale, Class<? extends Throwable> expectedException, String expectedValue) {
            if (expectedException == null) {
                assertEquals(expectedValue, ContentfulUtils.extractEventName(name, locale));
            } else {
                assertThrows(expectedException, () -> ContentfulUtils.extractEventName(name, locale));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractLocaleValue method tests")
    class ExtractLocaleValueTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, null),
                    arguments(Map.of("en", "value"), "en", "value"),
                    arguments(Map.of("en", "value"), "ru", null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractLocaleValue(Map<String, String> map, String locale, String expected) {
            assertEquals(expected, ContentfulUtils.extractLocaleValue(map, locale));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractCity method tests")
    class ExtractCityTest {
        private Stream<Arguments> data() {
            ContentfulSys contentfulSys0 = new ContentfulSys();
            contentfulSys0.setId("id0");

            ContentfulSys contentfulSys1 = new ContentfulSys();
            contentfulSys1.setId("id1");

            ContentfulSys contentfulSys2 = new ContentfulSys();
            contentfulSys2.setId("id2");

            ContentfulLink contentfulLink0 = new ContentfulLink();
            contentfulLink0.setSys(contentfulSys0);

            ContentfulLink contentfulLink1 = new ContentfulLink();
            contentfulLink1.setSys(contentfulSys1);

            ContentfulLink contentfulLink2 = new ContentfulLink();
            contentfulLink2.setSys(contentfulSys2);

            ContentfulCityFields contentfulCityFields1 = new ContentfulCityFields();
            contentfulCityFields1.setCityName(Map.of("en", "Name1"));

            ContentfulCity contentfulCity1 = new ContentfulCity();
            contentfulCity1.setFields(contentfulCityFields1);

            Map<String, ContentfulCity> cityMap = Map.of("id1", contentfulCity1);
            Set<String> entryErrorSet = Set.of("id0");

            return Stream.of(
                    arguments(contentfulLink0, cityMap, entryErrorSet, null, "eventName", IllegalArgumentException.class, null),
                    arguments(contentfulLink1, cityMap, entryErrorSet, "en", "eventName", null, "Name1"),
                    arguments(contentfulLink2, cityMap, entryErrorSet, null, "eventName", NullPointerException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractCity(ContentfulLink link, Map<String, ContentfulCity> cityMap, Set<String> entryErrorSet, String locale,
                         String eventName, Class<? extends Throwable> expectedException, String expectedValue) {
            if (expectedException == null) {
                assertEquals(expectedValue, ContentfulUtils.extractCity(link, cityMap, entryErrorSet, locale, eventName));
            } else {
                assertThrows(expectedException, () -> ContentfulUtils.extractCity(link, cityMap, entryErrorSet, locale, eventName));
            }
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
                            new Nameable(
                                    -1L,
                                    List.of(
                                            new LocaleItem("en", "DotNext 2016 Helsinki"),
                                            new LocaleItem("ru", "DotNext 2016 Хельсинки"))
                            ),
                            null,
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
                            "Europe/Helsinki",
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
    @DisplayName("fixEntryNotResolvableError method tests")
    class FixEntryNotResolvableErrorTest {
        private Stream<Arguments> createStream(ContentfulUtils.ConferenceSpaceInfo existingConferenceSpaceInfo, String existingEntryId) {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(-1);

            return Stream.of(
                    arguments(
                            null,
                            new HashSet<>(),
                            new HashMap<>(),
                            Collections.emptySet(),
                            Collections.emptyMap()),
                    arguments(
                            null,
                            new HashSet<>(),
                            new HashMap<>(Map.of(existingEntryId, speaker0)),
                            Collections.emptySet(),
                            Map.of(existingEntryId, speaker0)),
                    arguments(
                            null,
                            new HashSet<>(Set.of(existingEntryId)),
                            new HashMap<>(),
                            Set.of(existingEntryId),
                            Collections.emptyMap()),
                    arguments(
                            null,
                            new HashSet<>(Set.of(existingEntryId)),
                            new HashMap<>(Map.of(existingEntryId, speaker0)),
                            Set.of(existingEntryId),
                            Map.of(existingEntryId, speaker0)),
                    arguments(
                            existingConferenceSpaceInfo,
                            new HashSet<>(),
                            new HashMap<>(),
                            Collections.emptySet(),
                            Collections.emptyMap()),
                    arguments(
                            existingConferenceSpaceInfo,
                            new HashSet<>(),
                            new HashMap<>(Map.of(existingEntryId, speaker0)),
                            Collections.emptySet(),
                            Map.of(existingEntryId, speaker0)),
                    arguments(
                            existingConferenceSpaceInfo,
                            new HashSet<>(Set.of(existingEntryId)),
                            new HashMap<>(),
                            Collections.emptySet(),
                            Map.of(existingEntryId, speaker1)),
                    arguments(
                            existingConferenceSpaceInfo,
                            new HashSet<>(Set.of(existingEntryId)),
                            new HashMap<>(Map.of(existingEntryId, speaker0)),
                            Set.of(existingEntryId),
                            Map.of(existingEntryId, speaker0))
            );
        }

        private Stream<Arguments> data() {
            return Stream.concat(
                    Stream.concat(
                            Stream.concat(
                                    Stream.concat(
                                            Stream.concat(
                                                    Stream.concat(
                                                            createStream(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "6yIC7EpG1EhejCEJDEsuqA"),
                                                            createStream(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "2i2OfmHelyMCiK2sCUoGsS")
                                                    ),
                                                    createStream(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "1FDbCMYfsEkiQG6s8CWQwS")
                                            ),
                                            createStream(ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO, "MPZSTxFNbbjBdf5M5uoOZ")
                                    ),
                                    createStream(ContentfulUtils.ConferenceSpaceInfo.HOLY_JS_SPACE_INFO, "3YSoYRePW0OIeaAAkaweE6")
                            ),
                            createStream(ContentfulUtils.ConferenceSpaceInfo.HOLY_JS_SPACE_INFO, "2UddvLNyXmy4YaukAuE4Ao")
                    ),
                    createStream(ContentfulUtils.ConferenceSpaceInfo.MOBIUS_SPACE_INFO, "33qzWXnXYsgyCsSiwK0EOy")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void fixEntryNotResolvableError(ContentfulUtils.ConferenceSpaceInfo conferenceSpaceInfo,
                                        Set<String> entryErrorSet, Map<String, Speaker> speakerMap,
                                        Set<String> expectedEntryErrorSet, Map<String, Speaker> expectedSpeakerMap) {
            assertDoesNotThrow(() -> ContentfulUtils.fixEntryNotResolvableError(conferenceSpaceInfo, entryErrorSet, speakerMap));
            assertEquals(expectedEntryErrorSet, entryErrorSet);
            assertEquals(expectedSpeakerMap, speakerMap);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("needUpdate method tests (EventType)")
    class NeedUpdateEventTypeTest {
        private Stream<Arguments> data() {
            Organizer organizer0 = new Organizer(0, Collections.emptyList());
            Organizer organizer1 = new Organizer(1, Collections.emptyList());

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
            eventType0.setOrganizer(organizer0);
            eventType0.setTimeZone("Europe/Moscow");

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

            EventType eventType13 = new EventType();
            eventType13.setId(0);
            eventType13.setConference(Conference.JPOINT);
            eventType13.setLogoFileName("logoFileName0");
            eventType13.setName(List.of(new LocaleItem("en", "name0")));
            eventType13.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType13.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType13.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType13.setVkLink("vkLink0");
            eventType13.setTwitterLink("twitterLink0");
            eventType13.setFacebookLink("facebookLink0");
            eventType13.setYoutubeLink("youtubeLink0");
            eventType13.setTelegramLink("telegramLink0");
            eventType13.setOrganizer(organizer1);

            EventType eventType14 = new EventType();
            eventType14.setId(0);
            eventType14.setConference(Conference.JPOINT);
            eventType14.setLogoFileName("logoFileName0");
            eventType14.setName(List.of(new LocaleItem("en", "name0")));
            eventType14.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType14.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType14.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType14.setVkLink("vkLink0");
            eventType14.setTwitterLink("twitterLink0");
            eventType14.setFacebookLink("facebookLink0");
            eventType14.setYoutubeLink("youtubeLink0");
            eventType14.setTelegramLink("telegramLink0");
            eventType14.setOrganizer(organizer0);

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
                    arguments(eventType0, eventType12, true),
                    arguments(eventType0, eventType13, true),
                    arguments(eventType0, eventType14, true)
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
            ZonedDateTime now = ZonedDateTime.now();

            Company company0 = new Company(0, List.of(new LocaleItem("en", "company0")));
            Company company5 = new Company(4, List.of(new LocaleItem("en", "company4")));

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setPhotoFileName("photoFileName0");
            speaker0.setPhotoUpdatedAt(now);
            speaker0.setName(List.of(new LocaleItem("en", "name0")));
            speaker0.setCompanies(List.of(company0));
            speaker0.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker0.setTwitter("twitter0");
            speaker0.setGitHub("gitHub0");
            speaker0.setHabr("habr0");
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
            speaker3.setPhotoUpdatedAt(now.plus(1, ChronoUnit.DAYS));

            Speaker speaker4 = new Speaker();
            speaker4.setId(0);
            speaker4.setPhotoFileName("photoFileName0");
            speaker4.setPhotoUpdatedAt(now);
            speaker4.setName(List.of(new LocaleItem("en", "name3")));

            Speaker speaker5 = new Speaker();
            speaker5.setId(0);
            speaker5.setPhotoFileName("photoFileName0");
            speaker5.setPhotoUpdatedAt(now);
            speaker5.setName(List.of(new LocaleItem("en", "name0")));
            speaker5.setCompanies(List.of(company5));

            Speaker speaker6 = new Speaker();
            speaker6.setId(0);
            speaker6.setPhotoFileName("photoFileName0");
            speaker6.setPhotoUpdatedAt(now);
            speaker6.setName(List.of(new LocaleItem("en", "name0")));
            speaker6.setCompanies(List.of(company0));
            speaker6.setBio(List.of(new LocaleItem("en", "bio6")));

            Speaker speaker7 = new Speaker();
            speaker7.setId(0);
            speaker7.setPhotoFileName("photoFileName0");
            speaker7.setPhotoUpdatedAt(now);
            speaker7.setName(List.of(new LocaleItem("en", "name0")));
            speaker7.setCompanies(List.of(company0));
            speaker7.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker7.setTwitter("twitter7");

            Speaker speaker8 = new Speaker();
            speaker8.setId(0);
            speaker8.setPhotoFileName("photoFileName0");
            speaker8.setPhotoUpdatedAt(now);
            speaker8.setName(List.of(new LocaleItem("en", "name0")));
            speaker8.setCompanies(List.of(company0));
            speaker8.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker8.setTwitter("twitter0");
            speaker8.setGitHub("gitHub8");

            Speaker speaker9 = new Speaker();
            speaker9.setId(0);
            speaker9.setPhotoFileName("photoFileName0");
            speaker9.setPhotoUpdatedAt(now);
            speaker9.setName(List.of(new LocaleItem("en", "name0")));
            speaker9.setCompanies(List.of(company0));
            speaker9.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker9.setTwitter("twitter0");
            speaker9.setGitHub("gitHub0");
            speaker9.setHabr("habr9");

            Speaker speaker10 = new Speaker();
            speaker10.setId(0);
            speaker10.setPhotoFileName("photoFileName0");
            speaker10.setPhotoUpdatedAt(now);
            speaker10.setName(List.of(new LocaleItem("en", "name0")));
            speaker10.setCompanies(List.of(company0));
            speaker10.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker10.setTwitter("twitter0");
            speaker10.setGitHub("gitHub0");
            speaker10.setHabr("habr0");
            speaker10.setJavaChampion(false);

            Speaker speaker11 = new Speaker();
            speaker11.setId(0);
            speaker11.setPhotoFileName("photoFileName0");
            speaker11.setPhotoUpdatedAt(now);
            speaker11.setName(List.of(new LocaleItem("en", "name0")));
            speaker11.setCompanies(List.of(company0));
            speaker11.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker11.setTwitter("twitter0");
            speaker11.setGitHub("gitHub0");
            speaker11.setHabr("habr0");
            speaker11.setJavaChampion(true);
            speaker11.setMvp(false);

            Speaker speaker12 = new Speaker();
            speaker12.setId(0);
            speaker12.setPhotoFileName("photoFileName0");
            speaker12.setPhotoUpdatedAt(now);
            speaker12.setName(List.of(new LocaleItem("en", "name0")));
            speaker12.setCompanies(List.of(company0));
            speaker12.setBio(List.of(new LocaleItem("en", "bio0")));
            speaker12.setTwitter("twitter0");
            speaker12.setGitHub("gitHub0");
            speaker12.setHabr("habr0");
            speaker12.setJavaChampion(true);
            speaker12.setMvp(true);
            speaker12.setMvpReconnect(false);

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
                    arguments(speaker0, speaker10, true),
                    arguments(speaker0, speaker11, true),
                    arguments(speaker0, speaker12, true)
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
            event0.setTimeZone("Europe/Moscow");

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

            Event event9 = new Event();
            event9.setEventTypeId(0);
            event9.setName(List.of(new LocaleItem("en", "name0")));
            event9.setStartDate(LocalDate.of(2020, 8, 5));
            event9.setEndDate(LocalDate.of(2020, 8, 6));
            event9.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            event9.setYoutubeLink("youtubeLink0");
            event9.setPlaceId(0);
            event9.setTalkIds(List.of(0L));

            return Stream.of(
                    arguments(event0, event0, false),
                    arguments(event0, event1, true),
                    arguments(event0, event2, true),
                    arguments(event0, event3, true),
                    arguments(event0, event4, true),
                    arguments(event0, event5, true),
                    arguments(event0, event6, true),
                    arguments(event0, event7, true),
                    arguments(event0, event8, true),
                    arguments(event0, event9, true)
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
    @DisplayName("needPhotoUpdate method tests")
    class NeedPhotoUpdateTest {
        private Stream<Arguments> data() {
            final ZonedDateTime NOW = ZonedDateTime.now();
            final ZonedDateTime YESTERDAY = NOW.minus(1, ChronoUnit.DAYS);
            final String VALID_URL = "https://valid.com";
            final String PHOTO_FILE_NAME = "0000.jpg";

            return Stream.of(
                    arguments(null, null, VALID_URL, PHOTO_FILE_NAME, true, true),
                    arguments(null, NOW, VALID_URL, PHOTO_FILE_NAME, true, true),
                    arguments(null, null, VALID_URL, PHOTO_FILE_NAME, false, false),
                    arguments(null, NOW, VALID_URL, PHOTO_FILE_NAME, false, false),
                    arguments(NOW, null, VALID_URL, PHOTO_FILE_NAME, true, true),
                    arguments(NOW, null, VALID_URL, PHOTO_FILE_NAME, false, true),
                    arguments(NOW, NOW, VALID_URL, PHOTO_FILE_NAME, true, false),
                    arguments(NOW, NOW, VALID_URL, PHOTO_FILE_NAME, false, false),
                    arguments(NOW, YESTERDAY, VALID_URL, PHOTO_FILE_NAME, true, true),
                    arguments(NOW, YESTERDAY, VALID_URL, PHOTO_FILE_NAME, false, true),
                    arguments(YESTERDAY, NOW, VALID_URL, PHOTO_FILE_NAME, true, false),
                    arguments(YESTERDAY, NOW, VALID_URL, PHOTO_FILE_NAME, false, false)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void needPhotoUpdate(ZonedDateTime targetPhotoUpdatedAt, ZonedDateTime resourcePhotoUpdatedAt,
                             String targetPhotoUrl, String resourcePhotoFileName, boolean needUpdate, boolean expected) throws IOException {
            new MockUp<ImageUtils>() {
                @Mock
                boolean needUpdate(String targetPhotoUrl, String resourceFileName) throws IOException {
                    return needUpdate;
                }
            };

            assertEquals(expected, ContentfulUtils.needPhotoUpdate(targetPhotoUpdatedAt, resourcePhotoUpdatedAt,
                    targetPhotoUrl, resourcePhotoFileName));
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
        void equals(List<String> a, List<String> b, boolean expected) {
            assertEquals(expected, ContentfulUtils.equals(a, b));
        }
    }

    @Test
    void iterateAllEntities() {
        new MockUp<ContentfulUtils>() {
            @Mock
            List<String> getLocales() {
                return Collections.emptyList();
            }

            @Mock
            List<EventType> getEventTypes() {
                return Collections.emptyList();
            }

            @Mock
            List<Event> getEvents(String eventName, LocalDate startDate) {
                return Collections.emptyList();
            }

            @Mock
            List<Speaker> getSpeakers(ContentfulUtils.ConferenceSpaceInfo conferenceSpaceInfo, String conferenceCode) {
                return Collections.emptyList();
            }

            @Mock
            List<Talk> getTalks(ContentfulUtils.ConferenceSpaceInfo conferenceSpaceInfo, String conferenceCode, boolean ignoreDemoStage) {
                return Collections.emptyList();
            }

            @Mock
            void iterateAllEntities(Invocation invocation) {
                invocation.proceed();
            }
        };

        assertDoesNotThrow(ContentfulUtils::iterateAllEntities);
    }
}
