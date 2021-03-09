package guess.domain.question;

import com.kennycason.kumo.WordFrequency;
import guess.domain.Language;
import guess.domain.QuestionAnswer;
import guess.domain.source.Speaker;

import java.util.List;
import java.util.Map;

/**
 * Question about tag cloud by speaker.
 */
public class TagCloudBySpeakerQuestion extends QuestionAnswer<Speaker> implements Question {
    private final Map<Language, List<WordFrequency>> languageWordFrequenciesMap;

    public TagCloudBySpeakerQuestion(Map<Language, List<WordFrequency>> languageWordFrequenciesMap, Speaker speaker) {
        super(speaker);

        this.languageWordFrequenciesMap = languageWordFrequenciesMap;
    }

    public Map<Language, List<WordFrequency>> getLanguageWordFrequenciesMap() {
        return languageWordFrequenciesMap;
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
