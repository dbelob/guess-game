package guess.domain.statistics.olap.dimension;

import guess.domain.statistics.olap.DimensionType;

/**
 * Dimension.
 */
public abstract class Dimension<T> {
    private final DimensionType dimensionType;
    private final T value;

    protected Dimension(DimensionType dimensionType, T value) {
        this.dimensionType = dimensionType;
        this.value = value;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public T getValue() {
        return value;
    }
}
