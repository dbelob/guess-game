package guess.domain.statistics.event;

import java.util.List;

/**
 * Event statistics.
 */
public record EventStatistics(List<EventMetrics> eventMetricsList, EventMetrics totals) {
}
