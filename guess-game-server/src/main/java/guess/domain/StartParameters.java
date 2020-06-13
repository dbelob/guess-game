package guess.domain;

import java.util.List;

/**
 * Start parameters.
 */
public class StartParameters {
    private final List<Long> eventTypeIds;
    private final List<Long> eventIds;
    private final GuessMode guessMode;
    private final int quantity;

    public StartParameters(List<Long> eventTypeIds, List<Long> eventIds, GuessMode guessMode, int quantity) {
        this.eventTypeIds = eventTypeIds;
        this.eventIds = eventIds;
        this.guessMode = guessMode;
        this.quantity = quantity;
    }

    public List<Long> getEventTypeIds() {
        return eventTypeIds;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public int getQuantity() {
        return quantity;
    }

    public GuessMode getGuessMode() {
        return guessMode;
    }
}
