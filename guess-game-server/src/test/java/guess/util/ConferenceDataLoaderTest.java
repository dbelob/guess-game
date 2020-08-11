package guess.util;

import guess.domain.source.LocaleItem;
import guess.domain.source.Talk;
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

@DisplayName("ConferenceDataLoader class tests")
public class ConferenceDataLoaderTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("deleteOpeningAndClosingTalks method tests")
    class DeleteOpeningAndClosingTalksTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setName(List.of(new LocaleItem("en", "Conference opening")));

            Talk talk1 = new Talk();
            talk1.setName(List.of(new LocaleItem("en", "Conference closing")));

            Talk talk2 = new Talk();
            talk2.setName(List.of(new LocaleItem("en", "School opening")));

            Talk talk3 = new Talk();
            talk3.setName(List.of(new LocaleItem("en", "School closing")));

            Talk talk4 = new Talk();
            talk4.setName(List.of(new LocaleItem("en", "name4")));

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyList()),
                    arguments(List.of(talk0), Collections.emptyList()),
                    arguments(List.of(talk1), Collections.emptyList()),
                    arguments(List.of(talk2), Collections.emptyList()),
                    arguments(List.of(talk3), Collections.emptyList()),
                    arguments(List.of(talk0, talk1), Collections.emptyList()),
                    arguments(List.of(talk0, talk1, talk2), Collections.emptyList()),
                    arguments(List.of(talk0, talk1, talk2, talk3), Collections.emptyList()),
                    arguments(List.of(talk4), List.of(talk4)),
                    arguments(List.of(talk0, talk4), List.of(talk4)),
                    arguments(List.of(talk0, talk1, talk4), List.of(talk4)),
                    arguments(List.of(talk0, talk1, talk2, talk4), List.of(talk4)),
                    arguments(List.of(talk0, talk1, talk2, talk3, talk4), List.of(talk4))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void deleteOpeningAndClosingTalks(List<Talk> talks, List<Talk> expected) {
            assertEquals(expected, ConferenceDataLoader.deleteOpeningAndClosingTalks(talks));
        }
    }
}
