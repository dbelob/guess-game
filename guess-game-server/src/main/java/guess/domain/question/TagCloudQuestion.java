package guess.domain.question;

import guess.domain.Language;
import guess.domain.QuestionAnswer;
import guess.domain.source.Speaker;
import guess.domain.tagcloud.SerializedWordFrequency;
import guess.util.tagcloud.TagCloudUtils;

import java.util.List;
import java.util.Map;

/**
 * Question about tag cloud.
 */
public class TagCloudQuestion extends QuestionAnswer<Speaker> implements Question {
    private final Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap;
    private Map<Language, byte[]> languageImageMap;

    public TagCloudQuestion(Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap, Speaker speaker) {
        super(speaker);

        this.languageWordFrequenciesMap = languageWordFrequenciesMap;
    }

    public Map<Language, List<SerializedWordFrequency>> getLanguageWordFrequenciesMap() {
        return languageWordFrequenciesMap;
    }

    public Map<Language, byte[]> getLanguageImageMap() {
        if (languageImageMap == null) {
            languageImageMap = TagCloudUtils.createLanguageImageMap(languageWordFrequenciesMap);
        }

        return languageImageMap;
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
