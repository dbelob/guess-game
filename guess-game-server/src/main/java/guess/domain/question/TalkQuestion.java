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
    private Speaker speaker;            //TODO: delete
    private List<Speaker> speakers;
    private Talk talk;

    public TalkQuestion(List<Speaker> speakers, Talk talk) {
        super(talk.getId());

        this.speaker = speakers.get(0); //TODO: delete
        this.speakers = speakers;
        this.talk = talk;
    }

    //TODO: delete
    public Speaker getSpeaker() {
        return speaker;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public Talk getTalk() {
        return talk;
    }

    @Override
    public boolean isSimilar(Question question) {
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
                    shuffledSpeakers,
                    talk);
        } else {
            // Dont't change question
            return this;
        }
    }
}
