package guess.domain.answer;

import java.util.List;

/**
 * Answer set.
 */
public class AnswerSet {
    private final long questionId;
    private final List<Long> answers;
    private final boolean isSuccess;

    public AnswerSet(long questionId, List<Long> answers, boolean isSuccess) {
        this.questionId = questionId;
        this.answers = answers;
        this.isSuccess = isSuccess;
    }

    public long getQuestionId() {
        return questionId;
    }

    public List<Long> getAnswers() {
        return answers;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
