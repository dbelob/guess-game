package guess.domain.statistics.olap.dimension;

import guess.domain.statistics.olap.DimensionType;

/**
 * Year dimension.
 */
public class YearDimension extends Dimension<Integer> {
    public YearDimension(Integer value) {
        super(DimensionType.YEAR, value);
    }
}
