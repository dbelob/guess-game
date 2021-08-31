package guess.domain.statistics.olap;

import java.util.List;

/**
 * OLAP entity statistics.
 */
public class OlapEntityStatistics<T, S> {
    private final List<T> dimensionValues;
    private final List<OlapEntityMetrics<S>> metricsList;

    public OlapEntityStatistics(List<T> dimensionValues, List<OlapEntityMetrics<S>> metricsList) {
        this.dimensionValues = dimensionValues;
        this.metricsList = metricsList;
    }

    public List<T> getDimensionValues() {
        return dimensionValues;
    }

    public List<OlapEntityMetrics<S>> getMetricsList() {
        return metricsList;
    }
}
