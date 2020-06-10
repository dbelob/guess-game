package guess.domain.statistics;

import java.util.List;

/**
 * Event statistics.
 */
public class EventStatistics {
    private final List<EventMetrics> eventMetricsList;
    private final EventMetrics totals;

    public EventStatistics(List<EventMetrics> eventMetricsList, EventMetrics totals) {
        this.eventMetricsList = eventMetricsList;
        this.totals = totals;
    }

    public List<EventMetrics> getEventMetricsList() {
        return eventMetricsList;
    }

    public EventMetrics getTotals() {
        return totals;
    }
}
