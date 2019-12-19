package guess.dto.result;

import guess.domain.GuessType;
import guess.domain.Language;
import guess.domain.answer.ErrorDetails;
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
    private String fileName;
    private String name;
    private List<String> wrongAnswers;

    private SpeakerErrorDetailsDto(String fileName, String name, List<String> wrongAnswers) {
        this.fileName = fileName;
        this.name = name;
        this.wrongAnswers = wrongAnswers;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public List<String> getWrongAnswers() {
        return wrongAnswers;
    }

    private static SpeakerErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessType guessType, Language language) {
        List<Speaker> speakers = errorDetails.getAllAnswers().stream()
                .map(q -> ((SpeakerQuestion) q).getSpeaker())
                .collect(Collectors.toList());

        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers,
                language,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);

        if (GuessType.GUESS_NAME_TYPE.equals(guessType) || GuessType.GUESS_PICTURE_TYPE.equals(guessType)) {
            List<String> wrongAnswers = errorDetails.getWrongAnswers().stream()
                    .map(q -> GuessType.GUESS_NAME_TYPE.equals(guessType) ?
                            LocalizationUtils.getSpeakerName(((SpeakerQuestion) q).getSpeaker(), language, speakerDuplicates) :
                            ((SpeakerQuestion) q).getSpeaker().getFileName())
                    .collect(Collectors.toList());

            return new SpeakerErrorDetailsDto(
                    ((SpeakerQuestion) errorDetails.getQuestion()).getSpeaker().getFileName(),
                    LocalizationUtils.getSpeakerName(((SpeakerQuestion) errorDetails.getQuestion()).getSpeaker(), language, speakerDuplicates),
                    wrongAnswers);
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess type: %s", guessType));
        }
    }

    public static List<SpeakerErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, GuessType guessType, Language language) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, guessType, language))
                .collect(Collectors.toList());
    }
}
