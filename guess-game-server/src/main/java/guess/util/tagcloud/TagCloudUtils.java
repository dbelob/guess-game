package guess.util.tagcloud;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import guess.domain.Language;
import guess.domain.source.Talk;
import guess.util.LocalizationUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tag cloud utility methods.
 */
public class TagCloudUtils {
    private static final Logger log = LoggerFactory.getLogger(TagCloudUtils.class);

    private static final String STOP_WORDS_FILENAME = "stop-words.txt";
    private static final int DEFAULT_CREATION_TALK_WORD_FREQUENCIES_TO_RETURN = 300;
    private static final int DEFAULT_MERGE_TALK_WORD_FREQUENCIES_TO_RETURN = 20;
    private static final int TALK_IMAGE_WIDTH = 250;
    private static final int TALK_IMAGE_HEIGHT = 250;
    private static final int MIN_FONT = 10;
    private static final int MAX_FONT = 40;

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
        frequencyAnalyzer.setWordFrequenciesToReturn(DEFAULT_CREATION_TALK_WORD_FREQUENCIES_TO_RETURN);
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
                .limit(DEFAULT_MERGE_TALK_WORD_FREQUENCIES_TO_RETURN)
                .collect(Collectors.toList());
    }

    /**
     * Creates image.
     *
     * @param wordFrequencies word frequencies
     * @return image
     */
    public static byte[] createImage(List<WordFrequency> wordFrequencies) throws IOException {
        final Dimension dimension = new Dimension(TALK_IMAGE_WIDTH, TALK_IMAGE_HEIGHT);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);

        // Create tag cloud
        wordCloud.setBackgroundColor(new Color(0xFFFFFF, false));
        wordCloud.setPadding(0);
        wordCloud.setBackground(new RectangleBackground(dimension));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0x000000)));
        wordCloud.setFontScalar(new LinearFontScalar(MIN_FONT, MAX_FONT));
        wordCloud.build(wordFrequencies);

        // Change image type from TYPE_INT_ARGB to TYPE_INT_RGB
        BufferedImage oldImage = wordCloud.getBufferedImage();
        BufferedImage newImage = new BufferedImage(oldImage.getWidth(), oldImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        newImage.createGraphics().drawImage(oldImage, 0, 0, Color.WHITE, null);

        // Create image array
        byte[] result;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(newImage, "jpg", bos);

            result = bos.toByteArray();
        }

        return result;
    }

    /**
     * Creates language, image map.
     *
     * @param languageWordFrequenciesMap language, word frequencies map
     * @return language, image map
     */
    public static Map<Language, byte[]> createLanguageImageMap(Map<Language, List<WordFrequency>> languageWordFrequenciesMap) {
        return languageWordFrequenciesMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            try {
                                return TagCloudUtils.createImage(e.getValue());
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                ));
    }

    /**
     * Gets image for language.
     *
     * @param languageImageMap language, image map
     * @param language         language
     * @return image
     */
    public static byte[] getImage(Map<Language, byte[]> languageImageMap, Language language) {
        if (languageImageMap.containsKey(language)) {
            return languageImageMap.get(language);
        } else if (languageImageMap.containsKey(Language.ENGLISH)) {
            return languageImageMap.get(Language.ENGLISH);
        } else if (!languageImageMap.isEmpty()) {
            Map.Entry<Language, byte[]> entry = languageImageMap.entrySet().iterator().next();

            return entry.getValue();
        } else {
            return new byte[]{};
        }
    }

    /**
     * Loads stop words.
     *
     * @return stop words
     */
    private static Set<String> loadStopWords() {
        try {
            final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(STOP_WORDS_FILENAME);
            final List<String> lines = IOUtils.readLines(Objects.requireNonNull(inputStream));

            return new HashSet<>(lines);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptySet();
    }
}
