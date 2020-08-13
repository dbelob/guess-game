package guess.util;

import guess.domain.Identifiable;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.Speaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("QuestionUtils class tests")
public class QuestionUtilsTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("removeDuplicatesById method tests")
    class RemoveDuplicatesByIdTest {
        private Identifiable createIdentifiable(long id) {
            Speaker speaker = new Speaker();
            speaker.setId(id);

            return new SpeakerQuestion(speaker);
        }

        private Stream<Arguments> data() {
            Identifiable identifiable0 = createIdentifiable(0);
            Identifiable identifiable1 = createIdentifiable(1);
            Identifiable identifiable2 = createIdentifiable(2);

            return Stream.of(
                    arguments(null, Collections.emptyList()),
                    arguments(Collections.emptyList(), Collections.emptyList()),
                    arguments(Collections.singletonList(identifiable0), Collections.singletonList(identifiable0)),
                    arguments(Arrays.asList(identifiable0, identifiable0), Collections.singletonList(identifiable0)),
                    arguments(Arrays.asList(identifiable0, identifiable1), Arrays.asList(identifiable0, identifiable1)),
                    arguments(Arrays.asList(identifiable0, identifiable1, identifiable0), Arrays.asList(identifiable0, identifiable1)),
                    arguments(Arrays.asList(identifiable0, identifiable1, identifiable0, identifiable1), Arrays.asList(identifiable0, identifiable1)),
                    arguments(Arrays.asList(identifiable0, identifiable1, identifiable2), Arrays.asList(identifiable0, identifiable1, identifiable2)),
                    arguments(Arrays.asList(identifiable0, identifiable1, identifiable2, identifiable0), Arrays.asList(identifiable0, identifiable1, identifiable2)),
                    arguments(Arrays.asList(identifiable0, identifiable1, identifiable2, identifiable0, identifiable1), Arrays.asList(identifiable0, identifiable1, identifiable2))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        public void removeDuplicatesById(List<? extends Identifiable> items, List<? extends Identifiable> expected) {
            assertEquals(expected, QuestionUtils.removeDuplicatesById(items));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getQuantities method tests")
    class GetQuantitiesTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(0, Collections.emptyList()),
                    arguments(1, Collections.singletonList(1)),
                    arguments(2, Collections.singletonList(2)),
                    arguments(3, Collections.singletonList(3)),
                    arguments(4, Collections.singletonList(4)),
                    arguments(5, Collections.singletonList(5)),
                    arguments(10, Arrays.asList(5, 10)),
                    arguments(11, Arrays.asList(5, 10, 11)),
                    arguments(20, Arrays.asList(5, 10, 20)),
                    arguments(25, Arrays.asList(5, 10, 20, 25)),
                    arguments(50, Arrays.asList(5, 10, 20, 50)),
                    arguments(57, Arrays.asList(5, 10, 20, 50, 57)),
                    arguments(100, Arrays.asList(5, 10, 20, 50, 100)),
                    arguments(105, Arrays.asList(5, 10, 20, 50, 100, 105)),
                    arguments(200, Arrays.asList(5, 10, 20, 50, 100, 200)),
                    arguments(242, Arrays.asList(5, 10, 20, 50, 100, 200, 242)),
                    arguments(300, Arrays.asList(5, 10, 20, 50, 100, 200, 300)),
                    arguments(383, Arrays.asList(5, 10, 20, 50, 100, 200, 300, 383)),
                    arguments(1000, Arrays.asList(5, 10, 20, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000)),
                    arguments(1001, Arrays.asList(5, 10, 20, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1001))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        public void getQuantities(int count, List<Integer> expected) {
            assertEquals(expected, QuestionUtils.getQuantities(count));
        }
    }
}
