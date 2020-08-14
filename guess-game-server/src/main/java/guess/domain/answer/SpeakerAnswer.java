package guess.domain.answer;

import guess.domain.QuestionAnswer;
import guess.domain.source.Speaker;

/**
 * Answer about speaker.
 */
public class SpeakerAnswer extends QuestionAnswer<Speaker> implements Answer {
    public SpeakerAnswer(Speaker speaker) {
        super(speaker);
    }

    public Speaker getSpeaker() {
        return getEntity();
    }
}
