package guess.util;

import guess.domain.source.LocaleItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("SearchUtilsTest class tests")
public class SearchUtilsTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("trimAndLowerCase method tests")
    class TrimAndLowerCaseTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null),
                    arguments("", ""),
                    arguments("value", "value"),
                    arguments(" value", "value"),
                    arguments("value ", "value"),
                    arguments(" value ", "value"),
                    arguments("VALUE", "value"),
                    arguments(" VALUE", "value"),
                    arguments("VALUE ", "value"),
                    arguments(" VALUE ", "value"),
                    arguments("VaLUe", "value"),
                    arguments(" VaLUe", "value"),
                    arguments("VaLUe ", "value"),
                    arguments(" VaLUe ", "value")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void trimAndLowerCase(String value, String expected) {
            assertEquals(expected, SearchUtils.trimAndLowerCase(value));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isStringSet method tests")
    class IsStringSetTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, false),
                    arguments("", false),
                    arguments("value", true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void isStringSet(String value, boolean expected) {
            assertEquals(expected, SearchUtils.isStringSet(value));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isSubstringFound method tests (String)")
    class IsSubstringFoundStringTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, false),
                    arguments(null, "", false),
                    arguments(null, "value", false),
                    arguments("", null, false),
                    arguments("", "", false),
                    arguments("", "value", false),
                    arguments("value", null, false),
                    arguments("value", "", false),
                    arguments("value", "value", true),
                    arguments("value", "VALUE", true),
                    arguments("value", "VaLUe", true),
                    arguments("value", "value1", true),
                    arguments("value", "VALUE1", true),
                    arguments("value", "VaLUe1", true),
                    arguments("value", "1value", true),
                    arguments("value", "1VALUE", true),
                    arguments("value", "1VaLUe", true),
                    arguments("value", "1value2", true),
                    arguments("value", "1VALUE2", true),
                    arguments("value", "1VaLUe2", true),
                    arguments("value1", "value2", false)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void isSubstringFound(String trimmedLowerCasedSubstring, String item, boolean expected) {
            assertEquals(expected, SearchUtils.isSubstringFound(trimmedLowerCasedSubstring, item));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isSubstringFound method tests (List<LocaleItem>)")
    class IsSubstringFoundListTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, false),
                    arguments(null, Collections.emptyList(), false),
                    arguments(null, List.of(new LocaleItem("en", "")), false),
                    arguments(null, List.of(new LocaleItem("en", "value")), false),
                    arguments("", null, false),
                    arguments("", Collections.emptyList(), false),
                    arguments("", List.of(new LocaleItem("en", "")), false),
                    arguments("", List.of(new LocaleItem("en", "value")), false),
                    arguments("value", null, false),
                    arguments("value", Collections.emptyList(), false),
                    arguments("value", List.of(new LocaleItem("en", "")), false),
                    arguments("value", List.of(new LocaleItem("en", "value")), true),
                    arguments("value", List.of(new LocaleItem("en", "VALUE")), true),
                    arguments("value", List.of(new LocaleItem("en", "VaLUe")), true),
                    arguments("value", List.of(new LocaleItem("en", "value1")), true),
                    arguments("value", List.of(new LocaleItem("en", "VALUE1")), true),
                    arguments("value", List.of(new LocaleItem("en", "VaLUe1")), true),
                    arguments("value", List.of(new LocaleItem("en", "1value")), true),
                    arguments("value", List.of(new LocaleItem("en", "1VALUE")), true),
                    arguments("value", List.of(new LocaleItem("en", "1VaLUe")), true),
                    arguments("value", List.of(new LocaleItem("en", "1value2")), true),
                    arguments("value", List.of(new LocaleItem("en", "1VALUE2")), true),
                    arguments("value", List.of(new LocaleItem("en", "1VaLUe2")), true),
                    arguments("value1", List.of(new LocaleItem("en", "value2")), false),
                    arguments("value", List.of(
                            new LocaleItem("en", "abc"),
                            new LocaleItem("en", "cde")), false),
                    arguments("value", List.of(
                            new LocaleItem("en", "abc"),
                            new LocaleItem("en", "value")), true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void isSubstringFound(String trimmedLowerCasedSubstring, List<LocaleItem> localeItems, boolean expected) {
            assertEquals(expected, SearchUtils.isSubstringFound(trimmedLowerCasedSubstring, localeItems));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSubStringWithFirstAlphaNumeric method tests")
    class GetSubStringWithFirstAlphaNumericTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null),
                    arguments("Google", "Google"),
                    arguments("10up Inc", "10up Inc"),
                    arguments("1C-Bitrix", "1C-Bitrix"),
                    arguments("2GIS", "2GIS"),
                    arguments("‹div›RIOTS", "div›RIOTS"),
                    arguments("1С-Битрикс", "1С-Битрикс"),
                    arguments("Яндекс", "Яндекс"),
//                    arguments("‹Яндекс", "Яндекс"),
                    arguments("@#$", "@#$")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSubStringWithFirstAlphaNumeric(String value, String expected) {
            assertEquals(expected, SearchUtils.getSubStringWithFirstAlphaNumeric(value));
        }
    }
}
