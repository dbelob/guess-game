package guess.domain;

/**
 * Start parameters.
 */
public class StartParameters {
    private Long[] questionSetIds;
    private int quantity;
    private GuessType guessType;

    public StartParameters(Long[] questionSetIds, int quantity, GuessType guessType) {
        this.questionSetIds = questionSetIds;
        this.quantity = quantity;
        this.guessType = guessType;
    }

    public Long[] getQuestionSetIds() {
        return questionSetIds;
    }

    public int getQuantity() {
        return quantity;
    }

    public GuessType getGuessType() {
        return guessType;
    }
}
