package guess.util;

import guess.domain.Language;
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
}
