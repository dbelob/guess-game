package guess.domain.answer;

import guess.domain.question.Question;

import java.util.List;

/**
 * Error details.
 */
public class ErrorDetails {
    private final Question<?> question;
    private final List<Answer<?>> availableAnswers;
    private final List<Answer<?>> yourAnswers;

    public ErrorDetails(Question<?> question, List<Answer<?>> availableAnswers, List<Answer<?>> yourAnswers) {
        this.question = question;
        this.availableAnswers = availableAnswers;
        this.yourAnswers = yourAnswers;
    }

    public Question<?> getQuestion() {
        return question;
    }

    public List<Answer<?>> getAvailableAnswers() {
        return availableAnswers;
    }

    public List<Answer<?>> getYourAnswers() {
        return yourAnswers;
    }
}
