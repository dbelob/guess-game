package guess.dto.result;

import java.util.Arrays;
import java.util.Objects;

/**
 * Tag cloud answer DTO.
 */
public record TagCloudAnswerDto(SpeakerPairDto speaker, byte[] image) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagCloudAnswerDto)) return false;
        TagCloudAnswerDto that = (TagCloudAnswerDto) o;
        return Objects.equals(speaker, that.speaker) && Arrays.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(speaker);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "TagCloudAnswerDto{" +
                "speaker=" + speaker +
                ", image=" + Arrays.toString(image) +
                '}';
    }
}
