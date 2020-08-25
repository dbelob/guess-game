package guess.dto.guess;

import java.util.List;

/**
 * Source of question, answers DTO.
 */
public class QuestionAnswersSourceDto {
    private final String questionSetName;
    private final int currentIndex;
    private final int totalNumber;
    private final String logoFileName;

    private final List<Long> correctAnswerIds;
    private final List<Long> yourAnswerIds;

    public QuestionAnswersSourceDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                                    List<Long> correctAnswerIds, List<Long> yourAnswerIds) {
        this.questionSetName = questionSetName;
        this.currentIndex = currentIndex;
        this.totalNumber = totalNumber;
        this.logoFileName = logoFileName;
        this.correctAnswerIds = correctAnswerIds;
        this.yourAnswerIds = yourAnswerIds;
    }

    public String getQuestionSetName() {
        return questionSetName;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public List<Long> getCorrectAnswerIds() {
        return correctAnswerIds;
    }

    public List<Long> getYourAnswerIds() {
        return yourAnswerIds;
    }
}
