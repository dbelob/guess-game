package guess.dto.result;

import guess.domain.answer.ErrorDetails;
import guess.domain.GuessType;
import guess.domain.question.SpeakerQuestion;

import java.util.List;
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

    private static SpeakerErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessType guessType) {
        if (GuessType.GUESS_NAME_TYPE.equals(guessType) || GuessType.GUESS_PICTURE_TYPE.equals(guessType)) {
            List<String> wrongAnswers = errorDetails.getWrongAnswers().stream()
                    .map(q -> (GuessType.GUESS_NAME_TYPE.equals(guessType)) ? ((SpeakerQuestion) q).getName() : ((SpeakerQuestion) q).getFileName())
                    .collect(Collectors.toList());

            return new SpeakerErrorDetailsDto(
                    ((SpeakerQuestion) errorDetails.getQuestion()).getFileName(),
                    ((SpeakerQuestion) errorDetails.getQuestion()).getName(),
                    wrongAnswers);
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess type: %s", guessType));
        }
    }

    public static List<SpeakerErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, GuessType guessType) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, guessType))
                .collect(Collectors.toList());
    }
}
