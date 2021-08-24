package guess.domain.statistics.olap.dimension;

import java.util.Objects;

/**
 * Dimension.
 */
public abstract class Dimension<T> {
    private final T value;

    protected Dimension(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dimension)) return false;
        Dimension<?> dimension = (Dimension<?>) o;
        return Objects.equals(getValue(), dimension.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
