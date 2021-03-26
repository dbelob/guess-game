package guess.dto.result;

import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.Result;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Result DTO.
 */
public class ResultDto extends Result {
    private final List<SpeakerErrorDetailsDto> speakerErrorDetailsList;
    private final List<TalkErrorDetailsDto> talkErrorDetailsList;
    private final List<CompanyErrorDetailsDto> companyErrorDetailsList;
    private final List<AccountErrorDetailsDto> accountErrorDetailsList;
    private final List<TagCloudErrorDetailsDto> tagCloudErrorDetailsList;

    private ResultDto(Result result, List<SpeakerErrorDetailsDto> errorDetailsList, List<TalkErrorDetailsDto> talkErrorDetailsList,
                      List<CompanyErrorDetailsDto> companyErrorDetailsList, List<AccountErrorDetailsDto> accountErrorDetailsList,
                      List<TagCloudErrorDetailsDto> tagCloudErrorDetailsList) {
        super(result.getCorrectAnswers(), result.getWrongAnswers(), result.getSkippedAnswers(),
                result.getCorrectPercents(), result.getWrongPercents(), result.getSkippedPercents(),
                result.getGuessMode());

        this.speakerErrorDetailsList = errorDetailsList;
        this.talkErrorDetailsList = talkErrorDetailsList;
        this.companyErrorDetailsList = companyErrorDetailsList;
        this.accountErrorDetailsList = accountErrorDetailsList;
        this.tagCloudErrorDetailsList = tagCloudErrorDetailsList;
    }

    public List<SpeakerErrorDetailsDto> getSpeakerErrorDetailsList() {
        return speakerErrorDetailsList;
    }

    public List<TalkErrorDetailsDto> getTalkErrorDetailsList() {
        return talkErrorDetailsList;
    }

    public List<CompanyErrorDetailsDto> getCompanyErrorDetailsList() {
        return companyErrorDetailsList;
    }

    public List<AccountErrorDetailsDto> getAccountErrorDetailsList() {
        return accountErrorDetailsList;
    }

    public List<TagCloudErrorDetailsDto> getTagCloudErrorDetailsList() {
        return tagCloudErrorDetailsList;
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
        List<CompanyErrorDetailsDto> companyErrorDetailsList =
                (GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(result.getGuessMode()) || GuessMode.GUESS_SPEAKER_BY_COMPANY_MODE.equals(result.getGuessMode())) ?
                        CompanyErrorDetailsDto.convertToDto(
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
        List<TagCloudErrorDetailsDto> tagCloudErrorDetailsList =
                (GuessMode.GUESS_TAG_CLOUD_BY_SPEAKER_MODE.equals(result.getGuessMode()) || GuessMode.GUESS_SPEAKER_BY_TAG_CLOUD_MODE.equals(result.getGuessMode())) ?
                        TagCloudErrorDetailsDto.convertToDto(
                                errorDetailsList,
                                result.getGuessMode(),
                                language) :
                        Collections.emptyList();

        return new ResultDto(
                result,
                speakerErrorDetailsList,
                talkErrorDetailsList,
                companyErrorDetailsList,
                accountErrorDetailsList,
                tagCloudErrorDetailsList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultDto)) return false;
        if (!super.equals(o)) return false;
        ResultDto resultDto = (ResultDto) o;
        return Objects.equals(speakerErrorDetailsList, resultDto.speakerErrorDetailsList) &&
                Objects.equals(talkErrorDetailsList, resultDto.talkErrorDetailsList) &&
                Objects.equals(companyErrorDetailsList, resultDto.companyErrorDetailsList) &&
                Objects.equals(accountErrorDetailsList, resultDto.accountErrorDetailsList) &&
                Objects.equals(tagCloudErrorDetailsList, resultDto.tagCloudErrorDetailsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), speakerErrorDetailsList, talkErrorDetailsList, companyErrorDetailsList,
                accountErrorDetailsList, tagCloudErrorDetailsList);
    }
}
