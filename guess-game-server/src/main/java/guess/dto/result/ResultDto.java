package guess.dto.result;

import guess.domain.ErrorDetails;
import guess.domain.GuessType;
import guess.domain.Result;

import java.util.List;

/**
 * Result DTO.
 */
public class ResultDto {
    private long correctAnswers;
    private long wrongAnswers;
    private long skippedAnswers;
    private float correctPercents;
    private float wrongPercents;
    private float skippedPercents;
    private GuessType guessType;
    private List<ErrorDetailsDto> errorDetailsList;

    public ResultDto(long correctAnswers, long wrongAnswers, long skippedAnswers,
                     float correctPercents, float wrongPercents, float skippedPercents,
                     GuessType guessType, List<ErrorDetailsDto> errorDetailsList) {
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.skippedAnswers = skippedAnswers;
        this.correctPercents = correctPercents;
        this.wrongPercents = wrongPercents;
        this.skippedPercents = skippedPercents;
        this.guessType = guessType;
        this.errorDetailsList = errorDetailsList;
    }

    public long getCorrectAnswers() {
        return correctAnswers;
    }

    public long getWrongAnswers() {
        return wrongAnswers;
    }

    public long getSkippedAnswers() {
        return skippedAnswers;
    }

    public float getCorrectPercents() {
        return correctPercents;
    }

    public float getWrongPercents() {
        return wrongPercents;
    }

    public float getSkippedPercents() {
        return skippedPercents;
    }

    public GuessType getGuessType() {
        return guessType;
    }

    public List<ErrorDetailsDto> getErrorDetailsList() {
        return errorDetailsList;
    }

    public static ResultDto convertToDto(Result result, List<ErrorDetails> errorDetailsList) {
        return new ResultDto(
                result.getCorrectAnswers(),
                result.getWrongAnswers(),
                result.getSkippedAnswers(),
                result.getCorrectPercents(),
                result.getWrongPercents(),
                result.getSkippedPercents(),
                result.getGuessType(),
                ErrorDetailsDto.convertToDto(
                        errorDetailsList,
                        result.getGuessType()));
    }
}
