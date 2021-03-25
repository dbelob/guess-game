package guess.domain.tagcloud;

import com.kennycason.kumo.font.KumoFont;

import java.io.Serializable;
import java.util.Objects;

/**
 * Serialized word frequency.
 */
public class SerializedWordFrequency extends WordFrequencyNoArgConstructor implements Serializable {
    public SerializedWordFrequency(String word, int frequency) {
        super(word, frequency);
    }

    public SerializedWordFrequency(String word, int frequency, KumoFont font) {
        super(word, frequency, font);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SerializedWordFrequency)) return false;
        SerializedWordFrequency that = (SerializedWordFrequency) o;
        return getFrequency() == that.getFrequency() && Objects.equals(getWord(), that.getWord()) && Objects.equals(getFont(), that.getFont());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWord(), getFrequency(), getFont());
    }
}
