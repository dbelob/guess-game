package guess.domain.statistics.eventtype;

import guess.domain.source.EventType;
import guess.domain.statistics.Metrics;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Event type metrics.
 */
public class EventTypeMetrics extends AbstractEventTypeMetrics {
    private final EventType eventType;

    public EventTypeMetrics(EventType eventType, LocalDate startDate, long age, long duration, long eventsQuantity,
                            long speakersQuantity, Metrics metrics) {
        super(startDate, age, duration, eventsQuantity, speakersQuantity, metrics);

        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventTypeMetrics)) return false;
        if (!super.equals(o)) return false;
        EventTypeMetrics that = (EventTypeMetrics) o;
        return Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), eventType);
    }

    @Override
    public String toString() {
        return "EventTypeMetrics{" +
                "eventType=" + eventType +
                ", startDate=" + getStartDate() +
                ", age=" + getAge() +
                ", duration=" + getDuration() +
                ", eventsQuantity=" + getEventsQuantity() +
                ", speakersQuantity=" + getSpeakersQuantity() +
                ", talksQuantity=" + getTalksQuantity() +
                ", javaChampionsQuantity=" + getJavaChampionsQuantity() +
                ", mvpsQuantity=" + getMvpsQuantity() +
                '}';
    }
}
