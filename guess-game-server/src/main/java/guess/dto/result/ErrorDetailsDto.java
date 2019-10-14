package guess.dto.result;

import guess.domain.AnswerPair;
import guess.domain.ErrorDetails;
import guess.domain.GuessType;
import guess.domain.question.SpeakerQuestion;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Error details DTO.
 */
public class ErrorDetailsDto {
    private String fileName;
    private String name;
    private List<String> wrongAnswers;

    private String speakerFileName;
    private String speakerName;
    private String talkName;
    private List<AnswerPair> talkWrongAnswers;

    public ErrorDetailsDto(String fileName, String name, List<String> wrongAnswers,
                           String speakerFileName, String speakerName, String talkName, List<AnswerPair> talkWrongAnswers) {
        this.fileName = fileName;
        this.name = name;
        this.wrongAnswers = wrongAnswers;
        this.speakerFileName = speakerFileName;
        this.speakerName = speakerName;
        this.talkName = talkName;
        this.talkWrongAnswers = talkWrongAnswers;
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

    private static ErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessType guessType) {
        if (GuessType.GUESS_NAME_TYPE.equals(guessType) || GuessType.GUESS_PICTURE_TYPE.equals(guessType)) {
            List<String> wrongAnswers = errorDetails.getWrongAnswers().stream()
                    .map(q -> (GuessType.GUESS_NAME_TYPE.equals(guessType)) ? ((SpeakerQuestion) q).getName() : ((SpeakerQuestion) q).getFileName())
                    .collect(Collectors.toList());

            return new ErrorDetailsDto(
                    ((SpeakerQuestion) errorDetails.getQuestion()).getFileName(),
                    ((SpeakerQuestion) errorDetails.getQuestion()).getName(),
                    wrongAnswers,
                    null,
                    null,
                    null,
                    null);
        } else {
            //TODO: implement
            return new ErrorDetailsDto(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        }
    }

    public static List<ErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, GuessType guessType) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, guessType))
                .collect(Collectors.toList());
    }
}
