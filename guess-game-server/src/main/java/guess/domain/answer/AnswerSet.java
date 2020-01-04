package guess.domain.answer;

import java.util.List;

/**
 * Answer set.
 */
public class AnswerSet {
    private final List<Long> correctAnswerIds;
    private final List<Long> yourAnswerIds;
    private final boolean isSuccess;

    public AnswerSet(List<Long> correctAnswerIds, List<Long> yourAnswerIds, boolean isSuccess) {
        this.correctAnswerIds = correctAnswerIds;
        this.yourAnswerIds = yourAnswerIds;
        this.isSuccess = isSuccess;
    }

    public List<Long> getCorrectAnswerIds() {
        return correctAnswerIds;
    }

    public List<Long> getYourAnswerIds() {
        return yourAnswerIds;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
