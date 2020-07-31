package guess.util;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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
                    {"@dougqh", "dougqh"}
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
