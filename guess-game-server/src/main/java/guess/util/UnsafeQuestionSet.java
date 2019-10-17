package guess.util;

import java.util.Collections;
import java.util.List;

//TODO: delete
public class UnsafeQuestionSet {
    private long id;
    private String name;
    private String logoFileName;
    private List<UnsafeSpeakerQuestion> speakerQuestions = Collections.emptyList();

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

    public List<UnsafeSpeakerQuestion> getSpeakerQuestions() {
        return speakerQuestions;
    }

    public void setSpeakerQuestions(List<UnsafeSpeakerQuestion> speakerQuestions) {
        this.speakerQuestions = speakerQuestions;
    }
}