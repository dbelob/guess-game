package guess.domain;

/**
 * Start parameters.
 */
public class StartParameters {
    private long questionSetId;
    private int quantity;
    private GuessType guessType;

    public StartParameters(long questionSetId, int quantity, GuessType guessType) {
        this.questionSetId = questionSetId;
        this.quantity = quantity;
        this.guessType = guessType;
    }

    public long getQuestionSetId() {
        return questionSetId;
    }

    public int getQuantity() {
        return quantity;
    }

    public GuessType getGuessType() {
        return guessType;
    }
}
