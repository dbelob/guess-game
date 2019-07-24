package guess.domain;

/**
 * Error details.
 */
public class ErrorDetails {
    private Question question;
    private long wrongAnswers;

    public ErrorDetails(Question question, long wrongAnswers) {
        this.question = question;
        this.wrongAnswers = wrongAnswers;
    }

    public Question getQuestion() {
        return question;
    }

    public long getWrongAnswers() {
        return wrongAnswers;
    }
}
