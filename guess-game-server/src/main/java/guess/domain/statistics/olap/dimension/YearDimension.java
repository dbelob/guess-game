package guess.domain.statistics.olap.dimension;

import guess.domain.statistics.olap.DimensionType;

import java.util.Set;

/**
 * Year dimension.
 */
public class YearDimension extends Dimension<Long> {
    public YearDimension(Set<Long> values) {
        super(DimensionType.YEAR, values);
    }
}
