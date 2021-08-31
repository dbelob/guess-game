package guess.domain.statistics.olap;

import java.util.List;

/**
 * OLAP entity metrics.
 */
public class OlapEntityMetrics<T> {
    private final T entity;
    private final List<Long> measureValues;

    public OlapEntityMetrics(T entity, List<Long> measureValues) {
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
