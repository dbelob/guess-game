package guess.dto;

import guess.domain.GuessType;
import guess.domain.StartParameters;

/**
 * Start parameters DTO
 */
public class StartParametersDto {
    private static final String GUESS_NAME = "guessName";

    private Long[] questionSetIds;
    private int quantity;
    private String guessType;

    public StartParametersDto() {
    }

    private StartParametersDto(Long[] questionSetIds, int quantity, String guessType) {
        this.questionSetIds = questionSetIds;
        this.quantity = quantity;
        this.guessType = guessType;
    }

    public Long[] getQuestionSetIds() {
        return questionSetIds;
    }

    public void setQuestionSetIds(Long[] questionSetIds) {
        this.questionSetIds = questionSetIds;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getGuessType() {
        return guessType;
    }

    public void setGuessType(String guessType) {
        this.guessType = guessType;
    }

    public static StartParameters convertFromDto(StartParametersDto dto) {
        return new StartParameters(
                dto.getQuestionSetIds(),
                dto.getQuantity(),
                GUESS_NAME.equals(dto.getGuessType()) ? GuessType.GUESS_NAME_TYPE : GuessType.GUESS_PICTURE_TYPE);
    }
}
