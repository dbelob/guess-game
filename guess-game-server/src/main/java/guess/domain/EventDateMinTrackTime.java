package guess.domain;

import guess.domain.source.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Event, date, minimal track time.
 */
public class EventDateMinTrackTime {
    private final Event event;
    private final LocalDate date;
    private final LocalTime minTrackTime;

    public EventDateMinTrackTime(Event event, LocalDate date, LocalTime minTrackTime) {
        this.event = event;
        this.date = date;
        this.minTrackTime = minTrackTime;
    }

    public Event getEvent() {
        return event;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getMinTrackTime() {
        return minTrackTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventDateMinTrackTime)) return false;
        EventDateMinTrackTime that = (EventDateMinTrackTime) o;
        return Objects.equals(event, that.event) &&
                Objects.equals(date, that.date) &&
                Objects.equals(minTrackTime, that.minTrackTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, date, minTrackTime);
    }

    @Override
    public String toString() {
        return "EventDateMinTrackTime{" +
                "event=" + event +
                ", date=" + date +
                ", minTrackTime=" + minTrackTime +
                '}';
    }
}
