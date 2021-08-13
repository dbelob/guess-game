package guess.domain.statistics.olap.dimension;

import guess.domain.statistics.olap.DimensionType;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dimension)) return false;
        Dimension<?> dimension = (Dimension<?>) o;
        return getDimensionType() == dimension.getDimensionType() && getValue().equals(dimension.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDimensionType(), getValue());
    }
}
