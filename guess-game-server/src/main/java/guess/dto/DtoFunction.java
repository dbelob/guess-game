package guess.dto;

import guess.domain.QuestionAnswers;

import java.util.List;

/**
 * DTO function.
 */
public interface DtoFunction<T> {
    T apply(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
            QuestionAnswers questionAnswers, List<Long> wrongAnswerIds);
}
