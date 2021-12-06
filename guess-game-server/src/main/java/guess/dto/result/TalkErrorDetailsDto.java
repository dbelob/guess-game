package guess.dto.result;

import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.answer.TalkAnswer;
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
    private final List<SpeakerPairDto> speakers;
    private final String talkName;
    private final List<SpeakerPairDto> yourAnswers;

    private TalkErrorDetailsDto(List<SpeakerPairDto> speakers, String talkName, List<SpeakerPairDto> yourAnswers) {
        this.speakers = speakers;
        this.talkName = talkName;
        this.yourAnswers = yourAnswers;
    }

    public List<SpeakerPairDto> getSpeakers() {
        return speakers;
    }

    public String getTalkName() {
        return talkName;
    }

    public List<SpeakerPairDto> getYourAnswers() {
        return yourAnswers;
    }

    private static TalkErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessMode guessMode, Language language) {
        if (GuessMode.GUESS_TALK_BY_SPEAKER_MODE.equals(guessMode) || GuessMode.GUESS_SPEAKER_BY_TALK_MODE.equals(guessMode)) {
            List<Speaker> speakers = GuessMode.GUESS_TALK_BY_SPEAKER_MODE.equals(guessMode) ?
                    errorDetails.availableAnswers().stream()
                            .map(a -> ((TalkAnswer) a).getTalk().getSpeakers())
                            .flatMap(Collection::stream)
                            .distinct()
                            .collect(Collectors.toList()) :
                    errorDetails.availableAnswers().stream()
                            .map(a -> ((SpeakerAnswer) a).getSpeaker())
                            .collect(Collectors.toList());

            Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                    speakers,
                    s -> LocalizationUtils.getString(s.getName(), language),
                    s -> true);

            List<Speaker> questionSpeakers = GuessMode.GUESS_TALK_BY_SPEAKER_MODE.equals(guessMode) ?
                    ((TalkQuestion) errorDetails.question()).getSpeakers() :
                    errorDetails.correctAnswers().stream()
                            .map(a -> ((SpeakerAnswer) a).getSpeaker())
                            .collect(Collectors.toList());

            List<SpeakerPairDto> questionSpeakerPairs = questionSpeakers.stream()
                    .map(s -> new SpeakerPairDto(
                            LocalizationUtils.getSpeakerName(s, language, speakerDuplicates),
                            s.getPhotoFileName()))
                    .collect(Collectors.toList());

            List<SpeakerPairDto> yourAnswers = errorDetails.yourAnswers().stream()
                    .map(a -> GuessMode.GUESS_TALK_BY_SPEAKER_MODE.equals(guessMode) ?
                            new SpeakerPairDto(
                                    LocalizationUtils.getString(((TalkAnswer) a).getTalk().getName(), language),
                                    null) :
                            new SpeakerPairDto(
                                    LocalizationUtils.getSpeakerName(((SpeakerAnswer) a).getSpeaker(), language, speakerDuplicates),
                                    ((SpeakerAnswer) a).getSpeaker().getPhotoFileName()))
                    .collect(Collectors.toList());

            return new TalkErrorDetailsDto(
                    questionSpeakerPairs,
                    LocalizationUtils.getString(((TalkQuestion) errorDetails.question()).getTalk().getName(), language),
                    yourAnswers);
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess mode: %s", guessMode));
        }
    }

    public static List<TalkErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, GuessMode guessMode, Language language) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, guessMode, language))
                .collect(Collectors.toList());
    }
}
