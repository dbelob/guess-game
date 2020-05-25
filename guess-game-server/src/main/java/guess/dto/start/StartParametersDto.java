package guess.dto.start;

import guess.domain.GuessMode;
import guess.domain.StartParameters;

import java.util.List;

/**
 * Start parameters DTO
 */
public class StartParametersDto {
    private List<Long> questionSetIds;
    private int quantity;
    private String guessMode;

    public StartParametersDto() {
    }

    private StartParametersDto(List<Long> questionSetIds, int quantity, String guessMode) {
        this.questionSetIds = questionSetIds;
        this.quantity = quantity;
        this.guessMode = guessMode;
    }

    public List<Long> getQuestionSetIds() {
        return questionSetIds;
    }

    public void setQuestionSetIds(List<Long> questionSetIds) {
        this.questionSetIds = questionSetIds;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getGuessMode() {
        return guessMode;
    }

    public void setGuessMode(String guessMode) {
        this.guessMode = guessMode;
    }

    public static StartParameters convertFromDto(StartParametersDto dto) {
        return new StartParameters(
                dto.getQuestionSetIds(),
                dto.getQuantity(),
                GuessMode.valueOf(dto.getGuessMode()));
    }
}
