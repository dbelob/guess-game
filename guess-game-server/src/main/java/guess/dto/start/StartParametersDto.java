package guess.dto.start;

import guess.domain.GuessMode;
import guess.domain.StartParameters;

import java.util.List;

/**
 * Start parameters DTO
 */
public class StartParametersDto {
    private List<Long> eventTypeIds;
    private List<Long> eventIds;
    private String guessMode;
    private int quantity;

    public StartParametersDto() {
    }

    private StartParametersDto(List<Long> eventTypeIds, List<Long> eventIds, String guessMode, int quantity) {
        this.eventTypeIds = eventTypeIds;
        this.eventIds = eventIds;
        this.guessMode = guessMode;
        this.quantity = quantity;
    }

    public List<Long> getEventTypeIds() {
        return eventTypeIds;
    }

    public void setEventTypeIds(List<Long> eventTypeIds) {
        this.eventTypeIds = eventTypeIds;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public String getGuessMode() {
        return guessMode;
    }

    public void setGuessMode(String guessMode) {
        this.guessMode = guessMode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static StartParameters convertFromDto(StartParametersDto dto) {
        return new StartParameters(
                dto.getEventTypeIds(),
                dto.getEventIds(),
                GuessMode.valueOf(dto.getGuessMode()),
                dto.getQuantity());
    }
}
