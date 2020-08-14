package guess.domain.answer;

import guess.domain.QuestionAnswer;
import guess.domain.source.Talk;

/**
 * Answer about talk.
 */
public class TalkAnswer extends QuestionAnswer<Talk> implements Answer {
    public TalkAnswer(Talk talk) {
        super(talk);
    }

    public Talk getTalk() {
        return getEntity();
    }
}
