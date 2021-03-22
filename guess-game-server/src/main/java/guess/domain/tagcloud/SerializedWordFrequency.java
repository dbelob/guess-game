package guess.domain.tagcloud;

import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.font.KumoFont;

import java.io.Serializable;

/**
 * Serialized word frequency.
 */
public class SerializedWordFrequency extends WordFrequency implements Serializable {
    public SerializedWordFrequency(String word, int frequency) {
        super(word, frequency);
    }

    public SerializedWordFrequency(String word, int frequency, KumoFont font) {
        super(word, frequency, font);
    }
}
