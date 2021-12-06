package guess.dto.result;

import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.answer.TagCloudAnswer;
import guess.domain.question.TagCloudQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;
import guess.util.tagcloud.TagCloudUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tag cloud details DTO.
 */
public class TagCloudErrorDetailsDto {
    private final SpeakerPairDto speaker;
    private final byte[] image;
    private final List<TagCloudAnswerDto> yourAnswers;

    public TagCloudErrorDetailsDto(SpeakerPairDto speaker, byte[] image, List<TagCloudAnswerDto> yourAnswers) {
        this.speaker = speaker;
        this.image = image;
        this.yourAnswers = yourAnswers;
    }

    public SpeakerPairDto getSpeaker() {
        return speaker;
    }

    public byte[] getImage() {
        return image;
    }

    public List<TagCloudAnswerDto> getYourAnswers() {
        return yourAnswers;
    }

    private static TagCloudErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessMode guessMode, Language language) {
        if (GuessMode.GUESS_TAG_CLOUD_BY_SPEAKER_MODE.equals(guessMode) || GuessMode.GUESS_SPEAKER_BY_TAG_CLOUD_MODE.equals(guessMode)) {
            List<Speaker> speakers = GuessMode.GUESS_TAG_CLOUD_BY_SPEAKER_MODE.equals(guessMode) ?
                    Collections.singletonList(((TagCloudQuestion) errorDetails.question()).getSpeaker()) :
                    errorDetails.availableAnswers().stream()
                            .map(a -> ((SpeakerAnswer) a).getSpeaker())
                            .collect(Collectors.toList());

            Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                    speakers,
                    s -> LocalizationUtils.getString(s.getName(), language),
                    s -> true);

            TagCloudQuestion question = (TagCloudQuestion) errorDetails.question();

            List<TagCloudAnswerDto> yourAnswers = errorDetails.yourAnswers().stream()
                    .map(a -> GuessMode.GUESS_TAG_CLOUD_BY_SPEAKER_MODE.equals(guessMode) ?
                            new TagCloudAnswerDto(
                                    null,
                                    TagCloudUtils.getImage(((TagCloudAnswer) a).getLanguageImageMap(), language)) :
                            new TagCloudAnswerDto(
                                    new SpeakerPairDto(
                                            LocalizationUtils.getSpeakerName(((SpeakerAnswer) a).getSpeaker(), language, speakerDuplicates),
                                            ((SpeakerAnswer) a).getSpeaker().getPhotoFileName()),
                                    null))
                    .collect(Collectors.toList());

            Map<Language, byte[]> languageImageMap = GuessMode.GUESS_TAG_CLOUD_BY_SPEAKER_MODE.equals(guessMode) ?
                    ((TagCloudAnswer) errorDetails.correctAnswers().get(0)).getLanguageImageMap() :
                    question.getLanguageImageMap();

            return new TagCloudErrorDetailsDto(
                    new SpeakerPairDto(
                            LocalizationUtils.getSpeakerName(question.getSpeaker(), language, speakerDuplicates),
                            question.getSpeaker().getPhotoFileName()),
                    TagCloudUtils.getImage(languageImageMap, language),
                    yourAnswers);
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess mode: %s", guessMode));
        }
    }

    public static List<TagCloudErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, GuessMode guessMode, Language language) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, guessMode, language))
                .collect(Collectors.toList());
    }
}
