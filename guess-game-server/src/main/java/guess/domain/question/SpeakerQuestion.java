package guess.domain.question;

import guess.domain.source.Speaker;

/**
 * Question about speaker.
 */
public class SpeakerQuestion extends Question<Speaker> {
    public SpeakerQuestion(Speaker speaker) {
        super(speaker);
    }

    public Speaker getSpeaker() {
        return getEntity();
    }
}
