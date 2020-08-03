package guess.util;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

@RunWith(Enclosed.class)
class SearchUtilsTest {
    @RunWith(Parameterized.class)
    public static class TrimAndLowerCaseTest {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, null},
                    {"", ""},
                    {"value", "value"},
                    {" value", "value"},
                    {"value ", "value"},
                    {" value ", "value"},
                    {"VALUE", "value"},
                    {" VALUE", "value"},
                    {"VALUE ", "value"},
                    {" VALUE ", "value"},
                    {"VaLUe", "value"},
                    {" VaLUe", "value"},
                    {"VaLUe ", "value"},
                    {" VaLUe ", "value"}
            });
        }

        private final String value;
        private final String expected;

        public TrimAndLowerCaseTest(String value, String expected) {
            this.value = value;
            this.expected = expected;
        }

        @Test
        public void trimAndLowerCase() {
            assertEquals(expected, SearchUtils.trimAndLowerCase(value));
        }
    }

    @RunWith(Parameterized.class)
    public static class IsStringSetTest {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, false},
                    {"", false},
                    {"value", true}
            });
        }

        private final String value;
        private final boolean expected;

        public IsStringSetTest(String value, boolean expected) {
            this.value = value;
            this.expected = expected;
        }

        @Test
        public void isStringSet() {
            assertEquals(expected, SearchUtils.isStringSet(value));
        }
    }
}
