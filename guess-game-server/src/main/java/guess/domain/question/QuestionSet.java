package guess.domain.question;

import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;

import java.util.Collections;
import java.util.List;

public class QuestionSet {
    private long id;
    private String name;
    private String logoFileName;
    private List<SpeakerQuestion> speakerQuestions = Collections.emptyList();
    private List<TalkQuestion> talkQuestions = Collections.emptyList();

    public QuestionSet() {
    }

    public QuestionSet(long id, String name, String logoFileName, List<SpeakerQuestion> speakerQuestions, List<TalkQuestion> talkQuestions) {
        this.id = id;
        this.name = name;
        this.logoFileName = logoFileName;
        this.speakerQuestions = speakerQuestions;
        this.talkQuestions = talkQuestions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public void setLogoFileName(String logoFileName) {
        this.logoFileName = logoFileName;
    }

    public List<SpeakerQuestion> getSpeakerQuestions() {
        return speakerQuestions;
    }

    public void setSpeakerQuestions(List<SpeakerQuestion> speakerQuestions) {
        this.speakerQuestions = speakerQuestions;
    }

    public List<TalkQuestion> getTalkQuestions() {
        return talkQuestions;
    }

    public void setTalkQuestions(List<TalkQuestion> talkQuestions) {
        this.talkQuestions = talkQuestions;
    }
}
