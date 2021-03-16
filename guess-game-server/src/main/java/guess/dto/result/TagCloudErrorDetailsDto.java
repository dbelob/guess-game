package guess.dto.result;

import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.answer.ErrorDetails;

import java.util.List;
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
            //TODO: implement
            return new TagCloudErrorDetailsDto(
                    null,
                    null,
                    null
            );
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
