package guess.domain.answer;

import guess.domain.source.Speaker;

/**
 * Answer about speaker.
 */
public class SpeakerAnswer extends Answer {
    private final Speaker speaker;

    public SpeakerAnswer(Speaker speaker) {
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
