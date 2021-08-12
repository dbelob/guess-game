package guess.domain.statistics.olap;

/**
 * Dimension.
 */
public abstract class Dimension<T> {
    private final DimensionType dimensionType;
    private final T value;

    public Dimension(DimensionType dimensionType, T value) {
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
