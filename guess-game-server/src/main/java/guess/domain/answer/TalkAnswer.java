package guess.domain.answer;

import guess.domain.source.Talk;

/**
 * Answer about talk.
 */
public class TalkAnswer extends Answer {
    private final Talk talk;

    public TalkAnswer(Talk talk) {
        super(talk.getId());

        this.talk = talk;
    }

    public Talk getTalk() {
        return talk;
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
