package guess.domain.tagcloud;

import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.font.KumoFont;

/**
 * Word frequency with no-argument constructor.
 */
public class WordFrequencyNoArgConstructor extends WordFrequency {
    public WordFrequencyNoArgConstructor() {
        super(null, 0, null);
    }

    public WordFrequencyNoArgConstructor(String word, int frequency) {
        super(word, frequency);
    }

    public WordFrequencyNoArgConstructor(String word, int frequency, KumoFont font) {
        super(word, frequency, font);
    }
}
