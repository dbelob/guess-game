package guess.domain.question;

/**
 * Question about speaker.
 */
public class SpeakerQuestion extends Question {
    private String fileName;
    private String name;

    public SpeakerQuestion() {
    }

    public SpeakerQuestion(long id, String fileName, String name) {
        super(id);

        this.fileName = fileName;
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isSame(Question question) {
        return (question.getId() == getId());
    }

    @Override
    public Question transform() {
        // Dont't change question
        return this;
    }
}
