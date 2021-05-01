package guess.domain.answer;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var answerSet = (AnswerSet) o;
        return isSuccess == answerSet.isSuccess &&
                Objects.equals(correctAnswerIds, answerSet.correctAnswerIds) &&
                Objects.equals(yourAnswerIds, answerSet.yourAnswerIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(correctAnswerIds, yourAnswerIds, isSuccess);
    }
}
