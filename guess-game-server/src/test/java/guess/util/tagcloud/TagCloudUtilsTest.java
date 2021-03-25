package guess.util.tagcloud;

import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.Talk;
import guess.domain.tagcloud.SerializedWordFrequency;
import guess.util.LocalizationUtils;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("loadStopWords method tests")
    class LoadStopWordsTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(true),
                    arguments(false)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void loadStopWords(boolean withoutException) {
            new MockUp<IOUtils>() {
                @Mock
                List<String> readLines(InputStream input) throws IOException {
                    if (withoutException) {
                        return Collections.emptyList();
                    } else {
                        throw new IOException("Exception");
                    }
                }
            };

            assertDoesNotThrow(TagCloudUtils::loadStopWords);
        }
    }

    @Test
    void loadStopWords() {
        new MockUp<IOUtils>() {
            @Mock
            List<String> readLines(InputStream input) throws IOException {
                return Collections.emptyList();
            }
        };

        assertDoesNotThrow(TagCloudUtils::loadStopWords);
    }

    @Test
    void getWordFrequenciesByText() {
        new MockUp<TagCloudUtils>() {
            @Mock
            List<SerializedWordFrequency> getWordFrequenciesByText(Invocation invocation, String text, List<String> stopWords) {
                return invocation.proceed(text, stopWords);
            }

            @Mock
            Set<String> loadStopWords() {
                return Collections.emptySet();
            }
        };

        new MockUp<FrequencyAnalyzer>() {
            @Mock
            void setWordFrequenciesToReturn(final int wordFrequenciesToReturn) {
                // Nothing
            }

            @Mock
            void setStopWords(final Collection<String> stopWords) {
                // Nothing
            }

            @Mock
            List<WordFrequency> load(final List<String> texts) {
                return List.of(
                        new WordFrequency("first", 42),
                        new WordFrequency("second", 41),
                        new WordFrequency("third", 40));
            }
        };

        assertDoesNotThrow(() -> TagCloudUtils.getWordFrequenciesByText("line0\nline1\nline2", Collections.emptyList()));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("mergeWordFrequencies method tests")
    class MergeWordFrequenciesTest {
        private Stream<Arguments> data() {
            SerializedWordFrequency wordFrequency0 = new SerializedWordFrequency("first", 10);
            SerializedWordFrequency wordFrequency1 = new SerializedWordFrequency("second", 5);
            SerializedWordFrequency wordFrequency2 = new SerializedWordFrequency("third", 7);
            SerializedWordFrequency wordFrequency3 = new SerializedWordFrequency("first", 14);
            SerializedWordFrequency wordFrequency4 = new SerializedWordFrequency("second", 5);

            return Stream.of(
                    arguments(List.of(
                            List.of(wordFrequency0, wordFrequency1)),
                            List.of(wordFrequency0, wordFrequency1)),
                    arguments(List.of(
                            List.of(wordFrequency0, wordFrequency1),
                            List.of(wordFrequency2)),
                            List.of(wordFrequency0, wordFrequency2, wordFrequency1)),
                    arguments(List.of(
                            List.of(wordFrequency0, wordFrequency1),
                            List.of(wordFrequency2, wordFrequency3, wordFrequency4)),
                            List.of(
                                    new SerializedWordFrequency("first", 24),
                                    new SerializedWordFrequency("second", 10),
                                    new SerializedWordFrequency("third", 7)))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void mergeWordFrequencies(List<List<SerializedWordFrequency>> wordFrequenciesList, List<SerializedWordFrequency> expected) {
            assertEquals(expected, TagCloudUtils.mergeWordFrequencies(wordFrequenciesList));
        }
    }

    @Test
    void mergeWordFrequenciesMaps() {
        new MockUp<TagCloudUtils>() {
            @Mock
            Map<Language, List<SerializedWordFrequency>> mergeWordFrequenciesMaps(
                    Invocation invocation,
                    List<Map<Language, List<SerializedWordFrequency>>> languageWordFrequenciesMapList) {
                return invocation.proceed(languageWordFrequenciesMapList);
            }

            @Mock
            List<SerializedWordFrequency> mergeWordFrequencies(List<List<SerializedWordFrequency>> wordFrequenciesList) {
                return Collections.emptyList();
            }
        };

        SerializedWordFrequency wordFrequency0 = new SerializedWordFrequency("first", 42);
        SerializedWordFrequency wordFrequency1 = new SerializedWordFrequency("second", 41);
        SerializedWordFrequency wordFrequency2 = new SerializedWordFrequency("third", 40);
        Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap0 = Map.of(Language.ENGLISH, List.of(wordFrequency0, wordFrequency1));
        Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap1 = Map.of(Language.ENGLISH, List.of(wordFrequency2));
        List<Map<Language, List<SerializedWordFrequency>>> languageWordFrequenciesMapList = List.of(languageWordFrequenciesMap0, languageWordFrequenciesMap1);

        assertDoesNotThrow(() -> TagCloudUtils.mergeWordFrequenciesMaps(languageWordFrequenciesMapList));
    }
}
