package guess.dto.guess;

import guess.domain.Language;
import guess.domain.question.QuestionAnswers2;

import java.util.List;

/**
 * DTO function.
 */
@FunctionalInterface
public interface DtoFunction<T> {
    T apply(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
            QuestionAnswers2 questionAnswers2, List<Long> wrongAnswerIds, Language language);
}
