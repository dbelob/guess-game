package guess.domain.answer;

import guess.domain.question.Question;

import java.util.List;

/**
 * Error details.
 */
public class ErrorDetails {
    private Question question;
    private List<Question> wrongAnswers;

    public ErrorDetails(Question question, List<Question> wrongAnswers) {
        this.question = question;
        this.wrongAnswers = wrongAnswers;
    }

    public Question getQuestion() {
        return question;
    }

    public List<Question> getWrongAnswers() {
        return wrongAnswers;
    }
}
