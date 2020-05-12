package guess.domain.question;

import guess.domain.source.LocaleItem;

import java.util.List;

public class QuestionSet {
    private final long id;
    private final List<LocaleItem> name;
    private final String logoFileName;
    private final List<SpeakerQuestion> speakerQuestions;
    private final List<TalkQuestion> talkQuestions;

    public QuestionSet(long id, List<LocaleItem> name, String logoFileName, List<SpeakerQuestion> speakerQuestions, List<TalkQuestion> talkQuestions) {
        this.id = id;
        this.name = name;
        this.logoFileName = logoFileName;
        this.speakerQuestions = speakerQuestions;
        this.talkQuestions = talkQuestions;
    }

    public long getId() {
        return id;
    }

    public List<LocaleItem> getName() {
        return name;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public List<SpeakerQuestion> getSpeakerQuestions() {
        return speakerQuestions;
    }

    public List<TalkQuestion> getTalkQuestions() {
        return talkQuestions;
    }
}
