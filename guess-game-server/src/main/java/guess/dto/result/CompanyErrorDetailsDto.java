package guess.dto.result;

import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.answer.CompanyAnswer;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.CompanyBySpeakerQuestion;
import guess.domain.question.SpeakerByCompanyQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Company error details DTO.
 */
public record CompanyErrorDetailsDto(List<SpeakerPairDto> speakers, List<String> companyNames,
                                     List<SpeakerPairDto> yourAnswers) {
    private static CompanyErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessMode guessMode, Language language) {
        if (GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode) || GuessMode.GUESS_SPEAKER_BY_COMPANY_MODE.equals(guessMode)) {
            List<Speaker> speakers = GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode) ?
                    Collections.singletonList(((CompanyBySpeakerQuestion) errorDetails.question()).getSpeaker()) :
                    errorDetails.availableAnswers().stream()
                            .map(a -> ((SpeakerAnswer) a).getSpeaker())
                            .toList();

            Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                    speakers,
                    s -> LocalizationUtils.getString(s.getName(), language),
                    s -> true);

            List<Speaker> questionSpeakers = GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode) ?
                    Collections.singletonList(((CompanyBySpeakerQuestion) errorDetails.question()).getSpeaker()) :
                    errorDetails.correctAnswers().stream()
                            .map(a -> ((SpeakerAnswer) a).getSpeaker())
                            .toList();

            List<SpeakerPairDto> questionSpeakerPairs = questionSpeakers.stream()
                    .map(s -> new SpeakerPairDto(
                            LocalizationUtils.getSpeakerName(s, language, speakerDuplicates),
                            s.getPhotoFileName()))
                    .toList();

            List<String> companyNames = GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode) ?
                    ((CompanyBySpeakerQuestion) errorDetails.question()).getCompanies().stream()
                            .map(c -> LocalizationUtils.getString(c.getName(), language))
                            .toList() :
                    Collections.singletonList(LocalizationUtils.getString(((SpeakerByCompanyQuestion) errorDetails.question()).getCompany().getName(), language));

            List<SpeakerPairDto> yourAnswers = errorDetails.yourAnswers().stream()
                    .map(a -> GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode) ?
                            new SpeakerPairDto(
                                    LocalizationUtils.getString(((CompanyAnswer) a).getCompany().getName(), language),
                                    null) :
                            new SpeakerPairDto(
                                    LocalizationUtils.getSpeakerName(((SpeakerAnswer) a).getSpeaker(), language, speakerDuplicates),
                                    ((SpeakerAnswer) a).getSpeaker().getPhotoFileName()))
                    .toList();

            return new CompanyErrorDetailsDto(
                    questionSpeakerPairs,
                    companyNames,
                    yourAnswers);
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess mode: %s", guessMode));
        }
    }

    public static List<CompanyErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, GuessMode guessMode, Language language) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, guessMode, language))
                .toList();
    }
}
