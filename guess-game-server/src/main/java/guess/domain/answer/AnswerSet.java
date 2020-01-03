package guess.domain.answer;

import java.util.List;

/**
 * Answer set.
 */
public class AnswerSet {
    private final long questionId;
    private final List<Long> yourAnswers;
    private final boolean isSuccess;

    public AnswerSet(long questionId, List<Long> yourAnswers, boolean isSuccess) {
        this.questionId = questionId;
        this.yourAnswers = yourAnswers;
        this.isSuccess = isSuccess;
    }

    public long getQuestionId() {
        return questionId;
    }

    public List<Long> getYourAnswers() {
        return yourAnswers;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
