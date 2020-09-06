package guess.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Start parameters.
 */
public class StartParameters implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartParameters that = (StartParameters) o;
        return quantity == that.quantity &&
                Objects.equals(eventTypeIds, that.eventTypeIds) &&
                Objects.equals(eventIds, that.eventIds) &&
                guessMode == that.guessMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventTypeIds, eventIds, guessMode, quantity);
    }
}
