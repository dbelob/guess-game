package guess.domain.question;

import com.kennycason.kumo.WordFrequency;
import guess.domain.Language;
import guess.domain.QuestionAnswer;
import guess.domain.source.Speaker;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/**
 * Question about tag cloud.
 */
public class TagCloudQuestion extends QuestionAnswer<Speaker> implements Question {
    private final Map<Language, List<WordFrequency>> languageWordFrequenciesMap;
    private Map<Language, BufferedImage> languageImageMap;

    public TagCloudQuestion(Map<Language, List<WordFrequency>> languageWordFrequenciesMap, Speaker speaker) {
        super(speaker);

        this.languageWordFrequenciesMap = languageWordFrequenciesMap;
    }

    public Map<Language, List<WordFrequency>> getLanguageWordFrequenciesMap() {
        return languageWordFrequenciesMap;
    }

    public Map<Language, BufferedImage> getLanguageImageMap() {
        return languageImageMap;
    }

    public void setLanguageImageMap(Map<Language, BufferedImage> languageImageMap) {
        this.languageImageMap = languageImageMap;
    }

    public Speaker getSpeaker() {
        return getEntity();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
