package guess.domain;

import java.util.List;

/**
 * Start parameters.
 */
public class StartParameters {
    private List<Long> questionSetIds;
    private int quantity;
    private GuessType guessType;

    public StartParameters(List<Long> questionSetIds, int quantity, GuessType guessType) {
        this.questionSetIds = questionSetIds;
        this.quantity = quantity;
        this.guessType = guessType;
    }

    public List<Long> getQuestionSetIds() {
        return questionSetIds;
    }

    public int getQuantity() {
        return quantity;
    }

    public GuessType getGuessType() {
        return guessType;
    }
}
