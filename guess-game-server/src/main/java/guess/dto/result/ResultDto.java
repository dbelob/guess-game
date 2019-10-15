package guess.dto.result;

import guess.domain.ErrorDetails;
import guess.domain.GuessType;
import guess.domain.Result;

import java.util.Collections;
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
    private List<SpeakerErrorDetailsDto> speakerErrorDetailsList;
    private List<TalkErrorDetailsDto> talkErrorDetailsList;

    private ResultDto(long correctAnswers, long wrongAnswers, long skippedAnswers,
                      float correctPercents, float wrongPercents, float skippedPercents,
                      GuessType guessType, List<SpeakerErrorDetailsDto> errorDetailsList,
                      List<TalkErrorDetailsDto> talkErrorDetailsList) {
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.skippedAnswers = skippedAnswers;
        this.correctPercents = correctPercents;
        this.wrongPercents = wrongPercents;
        this.skippedPercents = skippedPercents;
        this.guessType = guessType;
        this.speakerErrorDetailsList = errorDetailsList;
        this.talkErrorDetailsList = talkErrorDetailsList;
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

    public List<SpeakerErrorDetailsDto> getSpeakerErrorDetailsList() {
        return speakerErrorDetailsList;
    }

    public List<TalkErrorDetailsDto> getTalkErrorDetailsList() {
        return talkErrorDetailsList;
    }

    public static ResultDto convertToDto(Result result, List<ErrorDetails> errorDetailsList) {
        List<SpeakerErrorDetailsDto> speakerErrorDetailsList =
                (GuessType.GUESS_NAME_TYPE.equals(result.getGuessType()) || GuessType.GUESS_PICTURE_TYPE.equals(result.getGuessType())) ?
                        SpeakerErrorDetailsDto.convertToDto(
                                errorDetailsList,
                                result.getGuessType()) :
                        Collections.emptyList();
        List<TalkErrorDetailsDto> talkErrorDetailsList =
                (GuessType.GUESS_TALK_TYPE.equals(result.getGuessType()) || GuessType.GUESS_SPEAKER_TYPE.equals(result.getGuessType())) ?
                        TalkErrorDetailsDto.convertToDto(
                                errorDetailsList,
                                result.getGuessType()) :
                        Collections.emptyList();

        return new ResultDto(
                result.getCorrectAnswers(),
                result.getWrongAnswers(),
                result.getSkippedAnswers(),
                result.getCorrectPercents(),
                result.getWrongPercents(),
                result.getSkippedPercents(),
                result.getGuessType(),
                speakerErrorDetailsList,
                talkErrorDetailsList);
    }
}
