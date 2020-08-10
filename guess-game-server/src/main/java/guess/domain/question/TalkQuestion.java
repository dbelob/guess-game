package guess.domain.question;

import guess.domain.source.Speaker;
import guess.domain.source.Talk;

import java.util.List;

/**
 * Question about talk.
 */
public class TalkQuestion extends Question {
    private final List<Speaker> speakers;
    private final Talk talk;

    public TalkQuestion(List<Speaker> speakers, Talk talk) {
        super(talk.getId());

        this.speakers = speakers;
        this.talk = talk;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public Talk getTalk() {
        return talk;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
