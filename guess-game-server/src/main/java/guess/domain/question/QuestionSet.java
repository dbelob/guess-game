package guess.domain.question;

import guess.domain.source.Event;

import java.util.List;

public class QuestionSet {
    private final Event event;
    private final List<SpeakerQuestion> speakerQuestions;
    private final List<TalkQuestion> talkQuestions;

    public QuestionSet(Event event, List<SpeakerQuestion> speakerQuestions, List<TalkQuestion> talkQuestions) {
        this.event = event;
        this.speakerQuestions = speakerQuestions;
        this.talkQuestions = talkQuestions;
    }

    public Event getEvent() {
        return event;
    }

    public List<SpeakerQuestion> getSpeakerQuestions() {
        return speakerQuestions;
    }

    public List<TalkQuestion> getTalkQuestions() {
        return talkQuestions;
    }
}
