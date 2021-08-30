package guess.domain.statistics.olap;

import java.util.List;

/**
 * Event type OLAP statistics.
 */
public class EntityOlapStatistics<T, S> {
    private final List<T> dimensionValues;
    private final List<EntityOlapMetrics<S>> metricsList;

    public EntityOlapStatistics(List<T> dimensionValues, List<EntityOlapMetrics<S>> metricsList) {
        this.dimensionValues = dimensionValues;
        this.metricsList = metricsList;
    }

    public List<T> getDimensionValues() {
        return dimensionValues;
    }

    public List<EntityOlapMetrics<S>> getMetricsList() {
        return metricsList;
    }
}
