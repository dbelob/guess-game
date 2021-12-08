package guess.domain.statistics.eventtype;

import java.util.List;

/**
 * Event type statistics.
 */
public record EventTypeStatistics(List<EventTypeMetrics> eventTypeMetricsList, EventTypeMetrics totals) {
}
