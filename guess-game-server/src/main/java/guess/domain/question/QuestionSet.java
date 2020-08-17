package guess.domain.question;

import guess.domain.source.Event;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionSet that = (QuestionSet) o;
        return Objects.equals(event, that.event) &&
                Objects.equals(speakerQuestions, that.speakerQuestions) &&
                Objects.equals(talkQuestions, that.talkQuestions) &&
                Objects.equals(accountQuestions, that.accountQuestions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, speakerQuestions, talkQuestions, accountQuestions);
    }

    @Override
    public String toString() {
        return "QuestionSet{" +
                "event=" + event +
                ", speakerQuestions=" + speakerQuestions +
                ", talkQuestions=" + talkQuestions +
                ", accountQuestions=" + accountQuestions +
                '}';
    }
}
