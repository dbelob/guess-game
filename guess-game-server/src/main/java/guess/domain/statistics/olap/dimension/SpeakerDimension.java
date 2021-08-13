package guess.domain.statistics.olap.dimension;

import guess.domain.source.Speaker;
import guess.domain.statistics.olap.DimensionType;

import java.util.Set;

/**
 * Speaker dimension.
 */
public class SpeakerDimension extends Dimension<Speaker> {
    public SpeakerDimension(Set<Speaker> values) {
        super(DimensionType.SPEAKER, values);
    }
}
