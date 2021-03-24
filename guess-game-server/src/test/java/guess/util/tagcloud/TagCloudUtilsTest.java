package guess.util.tagcloud;

import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.Talk;
import guess.util.LocalizationUtils;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("TagCloudUtils class tests")
class TagCloudUtilsTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getTalkText method tests")
    class GetTalkTextTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));

            Talk talk1 = new Talk();
            talk1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));
            talk1.setShortDescription(List.of(new LocaleItem(Language.ENGLISH.getCode(), "ShortDescription1")));

            Talk talk2 = new Talk();
            talk2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name2")));
            talk2.setLongDescription(List.of(new LocaleItem(Language.ENGLISH.getCode(), "LongDescription2")));

            Talk talk3 = new Talk();
            talk3.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name3")));
            talk3.setShortDescription(List.of(new LocaleItem(Language.ENGLISH.getCode(), "ShortDescription3")));
            talk3.setLongDescription(List.of(new LocaleItem(Language.ENGLISH.getCode(), "LongDescription3")));

            return Stream.of(
                    arguments(talk0, null, "Name0\n"),
                    arguments(talk1, null, "Name1\nShortDescription1\n"),
                    arguments(talk2, null, "Name2\nLongDescription2\n"),
                    arguments(talk3, null, "Name3\nShortDescription3\nLongDescription3\n")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getTalkText(Talk talk, Language language, String expected) {
            new MockUp<LocalizationUtils>() {
                @Mock
                String getString(List<LocaleItem> localeItems, Language language) {
                    if ((localeItems != null) && !localeItems.isEmpty()) {
                        return localeItems.get(0).getText();
                    } else {
                        return "";
                    }
                }
            };

            assertEquals(expected, TagCloudUtils.getTalkText(talk, language));
        }
    }

    @Test
    void getTalkText() {
        new MockUp<TagCloudUtils>() {
            @Mock
            List<String> getTalkText(Invocation invocation, Talk talk) {
                return invocation.proceed(talk);
            }

            @Mock
            String getTalkText(Talk talk, Language language) {
                return "";
            }
        };

        assertDoesNotThrow(() -> TagCloudUtils.getTalkText(new Talk()));
    }
}
