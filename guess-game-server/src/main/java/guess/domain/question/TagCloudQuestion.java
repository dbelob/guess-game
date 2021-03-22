package guess.domain.question;

import guess.domain.Language;
import guess.domain.TagCloudQuestionAnswer;
import guess.domain.source.Speaker;
import guess.domain.tagcloud.SerializedWordFrequency;

import java.util.List;
import java.util.Map;

/**
 * Question about tag cloud.
 */
public class TagCloudQuestion extends TagCloudQuestionAnswer implements Question {
    public TagCloudQuestion(Speaker speaker, Map<Language, List<SerializedWordFrequency>> languageWordFrequenciesMap) {
        super(speaker, languageWordFrequenciesMap);
    }
}
