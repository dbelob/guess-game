package guess.domain;

import java.util.Set;

/**
 * Answer set.
 */
public class AnswerSet {
    private long questionId;
    private Set<Long> answers;
    private boolean isSuccess;

    public AnswerSet(long questionId, Set<Long> answers, boolean isSuccess) {
        this.questionId = questionId;
        this.answers = answers;
        this.isSuccess = isSuccess;
    }

    public long getQuestionId() {
        return questionId;
    }

    public Set<Long> getAnswers() {
        return answers;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
