package guess.domain.answer;

import guess.domain.question.Question;

import java.util.List;

/**
 * Error details.
 */
public record ErrorDetails(Question question, List<Answer> correctAnswers, List<Answer> availableAnswers,
                           List<Answer> yourAnswers) {
}
