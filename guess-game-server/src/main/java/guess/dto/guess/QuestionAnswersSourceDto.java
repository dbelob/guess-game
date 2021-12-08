package guess.dto.guess;

import java.util.List;

/**
 * Source of question, answers DTO.
 */
public record QuestionAnswersSourceDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                                       List<Long> correctAnswerIds, List<Long> yourAnswerIds) {
}
