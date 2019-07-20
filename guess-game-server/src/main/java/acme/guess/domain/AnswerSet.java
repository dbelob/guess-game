package acme.guess.domain;

/**
 * Answer set.
 */
public class AnswerSet {
    private long questionId;
    private long answerId;

    public AnswerSet(long questionId, long answerId) {
        this.questionId = questionId;
        this.answerId = answerId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public long getAnswerId() {
        return answerId;
    }
}
