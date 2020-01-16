package guess.util;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

@RunWith(Enclosed.class)
public class ContentfulUtilsTest {
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

        private String value;
        private String expected;

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

        private String value;
        private String expected;

        public ExtractGitHubTest(String value, String expected) {
            this.value = value;
            this.expected = expected;
        }

        @Test
        public void extractTwitter() {
            assertEquals(expected, ContentfulUtils.extractGitHub(value));
        }
    }
}
