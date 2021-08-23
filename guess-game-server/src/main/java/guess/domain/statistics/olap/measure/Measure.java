package guess.domain.statistics.olap.measure;

import guess.domain.statistics.olap.dimension.Dimension;

import java.util.Objects;
import java.util.Set;

/**
 * Measure.
 */
public abstract class Measure<T, S> {
    private final Set<Dimension> dimensions;
    protected final Set<T> entities;

    public Measure(Set<Dimension> dimensions, Set<T> entities) {
        this.dimensions = dimensions;
        this.entities = entities;
    }

    public Set<Dimension> getDimensions() {
        return dimensions;
    }

    public Set<T> getEntities() {
        return entities;
    }

    public abstract S calculateValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Measure)) return false;
        Measure<?, ?> measure = (Measure<?, ?>) o;
        return Objects.equals(getDimensions(), measure.getDimensions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDimensions());
    }
}
