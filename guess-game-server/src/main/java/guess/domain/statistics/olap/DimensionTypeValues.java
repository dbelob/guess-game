package guess.domain.statistics.olap;

import java.util.List;

/**
 * Dimension type and values.
 */
public record DimensionTypeValues<T>(DimensionType type, List<T> values) {
}
