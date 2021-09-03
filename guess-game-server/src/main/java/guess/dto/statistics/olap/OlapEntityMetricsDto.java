package guess.dto.statistics.olap;

import java.util.List;
import java.util.Objects;

/**
 * OLAP entity metrics DTO.
 */
public abstract class OlapEntityMetricsDto {
    private final List<Long> measureValues;

    public OlapEntityMetricsDto(List<Long> measureValues) {
        this.measureValues = measureValues;
    }

    public List<Long> getMeasureValues() {
        return measureValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OlapEntityMetricsDto)) return false;
        OlapEntityMetricsDto that = (OlapEntityMetricsDto) o;
        return Objects.equals(getMeasureValues(), that.getMeasureValues());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMeasureValues());
    }

    @Override
    public String toString() {
        return "OlapEntityMetricsDto{" +
                "measureValues=" + measureValues +
                '}';
    }
}
