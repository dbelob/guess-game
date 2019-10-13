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
    private Speaker speaker;
    private Talk talk;

    public TalkQuestion(Speaker speaker, Talk talk) {
        super(talk.getId());

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

    @Override
    public boolean isSame(Question question) {
        TalkQuestion talkQuestion = (TalkQuestion) question;

        for (Speaker questionTalkSpeaker : talkQuestion.getTalk().getSpeakers()) {
            for (Speaker speaker : talk.getSpeakers()) {
                if (questionTalkSpeaker.getId() == speaker.getId()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Question transform() {
        if (talk.getSpeakers().size() > 1) {
            List<Speaker> shuffledSpeakers = new ArrayList<>(talk.getSpeakers());
            Collections.shuffle(shuffledSpeakers);

            return new TalkQuestion(
                    shuffledSpeakers.get(0),
                    talk);
        } else {
            // Dont't change question
            return this;
        }
    }
}
