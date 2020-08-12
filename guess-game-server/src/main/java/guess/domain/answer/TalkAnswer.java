package guess.domain.answer;

import guess.domain.source.Talk;

/**
 * Answer about talk.
 */
public class TalkAnswer extends Answer<Talk> {
    public TalkAnswer(Talk talk) {
        super(talk);
    }

    public Talk getTalk() {
        return getEntity();
    }
}
