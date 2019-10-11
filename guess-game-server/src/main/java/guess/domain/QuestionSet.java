package guess.domain;

import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;

import java.util.Collections;
import java.util.List;

public class QuestionSet {
    private long id;
    private String name;
    private String directoryName;
    private String logoFileName;
    private List<SpeakerQuestion> speakerQuestions = Collections.emptyList();
    private List<TalkQuestion> talkQuestions = Collections.emptyList();

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

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
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
