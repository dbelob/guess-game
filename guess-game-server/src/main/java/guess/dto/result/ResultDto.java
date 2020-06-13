package guess.dto.result;

import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.Result;

import java.util.Collections;
import java.util.List;

/**
 * Result DTO.
 */
public class ResultDto {
    private final long correctAnswers;
    private final long wrongAnswers;
    private final long skippedAnswers;
    private final float correctPercents;
    private final float wrongPercents;
    private final float skippedPercents;
    private final GuessMode guessMode;
    private final List<SpeakerErrorDetailsDto> speakerErrorDetailsList;
    private final List<TalkErrorDetailsDto> talkErrorDetailsList;
    private final List<AccountErrorDetailsDto> accountErrorDetailsList;

    private ResultDto(long correctAnswers, long wrongAnswers, long skippedAnswers, float correctPercents,
                      float wrongPercents, float skippedPercents, GuessMode guessMode,
                      List<SpeakerErrorDetailsDto> errorDetailsList, List<TalkErrorDetailsDto> talkErrorDetailsList,
                      List<AccountErrorDetailsDto> accountErrorDetailsList) {
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.skippedAnswers = skippedAnswers;
        this.correctPercents = correctPercents;
        this.wrongPercents = wrongPercents;
        this.skippedPercents = skippedPercents;
        this.guessMode = guessMode;
        this.speakerErrorDetailsList = errorDetailsList;
        this.talkErrorDetailsList = talkErrorDetailsList;
        this.accountErrorDetailsList = accountErrorDetailsList;
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

    public GuessMode getGuessMode() {
        return guessMode;
    }

    public List<SpeakerErrorDetailsDto> getSpeakerErrorDetailsList() {
        return speakerErrorDetailsList;
    }

    public List<TalkErrorDetailsDto> getTalkErrorDetailsList() {
        return talkErrorDetailsList;
    }

    public List<AccountErrorDetailsDto> getAccountErrorDetailsList() {
        return accountErrorDetailsList;
    }

    public static ResultDto convertToDto(Result result, List<ErrorDetails> errorDetailsList, Language language) {
        List<SpeakerErrorDetailsDto> speakerErrorDetailsList =
                (GuessMode.GUESS_NAME_BY_PHOTO_MODE.equals(result.getGuessMode()) || GuessMode.GUESS_PHOTO_BY_NAME_MODE.equals(result.getGuessMode())) ?
                        SpeakerErrorDetailsDto.convertToDto(
                                errorDetailsList,
                                result.getGuessMode(),
                                language) :
                        Collections.emptyList();
        List<TalkErrorDetailsDto> talkErrorDetailsList =
                (GuessMode.GUESS_TALK_BY_SPEAKER_MODE.equals(result.getGuessMode()) || GuessMode.GUESS_SPEAKER_BY_TALK_MODE.equals(result.getGuessMode())) ?
                        TalkErrorDetailsDto.convertToDto(
                                errorDetailsList,
                                result.getGuessMode(),
                                language) :
                        Collections.emptyList();
        List<AccountErrorDetailsDto> accountErrorDetailsList =
                (GuessMode.GUESS_ACCOUNT_BY_SPEAKER_MODE.equals(result.getGuessMode()) || GuessMode.GUESS_SPEAKER_BY_ACCOUNT_MODE.equals(result.getGuessMode())) ?
                        AccountErrorDetailsDto.convertToDto(
                                errorDetailsList,
                                result.getGuessMode(),
                                language) :
                        Collections.emptyList();

        return new ResultDto(
                result.getCorrectAnswers(),
                result.getWrongAnswers(),
                result.getSkippedAnswers(),
                result.getCorrectPercents(),
                result.getWrongPercents(),
                result.getSkippedPercents(),
                result.getGuessMode(),
                speakerErrorDetailsList,
                talkErrorDetailsList,
                accountErrorDetailsList);
    }
}
