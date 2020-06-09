package guess.domain.statistics;

import java.util.List;

/**
 * Event type metrics.
 */
public class EventTypeStatistics {
    private final List<EventTypeMetrics> eventTypeMetricsList;
    private final EventTypeMetrics totals;

    public EventTypeStatistics(List<EventTypeMetrics> eventTypeMetricsList, EventTypeMetrics totals) {
        this.eventTypeMetricsList = eventTypeMetricsList;
        this.totals = totals;
    }

    public List<EventTypeMetrics> getEventTypeMetricsList() {
        return eventTypeMetricsList;
    }

    public EventTypeMetrics getTotals() {
        return totals;
    }
}
