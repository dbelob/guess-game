package guess.domain.question;

import guess.domain.source.Speaker;
import guess.domain.source.Talk;

/**
 * Question about talk.
 */
public class TalkQuestion extends Question {
    private Speaker speaker;
    private Talk talk;

    public TalkQuestion(long id, Speaker speaker, Talk talk) {
        super(id);
        this.speaker = speaker;
        this.talk = talk;
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }

    public Talk getTalk() {
        return talk;
    }

    public void setTalk(Talk talk) {
        this.talk = talk;
    }
}
