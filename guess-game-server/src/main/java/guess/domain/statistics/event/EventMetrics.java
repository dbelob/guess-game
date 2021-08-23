package guess.domain.statistics.event;

import guess.domain.source.Event;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Event metrics.
 */
public class EventMetrics extends AbstractEventMetrics {
    private final Event event;

    public EventMetrics(Event event, LocalDate startDate, long duration, long talksQuantity, long speakersQuantity,
                        long javaChampionsQuantity, long mvpsQuantity) {
        super(startDate, duration, talksQuantity, speakersQuantity, javaChampionsQuantity, mvpsQuantity);

        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventMetrics)) return false;
        if (!super.equals(o)) return false;
        EventMetrics that = (EventMetrics) o;
        return Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), event);
    }

    @Override
    public String toString() {
        return "EventMetrics{" +
                "event=" + event +
                '}';
    }
}
