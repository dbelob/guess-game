package guess.domain.statistics.olap.measure;

import guess.domain.statistics.olap.dimension.Dimension;

import java.util.Objects;
import java.util.Set;

/**
 * Measure.
 */
public abstract class Measure<T> {
    private final Set<Dimension> dimensions;
    private final T value;

    public Measure(Set<Dimension> dimensions, T value) {
        this.dimensions = dimensions;
        this.value = value;
    }

    public Set<Dimension> getDimensions() {
        return dimensions;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Measure)) return false;
        Measure<?> measure = (Measure<?>) o;
        return getDimensions().equals(measure.getDimensions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDimensions());
    }
}
