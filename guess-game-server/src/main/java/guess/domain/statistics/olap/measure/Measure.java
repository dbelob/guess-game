package guess.domain.statistics.olap.measure;

import java.util.Set;

/**
 * Measure.
 */
public abstract class Measure<T> {
    protected final Set<T> entities;

    protected Measure(Set<T> entities) {
        this.entities = entities;
    }

    public Set<T> getEntities() {
        return entities;
    }

    public abstract Long calculateValue();
}
