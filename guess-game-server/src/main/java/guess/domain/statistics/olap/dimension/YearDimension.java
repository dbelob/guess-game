package guess.domain.statistics.olap.dimension;

import guess.domain.statistics.olap.DimensionType;

/**
 * Year dimension.
 */
public class YearDimension extends Dimension<Long> {
    public YearDimension(Long value) {
        super(DimensionType.YEAR, value);
    }
}
