package guess.util.yaml;

import guess.domain.Identifiable;
import guess.domain.Identifier;
import guess.domain.source.Nameable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("PropertyMatcher class tests")
class PropertyMatcherTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("constructor tests")
    class ConstructorTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(Identifiable.class, Collections.emptyList(), null),
                    arguments(Identifier.class, List.of("id"), null),
                    arguments(Nameable.class, List.of("id"), null),
                    arguments(Nameable.class, List.of("id", "name"), null),
                    arguments(Nameable.class, List.of("invalid"), NoSuchFieldException.class),
                    arguments(Nameable.class, List.of("id", "name", "invalid"), NoSuchFieldException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void constructor(Class<?> clazz, List<String> propertyNames, Class<? extends Throwable> expectedType) {
            if (expectedType == null) {
                assertDoesNotThrow(() -> new PropertyMatcher(clazz, propertyNames));
            } else {
                assertThrows(expectedType, () -> new PropertyMatcher(clazz, propertyNames));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getClassFieldNames method tests")
    class GetClassFieldNamesTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(Identifiable.class, Collections.emptySet()),
                    arguments(Identifier.class, Set.of("id")),
                    arguments(Nameable.class, Set.of("id", "name", "shortDescription", "longDescription"))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getClassFieldNames(Class<?> clazz, Set<String> expected) {
            assertEquals(expected, PropertyMatcher.getClassFieldNames(clazz));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkPropertyNamesExistence method tests")
    class CheckPropertyNamesExistenceTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptySet(), null),
                    arguments(Collections.emptyList(), Set.of("field0"), null),
                    arguments(List.of("id"), Set.of("id"), null),
                    arguments(List.of("id"), Set.of("id", "field0"), null),
                    arguments(List.of("id"), Collections.emptySet(), NoSuchFieldException.class),
                    arguments(List.of("id"), Set.of("field0"), NoSuchFieldException.class),
                    arguments(List.of("id"), Set.of("field0", "field1"), NoSuchFieldException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void checkPropertyNamesExistence(List<String> propertyNames, Set<String> classFieldNames, Class<? extends Throwable> expectedType) {
            if (expectedType == null) {
                assertDoesNotThrow(() -> PropertyMatcher.checkPropertyNamesExistence(propertyNames, classFieldNames));
            } else {
                assertThrows(expectedType, () -> PropertyMatcher.checkPropertyNamesExistence(propertyNames, classFieldNames));
            }
        }
    }
}
