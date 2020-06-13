package guess.domain.question;

import guess.domain.source.Event;

import java.util.List;

public class QuestionSet {
    private final Event event;
    private final List<SpeakerQuestion> speakerQuestions;
    private final List<TalkQuestion> talkQuestions;
    private final List<SpeakerQuestion> accountQuestions;

    public QuestionSet(Event event, List<SpeakerQuestion> speakerQuestions, List<TalkQuestion> talkQuestions,
                       List<SpeakerQuestion> accountQuestions) {
        this.event = event;
        this.speakerQuestions = speakerQuestions;
        this.talkQuestions = talkQuestions;
        this.accountQuestions = accountQuestions;
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

    public List<SpeakerQuestion> getAccountQuestions() {
        return accountQuestions;
    }
}
