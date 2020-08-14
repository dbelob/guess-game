package guess.domain.question;

import guess.domain.QuestionAnswer;
import guess.domain.source.Speaker;

/**
 * Question about speaker.
 */
public class SpeakerQuestion extends QuestionAnswer<Speaker> implements Question {
    public SpeakerQuestion(Speaker speaker) {
        super(speaker);
    }

    public Speaker getSpeaker() {
        return getEntity();
    }
}
