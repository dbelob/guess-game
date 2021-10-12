package guess.domain.statistics.olap.dimension;

import guess.domain.source.Speaker;

/**
 * Speaker dimension.
 */
public class SpeakerDimension extends Dimension<Speaker> {
    public SpeakerDimension(Object value) {
        super(Speaker.class, value);
    }
}
