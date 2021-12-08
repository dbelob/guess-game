package guess.domain.statistics.olap;

import java.util.List;

/**
 * OLAP entity metrics.
 */
public record OlapEntityMetrics<T>(T entity, List<Long> measureValues, long total) {
}
