package guess.dto.result;

import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Speaker error details DTO.
 */
public class SpeakerErrorDetailsDto {
    private final String fileName;
    private final String name;
    private final List<String> yourAnswers;

    private SpeakerErrorDetailsDto(String fileName, String name, List<String> yourAnswers) {
        this.fileName = fileName;
        this.name = name;
        this.yourAnswers = yourAnswers;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public List<String> getYourAnswers() {
        return yourAnswers;
    }

    private static SpeakerErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessMode guessMode, Language language) {
        List<Speaker> speakers = errorDetails.getAvailableAnswers().stream()
                .map(q -> ((SpeakerAnswer) q).getSpeaker())
                .collect(Collectors.toList());

        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers,
                language,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);

        if (GuessMode.GUESS_NAME_BY_PHOTO_MODE.equals(guessMode) || GuessMode.GUESS_PHOTO_BY_NAME_MODE.equals(guessMode)) {
            List<String> yourAnswers = errorDetails.getYourAnswers().stream()
                    .map(q -> GuessMode.GUESS_NAME_BY_PHOTO_MODE.equals(guessMode) ?
                            LocalizationUtils.getSpeakerName(((SpeakerAnswer) q).getSpeaker(), language, speakerDuplicates) :
                            ((SpeakerAnswer) q).getSpeaker().getFileName())
                    .collect(Collectors.toList());

            return new SpeakerErrorDetailsDto(
                    ((SpeakerQuestion) errorDetails.getQuestion()).getSpeaker().getFileName(),
                    LocalizationUtils.getSpeakerName(((SpeakerQuestion) errorDetails.getQuestion()).getSpeaker(), language, speakerDuplicates),
                    yourAnswers);
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess mode: %s", guessMode));
        }
    }

    public static List<SpeakerErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, GuessMode guessMode, Language language) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, guessMode, language))
                .collect(Collectors.toList());
    }
}
