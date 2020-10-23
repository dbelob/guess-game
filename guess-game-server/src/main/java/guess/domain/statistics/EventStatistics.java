package guess.domain.statistics;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventStatistics)) return false;
        EventStatistics that = (EventStatistics) o;
        return Objects.equals(eventMetricsList, that.eventMetricsList) &&
                Objects.equals(totals, that.totals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventMetricsList, totals);
    }

    @Override
    public String toString() {
        return "EventStatistics{" +
                "eventMetricsList=" + eventMetricsList +
                ", totals=" + totals +
                '}';
    }
}
