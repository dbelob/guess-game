package guess.util;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.contentful.ContentfulLink;
import guess.domain.source.contentful.ContentfulSys;
import guess.domain.source.extract.ExtractPair;
import guess.domain.source.extract.ExtractSet;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static junit.framework.TestCase.assertEquals;

@RunWith(Enclosed.class)
public class ContentfulUtilsTest {
    @RunWith(Parameterized.class)
    public static class GetFirstMapValueTest {
        @Parameters
        public static Collection<Object[]> data() {
            Map<String, String> map0 = Map.of("key1", "value1");

            Map<String, String> map1 = new LinkedHashMap<>();
            map1.put("key1", "value1");
            map1.put("key2", "value2");

            Map<String, String> map2 = new LinkedHashMap<>();
            map2.put("key2", "value2");
            map2.put("key1", "value1");

            return Arrays.asList(new Object[][]{
                    {map0, "value1"},
                    {map1, "value1"},
                    {map2, "value2"}
            });
        }

        private final Map<String, String> map;
        private final String expected;

        public GetFirstMapValueTest(Map<String, String> map, String expected) {
            this.map = map;
            this.expected = expected;
        }

        @Test
        public void getFirstMapValue() {
            assertEquals(expected, ContentfulUtils.getFirstMapValue(map));
        }
    }

