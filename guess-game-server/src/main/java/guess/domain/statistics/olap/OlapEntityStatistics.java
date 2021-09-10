package guess.domain.statistics.olap;

import java.util.List;

/**
 * OLAP entity statistics.
 */
public class OlapEntityStatistics<T, S> {
    private final List<T> dimensionValues;
    private final List<OlapEntityMetrics<S>> metricsList;
    private final OlapEntityMetrics<Void> totals;

    public OlapEntityStatistics(List<T> dimensionValues, List<OlapEntityMetrics<S>> metricsList, OlapEntityMetrics<Void> totals) {
        this.dimensionValues = dimensionValues;
        this.metricsList = metricsList;
        this.totals = totals;
    }

    public List<T> getDimensionValues() {
        return dimensionValues;
    }

    public List<OlapEntityMetrics<S>> getMetricsList() {
        return metricsList;
    }

    public OlapEntityMetrics<Void> getTotals() {
        return totals;
    }
}
