package guess.domain.question;

import guess.domain.Quadruple;
import guess.domain.answer.Answer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Question and answers.
 */
public record QuestionAnswers(Question question, List<Answer> correctAnswers,
                              Quadruple<Answer> availableAnswers) implements Serializable {
    public List<Answer> getAvailableAnswersAsList() {
        return Arrays.asList(
                availableAnswers.first(),
                availableAnswers.second(),
                availableAnswers.third(),
                availableAnswers.fourth());
    }
}