    @RunWith(Parameterized.class)
    public static class CreateUtcZonedDateTimeTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {LocalDate.of(2020, 1, 1), ZonedDateTime.of(2019, 12, 31, 21, 0, 0, 0, ZoneId.of("UTC"))},
                    {LocalDate.of(2020, 12, 31), ZonedDateTime.of(2020, 12, 30, 21, 0, 0, 0, ZoneId.of("UTC"))}
            });
        }

        private final LocalDate localDate;
        private final ZonedDateTime expected;

        public CreateUtcZonedDateTimeTest(LocalDate localDate, ZonedDateTime expected) {
            this.localDate = localDate;
            this.expected = expected;
        }

        @Test
        public void createUtcZonedDateTime() {
            assertEquals(expected, ContentfulUtils.createUtcZonedDateTime(localDate));
        }
    }

    @RunWith(Parameterized.class)
    public static class CreateEventLocalDateTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"2020-01-01T00:00+03:00", LocalDate.of(2020, 1, 1)},
                    {"2020-12-31T00:00+03:00", LocalDate.of(2020, 12, 31)}
            });
        }

        private final String zonedDateTimeString;
        private final LocalDate expected;

        public CreateEventLocalDateTest(String zonedDateTimeString, LocalDate expected) {
            this.zonedDateTimeString = zonedDateTimeString;
            this.expected = expected;
        }

        @Test
        public void createUtcZonedDateTime() {
            assertEquals(expected, ContentfulUtils.createEventLocalDate(zonedDateTimeString));
        }
    }

    @RunWith(Parameterized.class)
    public static class GetEventTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {Conference.DOT_NEXT, LocalDate.of(2016, 12, 7), new Event(
                            -1L,
                            null,
                            List.of(
                                    new LocaleItem("en", "DotNext 2016 Helsinki"),
                                    new LocaleItem("ru", "DotNext 2016 Хельсинки")),
                            LocalDate.of(2016, 12, 7),
                            LocalDate.of(2016, 12, 7),
                            List.of(
                                    new LocaleItem("en", "https://dotnext-helsinki.com"),
                                    new LocaleItem("ru", "https://dotnext-helsinki.com")),
                            "https://www.youtube.com/playlist?list=PLtWrKx3nUGBcaA5j9UT6XMnoGM6a2iCE5",
                            new Place(
                                    15,
                                    List.of(
                                            new LocaleItem("en", "Helsinki"),
                                            new LocaleItem("ru", "Хельсинки")),
                                    List.of(
                                            new LocaleItem("en", "Microsoft Talo, Keilalahdentie 2-4, 02150 Espoo")),
                                    "60.1704769, 24.8279349"),
                            Collections.emptyList())}
            });
        }

        private final Conference conference;
        private final LocalDate startDate;
        private final Event expected;

        public GetEventTest(Conference conference, LocalDate startDate, Event expected) {
            this.conference = conference;
            this.startDate = startDate;
            this.expected = expected;
        }

        @Test
        public void getEvent() {
            Event event = ContentfulUtils.getEvent(conference, startDate);

            assertEquals(expected, event);
            assertEquals(expected.getName(), event.getName());
            assertEquals(expected.getStartDate(), event.getStartDate());
            assertEquals(expected.getEndDate(), event.getEndDate());
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractStringTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, null},
                    {"", ""},
                    {" value0", "value0"},
                    {"value1 ", "value1"},
                    {" value2 ", "value2"}
            });
        }

        private final String value;
        private final String expected;

        public ExtractStringTest(String value, String expected) {
            this.value = value;
            this.expected = expected;
        }

        @Test
        public void extractBoolean() {
            assertEquals(expected, ContentfulUtils.extractString(value));
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractBooleanTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, false},
                    {Boolean.TRUE, true},
                    {Boolean.FALSE, false}
            });
        }

        private final Boolean value;
        private final boolean expected;

        public ExtractBooleanTest(Boolean value, boolean expected) {
            this.value = value;
            this.expected = expected;
        }

        @Test
        public void extractBoolean() {
            assertEquals(expected, ContentfulUtils.extractBoolean(value));
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractPropertyTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"abc", new ExtractSet(
                            List.of(new ExtractPair("([a-z]+)", 1)),
                            "Invalid property: %s"),
                            "abc"},
                    {"abc", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s"),
                            "abc"},
                    {" abc", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s"),
                            "abc"},
                    {"abc ", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s"),
                            "abc"},
                    {" abc ", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s"),
                            "abc"}
            });
        }

        private final String value;
        private final ExtractSet extractSet;
        private final String expected;

        public ExtractPropertyTest(String value, ExtractSet extractSet, String expected) {
            this.value = value;
            this.extractSet = extractSet;
            this.expected = expected;
        }

        @Test
        public void extractProperty() {
            assertEquals(expected, ContentfulUtils.extractProperty(value, extractSet));
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractPropertyWithExceptionTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"42", new ExtractSet(
                            List.of(new ExtractPair("([a-z]+)", 1)),
                            "Invalid property: %s")},
                    {"42", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s")},
                    {" 42", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s")},
                    {"42 ", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s")},
                    {" 42 ", new ExtractSet(
                            List.of(new ExtractPair("^[\\s]*([a-z]+)[\\s]*$", 1)),
                            "Invalid property: %s")}
            });
        }

        private final String value;
        private final ExtractSet extractSet;

        public ExtractPropertyWithExceptionTest(String value, ExtractSet extractSet) {
            this.value = value;
            this.extractSet = extractSet;
        }

        @Test(expected = IllegalArgumentException.class)
        public void extractProperty() {
            ContentfulUtils.extractProperty(value, extractSet);
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractTwitterTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, null},
                    {"", ""},
                    {" ", ""},
                    {"arungupta", "arungupta"},
                    {" arungupta", "arungupta"},
                    {"arungupta ", "arungupta"},
                    {" arungupta ", "arungupta"},
                    {"tagir_valeev", "tagir_valeev"},
                    {"kuksenk0", "kuksenk0"},
                    {"DaschnerS", "DaschnerS"},
                    {"@dougqh", "dougqh"},
                    {"42", "42"},
                    {"@42", "42"}
            });
        }

        private final String value;
        private final String expected;

        public ExtractTwitterTest(String value, String expected) {
            this.value = value;
            this.expected = expected;
        }

        @Test
        public void extractTwitter() {
            assertEquals(expected, ContentfulUtils.extractTwitter(value));
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractTwitterWithExceptionTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"%"},
                    {"%42"},
                    {"%dougqh"},
                    {"dougqh%"},
                    {"dou%gqh"}
            });
        }

        private final String value;

        public ExtractTwitterWithExceptionTest(String value) {
            this.value = value;
        }

        @Test(expected = IllegalArgumentException.class)
        public void extractTwitter() {
            ContentfulUtils.extractTwitter(value);
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractGitHubTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, null},
                    {"", ""},
                    {" ", ""},
                    {"cloudkserg", "cloudkserg"},
                    {" cloudkserg", "cloudkserg"},
                    {"cloudkserg ", "cloudkserg"},
                    {" cloudkserg ", "cloudkserg"},
                    {"pjBooms", "pjBooms"},
                    {"andre487", "andre487"},
                    {"Marina-Miranovich", "Marina-Miranovich"},
                    {"https://github.com/inponomarev", "inponomarev"},
                    {"http://github.com/inponomarev", "inponomarev"},
                    {"https://niquola.github.io/blog/", "niquola"},
                    {"http://niquola.github.io/blog/", "niquola"}
            });
        }

        private final String value;
        private final String expected;

        public ExtractGitHubTest(String value, String expected) {
            this.value = value;
            this.expected = expected;
        }

        @Test
        public void extractGitHub() {
            assertEquals(expected, ContentfulUtils.extractGitHub(value));
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractGitHubWithExceptionTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"%"},
                    {"%42"},
                    {"%dougqh"},
                    {"dougqh%"},
                    {"dou%gqh"}
            });
        }

        private final String value;

        public ExtractGitHubWithExceptionTest(String value) {
            this.value = value;
        }

        @Test(expected = IllegalArgumentException.class)
        public void extractGitHub() {
            ContentfulUtils.extractGitHub(value);
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractLanguageTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, null},
                    {Boolean.TRUE, Language.RUSSIAN.getCode()},
                    {Boolean.FALSE, Language.ENGLISH.getCode()}
            });
        }

        private final Boolean value;
        private final String expected;

        public ExtractLanguageTest(Boolean value, String expected) {
            this.value = value;
            this.expected = expected;
        }

        @Test
        public void extractLanguage() {
            assertEquals(expected, ContentfulUtils.extractLanguage(value));
        }
    }

    @RunWith(Parameterized.class)
    public static class CombineContentfulLinksTest {
        @Parameters
        public static Collection<Object[]> data() {
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

            return Arrays.asList(new Object[][]{
                    {null, null, Collections.emptyList()},
                    {Collections.emptyList(), null, Collections.emptyList()},
                    {List.of(contentfulLink0), null, List.of(contentfulLink0)},
                    {List.of(contentfulLink0, contentfulLink1), null, List.of(contentfulLink0, contentfulLink1)},
                    {List.of(contentfulLink0), contentfulLink1, List.of(contentfulLink0, contentfulLink1)},
                    {List.of(contentfulLink0), contentfulLink0, List.of(contentfulLink0)},
                    {List.of(contentfulLink0, contentfulLink1), contentfulLink2, List.of(contentfulLink0, contentfulLink1, contentfulLink2)},
                    {List.of(contentfulLink0, contentfulLink0), contentfulLink0, List.of(contentfulLink0)}
            });
        }

        private final List<ContentfulLink> presentations;
        private final ContentfulLink presentation;
        private final List<ContentfulLink> expected;

        public CombineContentfulLinksTest(List<ContentfulLink> presentations, ContentfulLink presentation, List<ContentfulLink> expected) {
            this.presentations = presentations;
            this.presentation = presentation;
            this.expected = expected;
        }

        @Test
        public void combineContentfulLinks() {
            assertEquals(expected, ContentfulUtils.combineContentfulLinks(presentations, presentation));
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractVideoLinksTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, Collections.emptyList()},
                    {"value", List.of("value")}
            });
        }

        private final String videoLink;
        private final List<String> expected;

        public ExtractVideoLinksTest(String videoLink, List<String> expected) {
            this.videoLink = videoLink;
            this.expected = expected;
        }

        @Test
        public void extractVideoLinks() {
            assertEquals(expected, ContentfulUtils.extractVideoLinks(videoLink));
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractAssetUrlTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, null},
                    {"", ""},
                    {" ", ""},
                    {"//assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"},
                    {" //assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"},
                    {"//assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf ", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"},
                    {" //assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf ", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"},
                    {"http://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"},
                    {"https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf", "https://assets.ctfassets.net/oxjq45e8ilak/6sKzieda7fGIQrNXZsR0cZ/bf48435803b5cac81cb4e3c729a581d6/2019_Azul_HTM.pdf"}
            });
        }

        private final String value;
        private final String expected;

        public ExtractAssetUrlTest(String value, String expected) {
            this.value = value;
            this.expected = expected;
        }

        @Test
        public void extractAssetUrl() {
            assertEquals(expected, ContentfulUtils.extractAssetUrl(value));
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractAssetUrlWithExceptionTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"abc"},
                    {"42"}
            });
        }

        private final String value;

        public ExtractAssetUrlWithExceptionTest(String value) {
            this.value = value;
        }

        @Test(expected = IllegalArgumentException.class)
        public void extractAssetUrl() {
            ContentfulUtils.extractAssetUrl(value);
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractLocaleItemsTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, null, true, Collections.emptyList()},
                    {null, "", true, Collections.emptyList()},
                    {"", null, true, Collections.emptyList()},
                    {"", "", true, Collections.emptyList()},
                    {"value0", null, true, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))},
                    {"value0", "", true, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))},
                    {"value0", "value0", true, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))},
                    {"value0", "value1", true, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"),
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))},
                    {null, "value1", true, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))},
                    {"", "value1", true, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))},
                    {null, null, false, Collections.emptyList()},
                    {null, "", false, Collections.emptyList()},
                    {"", null, false, Collections.emptyList()},
                    {"", "", false, Collections.emptyList()},
                    {"value0", null, false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))},
                    {"value0", "", false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))},
                    {"value0", "value0", false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))},
                    {"value0", "value1", false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"),
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))},
                    {null, "value1", false, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))},
                    {"", "value1", false, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))}
            });
        }

        private final String enText;
        private final String ruText;
        private final boolean checkEnTextExistence;
        private final List<LocaleItem> expected;

        public ExtractLocaleItemsTest(String enText, String ruText, boolean checkEnTextExistence, List<LocaleItem> expected) {
            this.enText = enText;
            this.ruText = ruText;
            this.checkEnTextExistence = checkEnTextExistence;
            this.expected = expected;
        }

        @Test
        public void extractLocaleItems() {
            assertEquals(expected, ContentfulUtils.extractLocaleItems(enText, ruText, checkEnTextExistence));
            assertEquals(expected, ContentfulUtils.extractLocaleItems(enText, ruText));
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractEventNameTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, null, null},
                    {null, "", null},
                    {null, "abc", null},
                    {"abc", "en", "abc"},
                    {"Moscow", "en", " Msc"},
                    {"Moscow ", "en", " Msc"},
                    {" Moscow ", "en", " Msc"},
                    {"abc Moscow", "en", "abc Msc"},
                    {"abc Moscow ", "en", "abc Msc"},
                    {"Moscow cde", "en", "Moscow cde"},
                    {" Moscow cde", "en", " Moscow cde"},
                    {"abc Moscow cde", "en", "abc Moscow cde"},
                    {"Piter", "en", " SPb"},
                    {"Piter ", "en", " SPb"},
                    {" Piter ", "en", " SPb"},
                    {"abc Piter", "en", "abc SPb"},
                    {"abc Piter ", "en", "abc SPb"},
                    {"Piter cde", "en", "Piter cde"},
                    {" Piter cde", "en", " Piter cde"},
                    {"abc Piter cde", "en", "abc Piter cde"},
                    {"Moscow", "ru-RU", " Мск"},
                    {"Moscow ", "ru-RU", " Мск"},
                    {" Moscow ", "ru-RU", " Мск"},
                    {"abc Moscow", "ru-RU", "abc Мск"},
                    {"abc Moscow ", "ru-RU", "abc Мск"},
                    {"Moscow cde", "ru-RU", "Moscow cde"},
                    {" Moscow cde", "ru-RU", " Moscow cde"},
                    {"abc Moscow cde", "ru-RU", "abc Moscow cde"},
                    {"Piter", "ru-RU", " СПб"},
                    {"Piter ", "ru-RU", " СПб"},
                    {" Piter ", "ru-RU", " СПб"},
                    {"abc Piter", "ru-RU", "abc СПб"},
                    {"abc Piter ", "ru-RU", "abc СПб"},
                    {"Piter cde", "ru-RU", "Piter cde"},
                    {" Piter cde", "ru-RU", " Piter cde"},
                    {"abc Piter cde", "ru-RU", "abc Piter cde"}
            });
        }

        private final String name;
        private final String locale;
        private final String expected;

        public ExtractEventNameTest(String name, String locale, String expected) {
            this.name = name;
            this.locale = locale;
            this.expected = expected;
        }

        @Test
        public void extractEventName() {
            assertEquals(expected, ContentfulUtils.extractEventName(name, locale));
        }
    }

    @RunWith(Parameterized.class)
    public static class ExtractEventNameWithExceptionTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"abc", ""},
                    {"abc", "unknown"}
            });
        }

        private final String name;
        private final String locale;

        public ExtractEventNameWithExceptionTest(String name, String locale) {
            this.name = name;
            this.locale = locale;
        }

        @Test(expected = IllegalArgumentException.class)
        public void extractEventName() {
            ContentfulUtils.extractEventName(name, locale);
        }
    }

    @RunWith(Parameterized.class)
    public static class NeedUpdateEventTypeTest {
        @Parameters
        public static Collection<Object[]> data() {
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

            return Arrays.asList(new Object[][]{
                    {eventType0, eventType0, false},
                    {eventType0, eventType1, true},
                    {eventType0, eventType2, true},
                    {eventType0, eventType3, true},
                    {eventType0, eventType4, true},
                    {eventType0, eventType5, true},
                    {eventType0, eventType6, true},
                    {eventType0, eventType7, true},
                    {eventType0, eventType8, true},
                    {eventType0, eventType9, true},
                    {eventType0, eventType10, true},
                    {eventType0, eventType11, true},
                    {eventType0, eventType12, true}
            });
        }

        private final EventType a;
        private final EventType b;
        private final boolean expected;

        public NeedUpdateEventTypeTest(EventType a, EventType b, boolean expected) {
            this.a = a;
            this.b = b;
            this.expected = expected;
        }

        @Test
        public void needUpdate() {
            assertEquals(expected, ContentfulUtils.needUpdate(a, b));
        }
    }

    @RunWith(Parameterized.class)
    public static class NeedUpdatePlaceTest {
        @Parameters
        public static Collection<Object[]> data() {
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

            return Arrays.asList(new Object[][]{
                    {place0, place0, false},
                    {place0, place1, true},
                    {place0, place2, true},
                    {place0, place3, true},
                    {place0, place4, true}
            });
        }

        private final Place a;
        private final Place b;
        private final boolean expected;

        public NeedUpdatePlaceTest(Place a, Place b, boolean expected) {
            this.a = a;
            this.b = b;
            this.expected = expected;
        }

        @Test
        public void needUpdate() {
            assertEquals(expected, ContentfulUtils.needUpdate(a, b));
        }
    }

    @RunWith(Parameterized.class)
    public static class NeedUpdateSpeakerTest {
        @Parameters
        public static Collection<Object[]> data() {
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

            return Arrays.asList(new Object[][]{
                    {speaker0, speaker0, false},
                    {speaker0, speaker1, true},
                    {speaker0, speaker2, true},
                    {speaker0, speaker3, true},
                    {speaker0, speaker4, true},
                    {speaker0, speaker5, true},
                    {speaker0, speaker6, true},
                    {speaker0, speaker7, true},
                    {speaker0, speaker8, true},
                    {speaker0, speaker9, true},
                    {speaker0, speaker10, true}
            });
        }

        private final Speaker a;
        private final Speaker b;
        private final boolean expected;

        public NeedUpdateSpeakerTest(Speaker a, Speaker b, boolean expected) {
            this.a = a;
            this.b = b;
            this.expected = expected;
        }

        @Test
        public void needUpdate() {
            assertEquals(expected, ContentfulUtils.needUpdate(a, b));
        }
    }

    @RunWith(Parameterized.class)
    public static class NeedUpdateTalkTest {
        @Parameters
        public static Collection<Object[]> data() {
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

            return Arrays.asList(new Object[][]{
                    {talk0, talk0, false},
                    {talk0, talk1, true},
                    {talk0, talk2, true},
                    {talk0, talk3, true},
                    {talk0, talk4, true},
                    {talk0, talk5, true},
                    {talk0, talk6, true},
                    {talk0, talk7, true},
                    {talk0, talk8, true},
                    {talk0, talk9, true},
                    {talk0, talk10, true},
                    {talk0, talk11, true}
            });
        }

        private final Talk a;
        private final Talk b;
        private final boolean expected;

        public NeedUpdateTalkTest(Talk a, Talk b, boolean expected) {
            this.a = a;
            this.b = b;
            this.expected = expected;
        }

        @Test
        public void needUpdate() {
            assertEquals(expected, ContentfulUtils.needUpdate(a, b));
        }
    }

    @RunWith(Parameterized.class)
    public static class NeedUpdateEventTest {
        @Parameters
        public static Collection<Object[]> data() {
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

            return Arrays.asList(new Object[][]{
                    {event0, event0, false},
                    {event0, event1, true},
                    {event0, event2, true},
                    {event0, event3, true},
                    {event0, event4, true},
                    {event0, event5, true},
                    {event0, event6, true},
                    {event0, event7, true},
                    {event0, event8, true}
            });
        }

        private final Event a;
        private final Event b;
        private final boolean expected;

        public NeedUpdateEventTest(Event a, Event b, boolean expected) {
            this.a = a;
            this.b = b;
            this.expected = expected;
        }

        @Test
        public void needUpdate() {
            assertEquals(expected, ContentfulUtils.needUpdate(a, b));
        }
    }

    @RunWith(Parameterized.class)
    public static class EqualsTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, null, true},
                    {null, List.of(""), false},
                    {List.of(""), null, false},
                    {List.of(""), List.of(""), true},
                    {List.of(""), List.of("a"), false},
                    {List.of("a"), List.of(""), false},
                    {List.of("a"), List.of("a"), true},
                    {List.of("a"), List.of("b"), false},
                    {List.of("a", "b"), List.of("a", "b"), true},
                    {List.of("a"), List.of("a", "b"), false},
                    {List.of("a", "b"), List.of("a"), false}
            });
        }

        private final List<String> a;
        private final List<String> b;
        private final boolean expected;

        public EqualsTest(List<String> a, List<String> b, boolean expected) {
            this.a = a;
            this.b = b;
            this.expected = expected;
        }

        @Test
        public void needUpdate() {
            assertEquals(expected, ContentfulUtils.equals(a, b));
        }
    }
}
