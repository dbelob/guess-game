package guess.dto.guess;

import guess.domain.Language;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.QuestionAnswers2;

import java.util.List;

/**
 * DTO function.
 */
@FunctionalInterface
public interface DtoFunction<T, S, U> {
    T apply(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
            QuestionAnswers2<S, U> questionAnswers2, List<Long> wrongAnswerIds, Language language);
}
