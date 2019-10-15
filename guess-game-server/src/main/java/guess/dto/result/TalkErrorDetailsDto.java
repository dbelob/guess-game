package guess.dto.result;

import guess.domain.ErrorDetails;
import guess.domain.ErrorPair;
import guess.domain.GuessType;
import guess.domain.question.TalkQuestion;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Talk error details DTO.
 */
public class TalkErrorDetailsDto {
    private String speakerFileName;
    private String speakerName;
    private String talkName;
    private List<ErrorPair> wrongAnswers;

    private TalkErrorDetailsDto(String speakerFileName, String speakerName, String talkName, List<ErrorPair> wrongAnswers) {
        this.speakerFileName = speakerFileName;
        this.speakerName = speakerName;
        this.talkName = talkName;
        this.wrongAnswers = wrongAnswers;
    }

    public String getSpeakerFileName() {
        return speakerFileName;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public String getTalkName() {
        return talkName;
    }

    public List<ErrorPair> getWrongAnswers() {
        return wrongAnswers;
    }

    private static TalkErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessType guessType) {
        if (GuessType.GUESS_TALK_TYPE.equals(guessType) || GuessType.GUESS_SPEAKER_TYPE.equals(guessType)) {
            List<ErrorPair> wrongAnswers = errorDetails.getWrongAnswers().stream()
                    .map(q -> (GuessType.GUESS_TALK_TYPE.equals(guessType)) ?
                            new ErrorPair(
                                    LocalizationUtils.getEnglishName(((TalkQuestion) q).getTalk().getName()),
                                    null) :
                            new ErrorPair(
                                    LocalizationUtils.getEnglishName(((TalkQuestion) q).getSpeaker().getName()),
                                    ((TalkQuestion) q).getSpeaker().getFileName()))
                    .collect(Collectors.toList());

            return new TalkErrorDetailsDto(
                    ((TalkQuestion) errorDetails.getQuestion()).getSpeaker().getFileName(),
                    LocalizationUtils.getEnglishName(((TalkQuestion) errorDetails.getQuestion()).getSpeaker().getName()),
                    LocalizationUtils.getEnglishName(((TalkQuestion) errorDetails.getQuestion()).getTalk().getName()),
                    wrongAnswers);
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess type: %s", guessType));
        }
    }

    public static List<TalkErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, GuessType guessType) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, guessType))
                .collect(Collectors.toList());
    }
}
