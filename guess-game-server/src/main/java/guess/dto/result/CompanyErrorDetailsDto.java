package guess.dto.result;

import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.answer.CompanyAnswer;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.CompanyBySpeakerQuestion;
import guess.domain.question.QuestionAnswersSet;
import guess.domain.question.SpeakerByCompanyQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Company error details DTO.
 */
public class CompanyErrorDetailsDto {
    private final List<SpeakerPairDto> speakers;
    private final String companyName;
    private final List<String> companyNames;
    private final List<SpeakerPairDto> yourAnswers;

    private CompanyErrorDetailsDto(List<SpeakerPairDto> speakers, String companyName, List<String> companyNames,
                                   List<SpeakerPairDto> yourAnswers) {
        this.speakers = speakers;
        this.companyName = companyName;
        this.companyNames = companyNames;
        this.yourAnswers = yourAnswers;
    }

    public List<SpeakerPairDto> getSpeakers() {
        return speakers;
    }

    public String getCompanyName() {
        return companyName;
    }

    public List<String> getCompanyNames() {
        return companyNames;
    }

    public List<SpeakerPairDto> getYourAnswers() {
        return yourAnswers;
    }

    private static CompanyErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessMode guessMode, Language language) {
        if (GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode) || GuessMode.GUESS_SPEAKER_BY_COMPANY_MODE.equals(guessMode)) {
            List<Speaker> speakers = GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode) ?
                    Collections.singletonList(((CompanyBySpeakerQuestion) errorDetails.getQuestion()).getSpeaker()) :
                    errorDetails.getAvailableAnswers().stream()
                            .map(a -> ((SpeakerAnswer) a).getSpeaker())
                            .collect(Collectors.toList());

            Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                    speakers,
                    s -> LocalizationUtils.getString(s.getName(), language),
                    s -> true);

            List<Speaker> questionSpeakers = GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode) ?
                    Collections.singletonList(((CompanyBySpeakerQuestion) errorDetails.getQuestion()).getSpeaker()) :
                    ((SpeakerByCompanyQuestion) errorDetails.getQuestion()).getSpeakers();

            if (GuessMode.GUESS_SPEAKER_BY_COMPANY_MODE.equals(guessMode)) {
                // Correct answers size must be < QUESTION_ANSWERS_LIST_SIZE
                questionSpeakers = questionSpeakers.subList(
                        0,
                        Math.min(QuestionAnswersSet.QUESTION_ANSWERS_LIST_SIZE - 1, questionSpeakers.size()));
            }

            List<SpeakerPairDto> questionSpeakerPairs = questionSpeakers.stream()
                    .map(s -> new SpeakerPairDto(
                            LocalizationUtils.getSpeakerName(s, language, speakerDuplicates),
                            s.getPhotoFileName()))
                    .collect(Collectors.toList());

            String companyName = GuessMode.GUESS_SPEAKER_BY_COMPANY_MODE.equals(guessMode) ?
                    LocalizationUtils.getString(((SpeakerByCompanyQuestion) errorDetails.getQuestion()).getCompany().getName(), language) :
                    null;

            List<String> companyNames = GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode) ?
                    ((CompanyBySpeakerQuestion) errorDetails.getQuestion()).getCompanies().stream()
                            .map(c -> LocalizationUtils.getString(c.getName(), language))
                            .collect(Collectors.toList()) :
                    Collections.emptyList();

            List<SpeakerPairDto> yourAnswers = errorDetails.getYourAnswers().stream()
                    .map(a -> GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode) ?
                            new SpeakerPairDto(
                                    LocalizationUtils.getString(((CompanyAnswer) a).getCompany().getName(), language),
                                    null) :
                            new SpeakerPairDto(
                                    LocalizationUtils.getSpeakerName(((SpeakerAnswer) a).getSpeaker(), language, speakerDuplicates),
                                    ((SpeakerAnswer) a).getSpeaker().getPhotoFileName()))
                    .collect(Collectors.toList());

            return new CompanyErrorDetailsDto(
                    questionSpeakerPairs,
                    companyName,
                    companyNames,
                    yourAnswers);
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess mode: %s", guessMode));
        }
    }

    public static List<CompanyErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, GuessMode guessMode, Language language) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, guessMode, language))
                .collect(Collectors.toList());
    }
}
