package guess.dto;

import guess.domain.GuessType;
import guess.domain.StartParameters;

/**
 * Start parameters DTO
 */
public class StartParametersDto {
    private static final String GUESS_NAME = "guessName";

    private long questionSetId;
    private int quantity;
    private String guessType;

    public StartParametersDto() {
    }

    private StartParametersDto(long questionSetId, int quantity, String guessType) {
        this.questionSetId = questionSetId;
        this.quantity = quantity;
        this.guessType = guessType;
    }

    public long getQuestionSetId() {
        return questionSetId;
    }

    public void setQuestionSetId(long questionSetId) {
        this.questionSetId = questionSetId;
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
                dto.getQuestionSetId(),
                dto.getQuantity(),
                GUESS_NAME.equals(dto.getGuessType()) ? GuessType.GUESS_NAME_TYPE : GuessType.GUESS_PICTURE_TYPE);
    }
}
