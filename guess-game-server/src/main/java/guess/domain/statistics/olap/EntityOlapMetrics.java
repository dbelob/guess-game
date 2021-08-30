package guess.domain.statistics.olap;

import java.util.List;

/**
 * Entity OLAP metrics.
 */
public class EntityOlapMetrics<T> {
    private final T entity;
    private final List<Long> measureValues;

    public EntityOlapMetrics(T entity, List<Long> measureValues) {
        this.entity = entity;
        this.measureValues = measureValues;
    }

    public T getEntity() {
        return entity;
    }

    public List<Long> getMeasureValues() {
        return measureValues;
    }
}
