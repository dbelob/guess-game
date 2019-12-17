package guess.domain.answer;

import guess.domain.question.Question;

import java.util.List;

/**
 * Error details.
 */
public class ErrorDetails {
    private final Question question;
    private final List<Question> allAnswers;
    private final List<Question> wrongAnswers;

    public ErrorDetails(Question question, List<Question> allAnswers, List<Question> wrongAnswers) {
        this.question = question;
        this.allAnswers = allAnswers;
        this.wrongAnswers = wrongAnswers;
    }

    public Question getQuestion() {
        return question;
    }

    public List<Question> getAllAnswers() {
        return allAnswers;
    }

    public List<Question> getWrongAnswers() {
        return wrongAnswers;
    }
}
