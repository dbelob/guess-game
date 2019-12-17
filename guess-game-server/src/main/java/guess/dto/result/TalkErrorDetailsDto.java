package guess.dto.result;

import guess.domain.GuessType;
import guess.domain.Language;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.ErrorPair;
import guess.domain.question.TalkQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
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

    private static TalkErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessType guessType, Language language) {
        List<Speaker> speakers = GuessType.GUESS_TALK_TYPE.equals(guessType) ?
                errorDetails.getAllAnswers().stream()
                        .map(q -> ((TalkQuestion) q).getTalk().getSpeakers())
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList()) :
                errorDetails.getAllAnswers().stream()
                        .map(q -> ((TalkQuestion) q).getSpeaker())
                        .collect(Collectors.toList());

        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers,
                language,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);

        if (GuessType.GUESS_TALK_TYPE.equals(guessType) || GuessType.GUESS_SPEAKER_TYPE.equals(guessType)) {
            List<ErrorPair> wrongAnswers = errorDetails.getWrongAnswers().stream()
                    .map(q -> GuessType.GUESS_TALK_TYPE.equals(guessType) ?
                            new ErrorPair(
                                    LocalizationUtils.getString(((TalkQuestion) q).getTalk().getName(), language),
                                    null) :
                            new ErrorPair(
                                    LocalizationUtils.getSpeakerName(((TalkQuestion) q).getSpeaker(), language, speakerDuplicates),
                                    ((TalkQuestion) q).getSpeaker().getFileName()))
                    .collect(Collectors.toList());

            return new TalkErrorDetailsDto(
                    ((TalkQuestion) errorDetails.getQuestion()).getSpeaker().getFileName(),
                    LocalizationUtils.getSpeakerName(((TalkQuestion) errorDetails.getQuestion()).getSpeaker(), language, speakerDuplicates),
                    LocalizationUtils.getString(((TalkQuestion) errorDetails.getQuestion()).getTalk().getName(), language),
                    wrongAnswers);
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess type: %s", guessType));
        }
    }

    public static List<TalkErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, GuessType guessType, Language language) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, guessType, language))
                .collect(Collectors.toList());
    }
}
