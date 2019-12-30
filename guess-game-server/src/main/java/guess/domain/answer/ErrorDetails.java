package guess.domain.answer;

import guess.domain.question.Question;

import java.util.List;

/**
 * Error details.
 */
public class ErrorDetails {
    private final Question question;
    private final List<Answer> availableAnswers;
    private final List<Answer> wrongAnswers;

    public ErrorDetails(Question question, List<Answer> availableAnswers, List<Answer> wrongAnswers) {
        this.question = question;
        this.availableAnswers = availableAnswers;
        this.wrongAnswers = wrongAnswers;
    }

    public Question getQuestion() {
        return question;
    }

    public List<Answer> getAvailableAnswers() {
        return availableAnswers;
    }

    public List<Answer> getWrongAnswers() {
        return wrongAnswers;
    }
}
