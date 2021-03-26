package guess.dto.result;

/**
 * Tag cloud answer DTO.
 */
public class TagCloudAnswerDto {
    private final SpeakerPairDto speaker;
    private final byte[] image;

    public TagCloudAnswerDto(SpeakerPairDto speaker, byte[] image) {
        this.speaker = speaker;
        this.image = image;
    }

    public SpeakerPairDto getSpeaker() {
        return speaker;
    }

    public byte[] getImage() {
        return image;
    }
}
