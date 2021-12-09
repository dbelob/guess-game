package guess.util.tagcloud;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.normalize.CharacterStrippingNormalizer;
import com.kennycason.kumo.palette.ColorPalette;
import guess.dao.exception.WrapperRuntimeException;
import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.domain.tagcloud.SerializedWordFrequency;
import guess.util.LocalizationUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
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
     * Checks talk text existence.
     *
     * @param talk     talk
     * @param language language
     * @return {@code true} if talk text exists, {@code false} otherwise
     */
    public static boolean isTalkTextExists(Talk talk, Language language) {
        if (language == null) {
            return false;
        }

        if (talk.getName().stream()
                .anyMatch(li -> language.getCode().equals(li.getLanguage()))) {
            return true;
        }

        if ((talk.getShortDescription() != null) && talk.getShortDescription().stream()
                .anyMatch(li -> language.getCode().equals(li.getLanguage()))) {
            return true;
        }

        return ((talk.getLongDescription() != null) && talk.getLongDescription().stream()
                .anyMatch(li -> language.getCode().equals(li.getLanguage())));
    }

    /**
     * Gets talk text.
     *
     * @param talk     talk
     * @param language language
     * @return talk text
     */
    public static String getTalkText(Talk talk, Language language) {
        var sb = new StringBuilder();

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
     * Gets speaker stop words.
     *
     * @param speaker speaker
     * @return speaker stop words
     */
    public static List<String> getSpeakerStopWords(Speaker speaker) {
        return speaker.getName().stream()
                .map(LocaleItem::getText)
                .map(name -> name.toLowerCase().split(" "))
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * Loads stop words.
     *
     * @return stop words
     */
    static Set<String> loadStopWords() {
        try {
            final var inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(STOP_WORDS_FILENAME);
            final List<String> lines = IOUtils.readLines(Objects.requireNonNull(inputStream));

            return new HashSet<>(lines);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }

        return Collections.emptySet();
    }

    /**
     * Gets word frequencies by text.
     *
     * @param text text
     * @return word frequencies
     */
    public static List<SerializedWordFrequency> getWordFrequenciesByText(String text, List<String> stopWords) {
        Set<String> fullStopWords = loadStopWords();
        fullStopWords.addAll(stopWords);

        final var frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(DEFAULT_CREATION_TALK_WORD_FREQUENCIES_TO_RETURN);
        frequencyAnalyzer.setStopWords(fullStopWords);
        frequencyAnalyzer.addNormalizer(new CharacterStrippingNormalizer(
                Pattern.compile("^[*]+|" +
                        "[*]+$|" +
                        "^[«`“”\\[{]{1}|" +
                        "[»`“”…®\\]}]{1}$"),
                ""));

        List<String> lines = Arrays.asList(text.split("\n"));

        return frequencyAnalyzer.load(lines).stream()
                .map(wf -> new SerializedWordFrequency(wf.getWord(), wf.getFrequency(), wf.getFont()))
                .toList();
    }

    /**
     * Merges word frequencies.
     *
     * @param wordFrequenciesList word frequencies list
     * @return word frequencies
     */
    public static List<SerializedWordFrequency> mergeWordFrequencies(List<List<SerializedWordFrequency>> wordFrequenciesList) {
        return wordFrequenciesList.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(
                        SerializedWordFrequency::getWord,
                        Collectors.summingInt(SerializedWordFrequency::getFrequency)
                ))
                .entrySet().stream()
                .map(e -> new SerializedWordFrequency(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(SerializedWordFrequency::getFrequency).reversed())
                .limit(DEFAULT_MERGE_TALK_WORD_FREQUENCIES_TO_RETURN)
                .toList();
    }

    /**
     * Merges word frequencies map.
     *
     * @param languageWordFrequenciesMapList source list
     * @return target map
     */
    public static Map<Language, List<SerializedWordFrequency>> mergeWordFrequenciesMaps(List<Map<Language, List<SerializedWordFrequency>>> languageWordFrequenciesMapList) {
        Map<Language, List<List<SerializedWordFrequency>>> languageWordFrequenciesListMap = new EnumMap<>(Language.class);

        for (Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap : languageWordFrequenciesMapList) {
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
     * Creates image.
     *
     * @param wordFrequencies word frequencies
     * @return image
     */
    static byte[] createImage(List<SerializedWordFrequency> wordFrequencies) throws IOException {
        final var dimension = new Dimension(TALK_IMAGE_WIDTH, TALK_IMAGE_HEIGHT);
        final var wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
        final List<WordFrequency> castedWordFrequencies = wordFrequencies.stream()
                .map(WordFrequency.class::cast)
                .toList();

        // Create tag cloud
        wordCloud.setBackgroundColor(new Color(0xFFFFFF, false));
        wordCloud.setPadding(0);
        wordCloud.setBackground(new RectangleBackground(dimension));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0x000000)));
        wordCloud.setFontScalar(new LinearFontScalar(MIN_FONT, MAX_FONT));
        wordCloud.build(new ArrayList<>(castedWordFrequencies));

        // Change image type from TYPE_INT_ARGB to TYPE_INT_RGB
        var oldImage = wordCloud.getBufferedImage();
        var newImage = new BufferedImage(oldImage.getWidth(), oldImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        newImage.createGraphics().drawImage(oldImage, 0, 0, Color.WHITE, null);

        // Create image array
        byte[] result;
        try (var bos = new ByteArrayOutputStream()) {
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
    public static Map<Language, byte[]> createLanguageImageMap(Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap) {
        return languageWordFrequenciesMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            try {
                                return TagCloudUtils.createImage(e.getValue());
                            } catch (IOException ex) {
                                throw new WrapperRuntimeException(ex);
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
}
