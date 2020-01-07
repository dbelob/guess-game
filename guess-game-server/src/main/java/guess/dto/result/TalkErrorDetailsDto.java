package guess.dto.result;

import guess.domain.GuessType;
import guess.domain.Language;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.answer.TalkAnswer;
import guess.domain.question.QuestionAnswersSet;
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
    private List<SpeakerPair> speakers;
    private String talkName;
    private List<SpeakerPair> yourAnswers;

    private TalkErrorDetailsDto(List<SpeakerPair> speakers, String talkName, List<SpeakerPair> yourAnswers) {
        this.speakers = speakers;
        this.talkName = talkName;
        this.yourAnswers = yourAnswers;
    }

    public List<SpeakerPair> getSpeakers() {
        return speakers;
    }

    public String getTalkName() {
        return talkName;
    }

    public List<SpeakerPair> getYourAnswers() {
        return yourAnswers;
    }

    private static TalkErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessType guessType, Language language) {
        List<Speaker> speakers = GuessType.GUESS_TALK_TYPE.equals(guessType) ?
                errorDetails.getAvailableAnswers().stream()
                        .map(a -> ((TalkAnswer) a).getTalk().getSpeakers())
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList()) :
                errorDetails.getAvailableAnswers().stream()
                        .map(a -> ((SpeakerAnswer) a).getSpeaker())
                        .collect(Collectors.toList());

        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers,
                language,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);

        if (GuessType.GUESS_TALK_TYPE.equals(guessType) || GuessType.GUESS_SPEAKER_TYPE.equals(guessType)) {
            List<Speaker> questionSpeakers = ((TalkQuestion) errorDetails.getQuestion()).getSpeakers();

            if (GuessType.GUESS_SPEAKER_TYPE.equals(guessType)) {
                // Correct answers size must be < QUESTION_ANSWERS_LIST_SIZE
                questionSpeakers = questionSpeakers.subList(
                        0,
                        Math.min(QuestionAnswersSet.QUESTION_ANSWERS_LIST_SIZE - 1, questionSpeakers.size()));
            }

            List<SpeakerPair> questionSpeakerPairs = questionSpeakers.stream()
                    .map(s -> new SpeakerPair(
                            LocalizationUtils.getSpeakerName(s, language, speakerDuplicates),
                            s.getFileName()))
                    .collect(Collectors.toList());

            List<SpeakerPair> yourAnswers = errorDetails.getYourAnswers().stream()
                    .map(a -> GuessType.GUESS_TALK_TYPE.equals(guessType) ?
                            new SpeakerPair(
                                    LocalizationUtils.getString(((TalkAnswer) a).getTalk().getName(), language),
                                    null) :
                            new SpeakerPair(
                                    LocalizationUtils.getSpeakerName(((SpeakerAnswer) a).getSpeaker(), language, speakerDuplicates),
                                    ((SpeakerAnswer) a).getSpeaker().getFileName()))
                    .collect(Collectors.toList());

            return new TalkErrorDetailsDto(
                    questionSpeakerPairs,
                    LocalizationUtils.getString(((TalkQuestion) errorDetails.getQuestion()).getTalk().getName(), language),
                    yourAnswers);
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
