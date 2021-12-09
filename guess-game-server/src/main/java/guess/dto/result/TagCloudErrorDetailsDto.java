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

import java.util.*;

/**
 * Tag cloud details DTO.
 */
public record TagCloudErrorDetailsDto(SpeakerPairDto speaker, byte[] image, List<TagCloudAnswerDto> yourAnswers) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagCloudErrorDetailsDto)) return false;
        TagCloudErrorDetailsDto that = (TagCloudErrorDetailsDto) o;
        return Objects.equals(speaker, that.speaker) && Arrays.equals(image, that.image) && Objects.equals(yourAnswers, that.yourAnswers);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(speaker, yourAnswers);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "TagCloudErrorDetailsDto{" +
                "speaker=" + speaker +
                ", image=" + Arrays.toString(image) +
                ", yourAnswers=" + yourAnswers +
                '}';
    }

    private static TagCloudErrorDetailsDto convertToDto(ErrorDetails errorDetails, GuessMode guessMode, Language language) {
        if (GuessMode.GUESS_TAG_CLOUD_BY_SPEAKER_MODE.equals(guessMode) || GuessMode.GUESS_SPEAKER_BY_TAG_CLOUD_MODE.equals(guessMode)) {
            List<Speaker> speakers = GuessMode.GUESS_TAG_CLOUD_BY_SPEAKER_MODE.equals(guessMode) ?
                    Collections.singletonList(((TagCloudQuestion) errorDetails.question()).getSpeaker()) :
                    errorDetails.availableAnswers().stream()
                            .map(a -> ((SpeakerAnswer) a).getSpeaker())
                            .toList();

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
                    .toList();

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
                .toList();
    }
}
