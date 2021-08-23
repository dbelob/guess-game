package guess.domain.statistics.olap.measure;

import guess.domain.statistics.olap.MeasureType;

import java.util.Objects;
import java.util.Set;

/**
 * Measure.
 */
public abstract class Measure<T, S> {
    private final MeasureType measureType;
    protected final Set<T> entities;

    public Measure(MeasureType measureType, Set<T> entities) {
        this.measureType = measureType;
        this.entities = entities;
    }

    public MeasureType getMeasureType() {
        return measureType;
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
        return getMeasureType() == measure.getMeasureType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMeasureType());
    }
}
