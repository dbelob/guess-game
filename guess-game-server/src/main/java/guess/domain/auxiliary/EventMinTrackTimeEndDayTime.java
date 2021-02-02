package guess.domain.auxiliary;

import guess.domain.source.Event;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Event, minimal track time, end date time.
 */
public class EventMinTrackTimeEndDayTime {
    private final Event event;
    private final LocalTime minTrackTime;
    private final LocalTime endDayTime;

    public EventMinTrackTimeEndDayTime(Event event, LocalTime minTrackTime, LocalTime endDayTime) {
        this.event = event;
        this.minTrackTime = minTrackTime;
        this.endDayTime = endDayTime;
    }

    public Event getEvent() {
        return event;
    }

    public LocalTime getMinTrackTime() {
        return minTrackTime;
    }

    public LocalTime getEndDayTime() {
        return endDayTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventMinTrackTimeEndDayTime)) return false;
        EventMinTrackTimeEndDayTime that = (EventMinTrackTimeEndDayTime) o;
        return Objects.equals(event, that.event) && Objects.equals(minTrackTime, that.minTrackTime) && Objects.equals(endDayTime, that.endDayTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, minTrackTime, endDayTime);
    }

    @Override
    public String toString() {
        return "EventMinTrackTimeEndDayTime{" +
                "event=" + event +
                ", minTrackTime=" + minTrackTime +
                ", endDayTime=" + endDayTime +
                '}';
    }
}
