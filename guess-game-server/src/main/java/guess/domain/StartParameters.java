package guess.domain;

import java.util.List;

/**
 * Start parameters.
 */
public class StartParameters {
    private final List<Long> questionSetIds;
    private final int quantity;
    private final GuessMode guessMode;

    public StartParameters(List<Long> questionSetIds, int quantity, GuessMode guessMode) {
        this.questionSetIds = questionSetIds;
        this.quantity = quantity;
        this.guessMode = guessMode;
    }

    public List<Long> getQuestionSetIds() {
        return questionSetIds;
    }

    public int getQuantity() {
        return quantity;
    }

    public GuessMode getGuessMode() {
        return guessMode;
    }
}
