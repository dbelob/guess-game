package guess.dto.guess;

import guess.domain.question.QuestionAnswers;

import java.util.List;

/**
 * DTO function.
 */
public interface DtoFunction<T> {
    T apply(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
            QuestionAnswers questionAnswers, List<Long> wrongAnswerIds);
}
