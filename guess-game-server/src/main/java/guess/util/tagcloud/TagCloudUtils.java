package guess.util.tagcloud;

import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import guess.domain.Language;
import guess.domain.source.Talk;
import guess.util.LocalizationUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tag cloud utility methods.
 */
public class TagCloudUtils {
    private static final Logger log = LoggerFactory.getLogger(TagCloudUtils.class);

    private static final String STOP_WORDS_FILENAME = "stop-words.txt";
    private static final int DEFAULT_TALK_WORD_FREQUENCIES_TO_RETURN = 600;

    private TagCloudUtils() {
    }

    /**
     * Gets talk text.
     *
     * @param talk     talk
     * @param language language
     * @return talk text
     */
    public static String getTalkText(Talk talk, Language language) {
        StringBuilder sb = new StringBuilder();

        sb.append(LocalizationUtils.getString(talk.getName(), language));
        sb.append("\n");

        if (talk.getShortDescription() != null) {
            sb.append(LocalizationUtils.getString(talk.getShortDescription(), language));
            sb.append("\n");
        }

        if (talk.getLongDescription() != null) {
            sb.append(LocalizationUtils.getString(talk.getLongDescription(), language));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Gets talk text.
     *
     * @param talk talk
     * @return talk text
     */
    public static String getTalkText(Talk talk) {
        return getTalkText(talk, Language.getLanguageByCode(talk.getLanguage()));
    }

    /**
     * Gets word frequencies by text.
     *
     * @param text text
     * @return word frequencies
     */
    public static List<WordFrequency> getWordFrequenciesByText(String text) {
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(DEFAULT_TALK_WORD_FREQUENCIES_TO_RETURN);
        frequencyAnalyzer.setStopWords(loadStopWords());

        List<String> lines = Arrays.asList(text.split("\n"));

        return frequencyAnalyzer.load(lines);
    }

    /**
     * Merges word frequencies map.
     *
     * @param languageWordFrequenciesMapList source list
     * @return target map
     */
    public static Map<Language, List<WordFrequency>> mergeWordFrequenciesMaps(List<Map<Language, List<WordFrequency>>> languageWordFrequenciesMapList) {
        Map<Language, List<List<WordFrequency>>> languageWordFrequenciesListMap = new HashMap<>();

        for (Map<Language, List<WordFrequency>> languageWordFrequenciesMap : languageWordFrequenciesMapList) {
            languageWordFrequenciesMap.forEach((key, value) -> {
                languageWordFrequenciesListMap.computeIfAbsent(key, k -> new ArrayList<>());
                languageWordFrequenciesListMap.get(key).add(value);
            });
        }

        return languageWordFrequenciesListMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> mergeWordFrequencies(e.getValue())
                ));
    }

    /**
     * Merges word frequencies.
     *
     * @param wordFrequenciesList word frequencies list
     * @return word frequencies
     */
    public static List<WordFrequency> mergeWordFrequencies(List<List<WordFrequency>> wordFrequenciesList) {
        return wordFrequenciesList.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(
                        WordFrequency::getWord,
                        Collectors.summingInt(WordFrequency::getFrequency)
                ))
                .entrySet().stream()
                .map(e -> new WordFrequency(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(WordFrequency::getFrequency).reversed())
                .limit(DEFAULT_TALK_WORD_FREQUENCIES_TO_RETURN)
                .collect(Collectors.toList());
    }

    /**
     * Loads stop words.
     *
     * @return stop words
     */
    private static Set<String> loadStopWords() {
        try {
            final List<String> lines = IOUtils.readLines(getInputStream(STOP_WORDS_FILENAME));
            return new HashSet<>(lines);

        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptySet();
    }

    /**
     * Gets input stream by path.
     *
     * @param path path
     * @return input stream
     */
    private static InputStream getInputStream(final String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
}
