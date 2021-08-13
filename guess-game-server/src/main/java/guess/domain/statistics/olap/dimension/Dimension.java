package guess.domain.statistics.olap.dimension;

import guess.domain.statistics.olap.DimensionType;

import java.util.Set;

/**
 * Dimension.
 */
public abstract class Dimension<T> {
    private final DimensionType dimensionType;
    private final Set<T> values;

    protected Dimension(DimensionType dimensionType, Set<T> values) {
        this.dimensionType = dimensionType;
        this.values = values;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public Set<T> getValues() {
        return values;
    }
}
