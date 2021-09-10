package guess.domain.statistics.olap;

import java.util.List;

/**
 * OLAP entity metrics.
 */
public class OlapEntityMetrics<T> {
    private final T entity;
    private final List<Long> measureValues;
    private final Long total;

    public OlapEntityMetrics(T entity, List<Long> measureValues, Long total) {
        this.entity = entity;
        this.measureValues = measureValues;
        this.total = total;
    }

    public T getEntity() {
        return entity;
    }

    public List<Long> getMeasureValues() {
        return measureValues;
    }

    public Long getTotal() {
        return total;
    }
}
