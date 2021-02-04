package guess.domain.auxiliary;

import guess.domain.source.Event;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Event, minimal track time, end date time.
 */
public class EventMinTrackTimeEndDayTime {
    private final Event event;
    private final LocalDateTime minTrackDateTime;
    private final LocalDateTime endDayDateTime;

    public EventMinTrackTimeEndDayTime(Event event, LocalDateTime minTrackDateTime, LocalDateTime endDayDateTime) {
        this.event = event;
        this.minTrackDateTime = minTrackDateTime;
        this.endDayDateTime = endDayDateTime;
    }

    public Event getEvent() {
        return event;
    }

    public LocalDateTime getMinTrackDateTime() {
        return minTrackDateTime;
    }

    public LocalDateTime getEndDayDateTime() {
        return endDayDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventMinTrackTimeEndDayTime)) return false;
        EventMinTrackTimeEndDayTime that = (EventMinTrackTimeEndDayTime) o;
        return Objects.equals(event, that.event) && Objects.equals(minTrackDateTime, that.minTrackDateTime) && Objects.equals(endDayDateTime, that.endDayDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, minTrackDateTime, endDayDateTime);
    }

    @Override
    public String toString() {
        return "EventMinTrackTimeEndDayTime{" +
                "event=" + event +
                ", minTrackDateTime=" + minTrackDateTime +
                ", endDayDateTime=" + endDayDateTime +
                '}';
    }
}
