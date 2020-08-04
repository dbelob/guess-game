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
public class ResultDto extends Result {
    private final List<SpeakerErrorDetailsDto> speakerErrorDetailsList;
    private final List<TalkErrorDetailsDto> talkErrorDetailsList;
    private final List<AccountErrorDetailsDto> accountErrorDetailsList;

    private ResultDto(Result result, List<SpeakerErrorDetailsDto> errorDetailsList,
                      List<TalkErrorDetailsDto> talkErrorDetailsList, List<AccountErrorDetailsDto> accountErrorDetailsList) {
        super(result.getCorrectAnswers(), result.getWrongAnswers(), result.getSkippedAnswers(),
                result.getCorrectPercents(), result.getWrongPercents(), result.getSkippedPercents(),
                result.getGuessMode());

        this.speakerErrorDetailsList = errorDetailsList;
        this.talkErrorDetailsList = talkErrorDetailsList;
        this.accountErrorDetailsList = accountErrorDetailsList;
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
                result,
                speakerErrorDetailsList,
                talkErrorDetailsList,
                accountErrorDetailsList);
    }
}
