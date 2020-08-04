package guess.util;

import guess.domain.source.LocaleItem;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(Enclosed.class)
public class SearchUtilsTest {
    @RunWith(Parameterized.class)
    public static class TrimAndLowerCaseTest {
        @Parameters
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

    @RunWith(Parameterized.class)
    public static class IsSubstringFoundString {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, null, false},
                    {null, "", false},
                    {null, "value", false},
                    {"", null, false},
                    {"", "", false},
                    {"", "value", false},
                    {"value", null, false},
                    {"value", "", false},
                    {"value", "value", true},
                    {"value", "VALUE", true},
                    {"value", "VaLUe", true},
                    {"value", "value1", true},
                    {"value", "VALUE1", true},
                    {"value", "VaLUe1", true},
                    {"value", "1value", true},
                    {"value", "1VALUE", true},
                    {"value", "1VaLUe", true},
                    {"value", "1value2", true},
                    {"value", "1VALUE2", true},
                    {"value", "1VaLUe2", true},
                    {"value1", "value2", false}
            });
        }

        private final String trimmedLowerCasedSubstring;
        private final String item;
        private final boolean expected;

        public IsSubstringFoundString(String trimmedLowerCasedSubstring, String item, boolean expected) {
            this.trimmedLowerCasedSubstring = trimmedLowerCasedSubstring;
            this.item = item;
            this.expected = expected;
        }

        @Test
        public void isSubstringFound() {
            assertEquals(expected, SearchUtils.isSubstringFound(trimmedLowerCasedSubstring, item));
        }
    }

    @RunWith(Parameterized.class)
    public static class IsSubstringFoundList {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, null, false},
                    {null, Collections.emptyList(), false},
                    {null, List.of(new LocaleItem("en", "")), false},
                    {null, List.of(new LocaleItem("en", "value")), false},
                    {"", null, false},
                    {"", Collections.emptyList(), false},
                    {"", List.of(new LocaleItem("en", "")), false},
                    {"", List.of(new LocaleItem("en", "value")), false},
                    {"value", null, false},
                    {"value", Collections.emptyList(), false},
                    {"value", List.of(new LocaleItem("en", "")), false},
                    {"value", List.of(new LocaleItem("en", "value")), true},
                    {"value", List.of(new LocaleItem("en", "VALUE")), true},
                    {"value", List.of(new LocaleItem("en", "VaLUe")), true},
                    {"value", List.of(new LocaleItem("en", "value1")), true},
                    {"value", List.of(new LocaleItem("en", "VALUE1")), true},
                    {"value", List.of(new LocaleItem("en", "VaLUe1")), true},
                    {"value", List.of(new LocaleItem("en", "1value")), true},
                    {"value", List.of(new LocaleItem("en", "1VALUE")), true},
                    {"value", List.of(new LocaleItem("en", "1VaLUe")), true},
                    {"value", List.of(new LocaleItem("en", "1value2")), true},
                    {"value", List.of(new LocaleItem("en", "1VALUE2")), true},
                    {"value", List.of(new LocaleItem("en", "1VaLUe2")), true},
                    {"value1", List.of(new LocaleItem("en", "value2")), false},
                    {"value", List.of(
                            new LocaleItem("en", "abc"),
                            new LocaleItem("en", "cde")), false},
                    {"value", List.of(
                            new LocaleItem("en", "abc"),
                            new LocaleItem("en", "value")), true}
            });
        }

        private final String trimmedLowerCasedSubstring;
        private final List<LocaleItem> localeItems;
        private final boolean expected;

        public IsSubstringFoundList(String trimmedLowerCasedSubstring, List<LocaleItem> localeItems, boolean expected) {
            this.trimmedLowerCasedSubstring = trimmedLowerCasedSubstring;
            this.localeItems = localeItems;
            this.expected = expected;
        }

        @Test
        public void isSubstringFound() {
            assertEquals(expected, SearchUtils.isSubstringFound(trimmedLowerCasedSubstring, localeItems));
        }
    }
}
