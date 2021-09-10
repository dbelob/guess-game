package guess.dto.statistics.olap;

import guess.domain.statistics.olap.OlapEntityMetrics;

import java.util.List;
import java.util.Objects;

/**
 * OLAP entity metrics DTO.
 */
public class OlapEntityMetricsDto {
    private final List<Long> measureValues;
    private final Long total;

    public OlapEntityMetricsDto(List<Long> measureValues, Long total) {
        this.measureValues = measureValues;
        this.total = total;
    }

    public List<Long> getMeasureValues() {
        return measureValues;
    }

    public Long getTotal() {
        return total;
    }

    public static OlapEntityMetricsDto convertToDto(OlapEntityMetrics<?> entityMetrics) {
        return new OlapEntityMetricsDto(entityMetrics.getMeasureValues(), entityMetrics.getTotal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OlapEntityMetricsDto)) return false;
        OlapEntityMetricsDto that = (OlapEntityMetricsDto) o;
        return Objects.equals(getMeasureValues(), that.getMeasureValues()) && Objects.equals(getTotal(), that.getTotal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMeasureValues(), getTotal());
    }

    @Override
    public String toString() {
        return "OlapEntityMetricsDto{" +
                "measureValues=" + measureValues +
                ", total=" + total +
                '}';
    }
}
