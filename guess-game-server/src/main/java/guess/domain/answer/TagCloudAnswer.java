package guess.domain.answer;

import com.kennycason.kumo.WordFrequency;
import guess.domain.Language;
import guess.domain.QuestionAnswer;
import guess.domain.source.Speaker;
import guess.util.tagcloud.TagCloudUtils;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TagCloudAnswer extends QuestionAnswer<Speaker> implements Answer {
    private final Map<Language, BufferedImage> languageImageMap;

    public TagCloudAnswer(Speaker speaker, Map<Language, List<WordFrequency>> languageWordFrequenciesMap) {
        super(speaker);

        this.languageImageMap = createLanguageImageMap(languageWordFrequenciesMap);
    }

    public Speaker getSpeaker() {
        return getEntity();
    }

    public Map<Language, BufferedImage> getLanguageImageMap() {
        return languageImageMap;
    }

    Map<Language, BufferedImage> createLanguageImageMap(Map<Language, List<WordFrequency>> languageWordFrequenciesMap) {
        return languageWordFrequenciesMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> TagCloudUtils.createImage(e.getValue())
                ));
    }
}
