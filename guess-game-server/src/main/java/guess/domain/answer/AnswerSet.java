package guess.domain.answer;

import java.io.Serializable;
import java.util.List;

/**
 * Answer set.
 */
public class AnswerSet implements Serializable {
    private final List<Long> correctAnswerIds;
    private final List<Long> yourAnswerIds;
    private boolean isSuccess;

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

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
