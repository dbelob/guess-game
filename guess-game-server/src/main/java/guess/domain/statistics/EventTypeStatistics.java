package guess.domain.statistics;

import java.util.List;
import java.util.Objects;

/**
 * Event type statistics.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventTypeStatistics)) return false;
        EventTypeStatistics that = (EventTypeStatistics) o;
        return Objects.equals(eventTypeMetricsList, that.eventTypeMetricsList) &&
                Objects.equals(totals, that.totals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventTypeMetricsList, totals);
    }

    @Override
    public String toString() {
        return "EventTypeStatistics{" +
                "eventTypeMetricsList=" + eventTypeMetricsList +
                ", totals=" + totals +
                '}';
    }
}
