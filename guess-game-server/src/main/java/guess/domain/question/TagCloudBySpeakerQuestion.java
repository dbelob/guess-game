package guess.domain.question;

import com.kennycason.kumo.WordFrequency;
import guess.domain.QuestionAnswer;
import guess.domain.source.Speaker;

import java.util.List;

/**
 * Question about tag cloud by speaker.
 */
public class TagCloudBySpeakerQuestion extends QuestionAnswer<Speaker> implements Question {
    private final List<WordFrequency> wordFrequencies;

    public TagCloudBySpeakerQuestion(List<WordFrequency> wordFrequencies, Speaker speaker) {
        super(speaker);

        this.wordFrequencies = wordFrequencies;
    }

    public List<WordFrequency> getWordFrequencies() {
        return wordFrequencies;
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
