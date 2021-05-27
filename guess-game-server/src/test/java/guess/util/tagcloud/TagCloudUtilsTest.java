package guess.util.tagcloud;

import guess.dao.exception.WrapperRuntimeException;
import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.domain.tagcloud.SerializedWordFrequency;
import guess.util.LocalizationUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("TagCloudUtils class tests")
class TagCloudUtilsTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isTalkTextExists method tests")
    class IsTalkTextExistsTest {
        private Stream<Arguments> data() {
            Talk talk0 = new Talk();
            talk0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));

            Talk talk1 = new Talk();
            talk1.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), "Name1")));
            talk1.setShortDescription(List.of(new LocaleItem(Language.ENGLISH.getCode(), "ShortDescription1")));

            Talk talk2 = new Talk();
            talk2.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), "Name2")));
            talk2.setLongDescription(List.of(new LocaleItem(Language.ENGLISH.getCode(), "LongDescription2")));

            Talk talk3 = new Talk();
            talk3.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), "Name3")));
            talk3.setShortDescription(List.of(new LocaleItem(Language.RUSSIAN.getCode(), "ShortDescription3")));
            talk3.setLongDescription(List.of(new LocaleItem(Language.ENGLISH.getCode(), "LongDescription3")));

            Talk talk4 = new Talk();
            talk4.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), "Name4")));

            Talk talk5 = new Talk();
            talk5.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), "Name5")));
            talk5.setShortDescription(List.of(new LocaleItem(Language.RUSSIAN.getCode(), "ShortDescription5")));

            Talk talk6 = new Talk();
            talk6.setName(List.of(new LocaleItem(Language.RUSSIAN.getCode(), "Name6")));
            talk6.setShortDescription(List.of(new LocaleItem(Language.RUSSIAN.getCode(), "ShortDescription6")));
            talk6.setLongDescription(List.of(new LocaleItem(Language.RUSSIAN.getCode(), "LongDescription6")));

            return Stream.of(
                    arguments(null, null, false),
                    arguments(talk0, Language.ENGLISH, true),
                    arguments(talk1, Language.ENGLISH, true),
                    arguments(talk2, Language.ENGLISH, true),
                    arguments(talk3, Language.ENGLISH, true),
                    arguments(talk4, Language.ENGLISH, false),
                    arguments(talk5, Language.ENGLISH, false),
                    arguments(talk6, Language.ENGLISH, false)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void isTalkTextExists(Talk talk, Language language, boolean expected) {
            assertEquals(expected, TagCloudUtils.isTalkTextExists(talk, language));
        }
    }

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
        @SuppressWarnings("unchecked")
        void getTalkText(Talk talk, Language language, String expected) {
            try (MockedStatic<LocalizationUtils> mockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
                mockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.nullable(Language.class)))
                        .thenAnswer(
                                (Answer<String>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    List<LocaleItem> localeItems = (List<LocaleItem>) args[0];

                                    if ((localeItems != null) && !localeItems.isEmpty()) {
                                        return localeItems.get(0).getText();
                                    } else {
                                        return "";
                                    }
                                }
                        );

                assertEquals(expected, TagCloudUtils.getTalkText(talk, language));
            }
        }
    }

    @Test
    void getTalkText() {
        try (MockedStatic<TagCloudUtils> mockedStatic = Mockito.mockStatic(TagCloudUtils.class)) {
            mockedStatic.when(() -> TagCloudUtils.getTalkText(Mockito.any(Talk.class)))
                    .thenCallRealMethod();
            mockedStatic.when(() -> TagCloudUtils.getTalkText(Mockito.any(Talk.class), Mockito.any(Language.class)))
                    .thenReturn("");

            assertDoesNotThrow(() -> TagCloudUtils.getTalkText(new Talk()));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakerStopWords method tests")
    class GetSpeakerStopWordsTest {
        private Stream<Arguments> data() {
            final String FIRST_NAME0 = "First0";
            final String LAST_NAME0 = "Last0";
            final String FIRST_NAME1 = "First1";
            final String LAST_NAME1 = "Last1";

            Speaker speaker0 = new Speaker();
            speaker0.setName(List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), String.format("%s %s", FIRST_NAME0, LAST_NAME0))
            ));

            Speaker speaker1 = new Speaker();
            speaker1.setName(List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), String.format("%s %s", FIRST_NAME0, LAST_NAME0)),
                    new LocaleItem(Language.RUSSIAN.getCode(), String.format("%s %s", FIRST_NAME1, LAST_NAME1))
            ));

            Speaker speaker2 = new Speaker();
            speaker2.setName(List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), String.format("%s %s", FIRST_NAME0, LAST_NAME0)),
                    new LocaleItem(Language.RUSSIAN.getCode(), "")
            ));

            return Stream.of(
                    arguments(speaker0, List.of(FIRST_NAME0.toLowerCase(), LAST_NAME0.toLowerCase())),
                    arguments(speaker1, List.of(FIRST_NAME0.toLowerCase(), LAST_NAME0.toLowerCase(),
                            FIRST_NAME1.toLowerCase(), LAST_NAME1.toLowerCase())),
                    arguments(speaker2, List.of(FIRST_NAME0.toLowerCase(), LAST_NAME0.toLowerCase()))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakerStopWords(Speaker speaker, List<String> expected) {
            List<String> actual = TagCloudUtils.getSpeakerStopWords(speaker);

            assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("loadStopWords method tests")
    class LoadStopWordsTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(true, List.of("first", "second", "third")),
                    arguments(false, List.of("first", "second", "third"))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void loadStopWords(boolean withoutException, List<String> lines) {
            try (MockedStatic<IOUtils> mockedStatic = Mockito.mockStatic(IOUtils.class)) {
                mockedStatic.when(() -> IOUtils.readLines(Mockito.any(InputStream.class)))
                        .thenAnswer(
                                (Answer<List<String>>) invocation -> {
                                    if (withoutException) {
                                        return lines;
                                    } else {
                                        throw new IOException("Exception");
                                    }
                                }
                        );

                Set<String> actual = TagCloudUtils.loadStopWords();

                if (withoutException) {
                    assertFalse(actual.isEmpty());
                } else {
                    assertTrue(actual.isEmpty());
                }
            }
        }
    }

    @Test
    void getWordFrequenciesByText() {
        try (MockedStatic<TagCloudUtils> mockedStatic = Mockito.mockStatic(TagCloudUtils.class)) {
            mockedStatic.when(() -> TagCloudUtils.getWordFrequenciesByText(Mockito.anyString(), Mockito.anyList()))
                    .thenCallRealMethod();
            mockedStatic.when(TagCloudUtils::loadStopWords)
                    .thenReturn(Collections.emptySet());

            assertDoesNotThrow(() -> TagCloudUtils.getWordFrequenciesByText("line0\nline1\nline2", Collections.emptyList()));
        }
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
        try (MockedStatic<TagCloudUtils> mockedStatic = Mockito.mockStatic(TagCloudUtils.class)) {
            mockedStatic.when(() -> TagCloudUtils.mergeWordFrequenciesMaps(Mockito.anyList()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> TagCloudUtils.mergeWordFrequencies(Mockito.anyList()))
                    .thenReturn(Collections.emptyList());

            SerializedWordFrequency wordFrequency0 = new SerializedWordFrequency("first", 42);
            SerializedWordFrequency wordFrequency1 = new SerializedWordFrequency("second", 41);
            SerializedWordFrequency wordFrequency2 = new SerializedWordFrequency("third", 40);
            Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap0 = Map.of(Language.ENGLISH, List.of(wordFrequency0, wordFrequency1));
            Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap1 = Map.of(Language.ENGLISH, List.of(wordFrequency2));
            List<Map<Language, List<SerializedWordFrequency>>> languageWordFrequenciesMapList = List.of(languageWordFrequenciesMap0, languageWordFrequenciesMap1);

            assertDoesNotThrow(() -> TagCloudUtils.mergeWordFrequenciesMaps(languageWordFrequenciesMapList));
        }
    }

    @Test
    void createImage() {
        SerializedWordFrequency wordFrequency0 = new SerializedWordFrequency("first", 42);
        SerializedWordFrequency wordFrequency1 = new SerializedWordFrequency("second", 41);
        SerializedWordFrequency wordFrequency2 = new SerializedWordFrequency("third", 40);
        List<SerializedWordFrequency> wordFrequencies = List.of(wordFrequency0, wordFrequency1, wordFrequency2);

        assertDoesNotThrow(() -> TagCloudUtils.createImage(wordFrequencies));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("createLanguageImageMap method tests")
    class CreateLanguageImageMapTest {
        private Stream<Arguments> data() {
            SerializedWordFrequency wordFrequency0 = new SerializedWordFrequency("first", 42);
            SerializedWordFrequency wordFrequency1 = new SerializedWordFrequency("second", 41);
            SerializedWordFrequency wordFrequency2 = new SerializedWordFrequency("third", 40);
            SerializedWordFrequency wordFrequency3 = new SerializedWordFrequency("fourth", 39);
            SerializedWordFrequency wordFrequency4 = new SerializedWordFrequency("fifth", 38);
            List<SerializedWordFrequency> wordFrequencies0 = List.of(wordFrequency0, wordFrequency1, wordFrequency2);
            List<SerializedWordFrequency> wordFrequencies1 = List.of(wordFrequency3, wordFrequency4);
            Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap = Map.of(
                    Language.ENGLISH, wordFrequencies0,
                    Language.RUSSIAN, wordFrequencies1);

            return Stream.of(
                    arguments(languageWordFrequenciesMap, true),
                    arguments(languageWordFrequenciesMap, false)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void createLanguageImageMap(Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap, boolean withoutException) {
            try (MockedStatic<TagCloudUtils> mockedStatic = Mockito.mockStatic(TagCloudUtils.class)) {
                mockedStatic.when(() -> TagCloudUtils.createLanguageImageMap(Mockito.anyMap()))
                        .thenCallRealMethod();
                mockedStatic.when(() -> TagCloudUtils.createImage(Mockito.anyList()))
                        .thenAnswer(
                                (Answer<byte[]>) invocation -> {
                                    if (withoutException) {
                                        return new byte[]{};
                                    } else {
                                        throw new IOException("Exception");
                                    }
                                }
                        );

                if (withoutException) {
                    assertDoesNotThrow(() -> TagCloudUtils.createLanguageImageMap(languageWordFrequenciesMap));
                } else {
                    assertThrows(WrapperRuntimeException.class, () -> TagCloudUtils.createLanguageImageMap(languageWordFrequenciesMap));
                }
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getImage method tests")
    class GetImageTest {
        private Stream<Arguments> data() {
            final byte[] IMAGE0 = new byte[]{0x00, 0x01, 0x02};
            final byte[] IMAGE1 = new byte[]{0x03, 0x04, 0x05};
            final byte[] IMAGE2 = new byte[]{};
            Map<Language, byte[]> languageImageMap0 = Map.of(
                    Language.ENGLISH, IMAGE0,
                    Language.RUSSIAN, IMAGE1);
            Map<Language, byte[]> languageImageMap1 = Map.of(
                    Language.ENGLISH, IMAGE0);
            Map<Language, byte[]> languageImageMap2 = Map.of(
                    Language.RUSSIAN, IMAGE1);
            Map<Language, byte[]> languageImageMap3 = Collections.emptyMap();

            return Stream.of(
                    arguments(languageImageMap0, Language.ENGLISH, IMAGE0),
                    arguments(languageImageMap0, Language.RUSSIAN, IMAGE1),
                    arguments(languageImageMap1, Language.RUSSIAN, IMAGE0),
                    arguments(languageImageMap2, Language.ENGLISH, IMAGE1),
                    arguments(languageImageMap3, Language.ENGLISH, IMAGE2)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getImage(Map<Language, byte[]> languageImageMap, Language language, byte[] expected) {
            assertTrue(Arrays.equals(expected, TagCloudUtils.getImage(languageImageMap, language)));
        }
    }
}
