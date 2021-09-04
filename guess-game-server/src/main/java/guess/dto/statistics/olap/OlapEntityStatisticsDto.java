package guess.dto.statistics.olap;

import java.util.List;

/**
 * OLAP entity statistics DTO.
 */
public class OlapEntityStatisticsDto<T, S extends OlapEntityMetricsDto> {
    private final List<T> dimensionValues;
    private final List<S> metricsList;

    public OlapEntityStatisticsDto(List<T> dimensionValues, List<S> metricsList) {
        this.dimensionValues = dimensionValues;
        this.metricsList = metricsList;
    }

    public List<T> getDimensionValues() {
        return dimensionValues;
    }

    public List<S> getMetricsList() {
        return metricsList;
    }
}
