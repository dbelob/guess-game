package guess.util;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.EventType;
import guess.domain.source.LocaleItem;
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
            eventType3.setName(List.of(new LocaleItem("en", "name3")));

            EventType eventType4 = new EventType();
            eventType4.setId(0);
            eventType4.setConference(Conference.JPOINT);
            eventType4.setName(List.of(new LocaleItem("en", "name0")));
            eventType4.setShortDescription(List.of(new LocaleItem("en", "shortDescription4")));

            EventType eventType5 = new EventType();
            eventType5.setId(0);
            eventType5.setConference(Conference.JPOINT);
            eventType5.setName(List.of(new LocaleItem("en", "name0")));
            eventType5.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType5.setLongDescription(List.of(new LocaleItem("en", "longDescription5")));

            EventType eventType6 = new EventType();
            eventType6.setId(0);
            eventType6.setConference(Conference.JPOINT);
            eventType6.setName(List.of(new LocaleItem("en", "name0")));
            eventType6.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType6.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType6.setSiteLink(List.of(new LocaleItem("en", "siteLink5")));

            EventType eventType7 = new EventType();
            eventType7.setId(0);
            eventType7.setConference(Conference.JPOINT);
            eventType7.setName(List.of(new LocaleItem("en", "name0")));
            eventType7.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType7.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType7.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType7.setVkLink("vkLink7");

            EventType eventType8 = new EventType();
            eventType8.setId(0);
            eventType8.setConference(Conference.JPOINT);
            eventType8.setName(List.of(new LocaleItem("en", "name0")));
            eventType8.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType8.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType8.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType8.setVkLink("vkLink0");
            eventType8.setTwitterLink("twitterLink8");

            EventType eventType9 = new EventType();
            eventType9.setId(0);
            eventType9.setConference(Conference.JPOINT);
            eventType9.setName(List.of(new LocaleItem("en", "name0")));
            eventType9.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType9.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType9.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType9.setVkLink("vkLink0");
            eventType9.setTwitterLink("twitterLink0");
            eventType9.setFacebookLink("facebookLink9");

            EventType eventType10 = new EventType();
            eventType10.setId(0);
            eventType10.setConference(Conference.JPOINT);
            eventType10.setName(List.of(new LocaleItem("en", "name0")));
            eventType10.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType10.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType10.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType10.setVkLink("vkLink0");
            eventType10.setTwitterLink("twitterLink0");
            eventType10.setFacebookLink("facebookLink0");
            eventType10.setYoutubeLink("youtubeLink10");

            EventType eventType11 = new EventType();
            eventType11.setId(0);
            eventType11.setConference(Conference.JPOINT);
            eventType11.setName(List.of(new LocaleItem("en", "name0")));
            eventType11.setShortDescription(List.of(new LocaleItem("en", "shortDescription0")));
            eventType11.setLongDescription(List.of(new LocaleItem("en", "longDescription0")));
            eventType11.setSiteLink(List.of(new LocaleItem("en", "siteLink0")));
            eventType11.setVkLink("vkLink0");
            eventType11.setTwitterLink("twitterLink0");
            eventType11.setFacebookLink("facebookLink0");
            eventType11.setYoutubeLink("youtubeLink0");
            eventType11.setTelegramLink("telegramLink11");

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
                    {eventType0, eventType11, true}
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
}
