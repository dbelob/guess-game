package guess.domain.answer;

import guess.domain.Language;
import guess.domain.TagCloudQuestionAnswer;
import guess.domain.source.Speaker;
import guess.domain.tagcloud.SerializedWordFrequency;

import java.util.List;
import java.util.Map;

/**
 * Answer about tag cloud.
 */
public class TagCloudAnswer extends TagCloudQuestionAnswer implements Answer {
    public TagCloudAnswer(Speaker speaker, Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap) {
        super(speaker, languageWordFrequenciesMap);
    }
}
