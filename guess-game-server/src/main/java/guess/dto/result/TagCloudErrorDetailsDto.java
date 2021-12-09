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

/**
 * Tag cloud details DTO.
 */
public record TagCloudErrorDetailsDto(SpeakerPairDto speaker, byte[] image, List<TagCloudAnswerDto> yourAnswers) {
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
