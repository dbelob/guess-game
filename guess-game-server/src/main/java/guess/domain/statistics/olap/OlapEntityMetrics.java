package guess.domain.statistics.olap;

import java.util.List;
import java.util.Objects;

/**
 * OLAP entity metrics.
 */
public class OlapEntityMetrics<T> {
    private final T entity;
    private final List<Long> measureValues;
    private final long total;

    public OlapEntityMetrics(T entity, List<Long> measureValues, long total) {
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

    public long getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OlapEntityMetrics)) return false;
        OlapEntityMetrics<?> that = (OlapEntityMetrics<?>) o;
        return Objects.equals(getEntity(), that.getEntity()) && Objects.equals(getMeasureValues(), that.getMeasureValues()) && Objects.equals(getTotal(), that.getTotal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEntity(), getMeasureValues(), getTotal());
    }

    @Override
    public String toString() {
        return "OlapEntityMetrics{" +
                "entity=" + entity +
                ", measureValues=" + measureValues +
                ", total=" + total +
                '}';
    }
}
