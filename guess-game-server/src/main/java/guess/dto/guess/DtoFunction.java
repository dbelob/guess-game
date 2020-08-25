package guess.dto.guess;

import guess.domain.Language;
import guess.domain.question.QuestionAnswers;

/**
 * DTO function.
 */
@FunctionalInterface
public interface DtoFunction<T> {
    T apply(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers, Language language);
}
