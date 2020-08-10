package guess.domain.question;

import guess.domain.source.Speaker;

/**
 * Question about speaker.
 */
public class SpeakerQuestion extends Question {
    private final Speaker speaker;

    public SpeakerQuestion(Speaker speaker) {
        super(speaker.getId());

        this.speaker = speaker;
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
