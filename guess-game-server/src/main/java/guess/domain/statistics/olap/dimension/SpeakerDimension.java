package guess.domain.statistics.olap.dimension;

import guess.domain.source.Speaker;
import guess.domain.statistics.olap.DimensionType;

/**
 * Speaker dimension.
 */
public class SpeakerDimension extends Dimension<Speaker> {
    public SpeakerDimension(Speaker value) {
        super(DimensionType.SPEAKER, value);
    }
}
