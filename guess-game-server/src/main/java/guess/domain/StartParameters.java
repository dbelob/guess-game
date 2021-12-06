package guess.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Start parameters.
 */
public record StartParameters(List<Long> eventTypeIds, List<Long> eventIds, GuessMode guessMode,
                              int quantity) implements Serializable {
}
