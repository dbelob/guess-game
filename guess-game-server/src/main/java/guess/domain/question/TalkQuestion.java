package guess.domain.question;

import guess.domain.source.Speaker;
import guess.domain.source.Talk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Question about talk.
 */
public class TalkQuestion extends Question {
    private List<Speaker> speakers;
    private Talk talk;

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
    public Question transform() {
        if (talk.getSpeakers().size() > 1) {
            List<Speaker> shuffledSpeakers = new ArrayList<>(talk.getSpeakers());
            Collections.shuffle(shuffledSpeakers);

            return new TalkQuestion(
                    shuffledSpeakers,
                    talk);
        } else {
            // Dont't change question
            return this;
        }
    }
}
