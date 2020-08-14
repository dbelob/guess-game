package guess.domain.question;

import guess.domain.QuestionAnswer;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;

import java.util.List;

/**
 * Question about talk.
 */
public class TalkQuestion extends QuestionAnswer<Talk> implements Question {
    private final List<Speaker> speakers;

    public TalkQuestion(List<Speaker> speakers, Talk talk) {
        super(talk);

        this.speakers = speakers;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public Talk getTalk() {
        return getEntity();
    }
}
