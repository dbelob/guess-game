package guess.domain.question;

import guess.domain.Quadruple;
import guess.domain.answer.Answer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Question and answers.
 */
public class QuestionAnswers implements Serializable {
    private final Question question;
    private final List<Answer> correctAnswers;
    private final Quadruple<Answer> availableAnswers;

    public QuestionAnswers(Question question, List<Answer> correctAnswers, Quadruple<Answer> availableAnswers) {
        this.question = question;
        this.correctAnswers = correctAnswers;
        this.availableAnswers = availableAnswers;
    }

    public Question getQuestion() {
        return question;
    }

    public List<Answer> getCorrectAnswers() {
        return correctAnswers;
    }

    public Quadruple<Answer> getAvailableAnswers() {
        return availableAnswers;
    }

    public List<Answer> getAvailableAnswersAsList() {
        return Arrays.asList(
                availableAnswers.first(),
                availableAnswers.second(),
                availableAnswers.third(),
                availableAnswers.fourth());
    }
}
