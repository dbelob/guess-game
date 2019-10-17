package guess.domain.question;

import guess.domain.source.Speaker;

/**
 * Question about speaker.
 */
public class SpeakerQuestion extends Question {
    private Speaker speaker;

    public SpeakerQuestion(Speaker speaker) {
        super(speaker.getId());

        this.speaker = speaker;
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }

    @Override
    public boolean isSame(Question question) {
        return (question.getId() == getId());
    }

    @Override
    public Question transform() {
        // Dont't change question
        return this;
    }
}
