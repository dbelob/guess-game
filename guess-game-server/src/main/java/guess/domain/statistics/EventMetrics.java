package guess.domain.statistics;

import guess.domain.source.Event;

import java.time.LocalDate;

/**
 * Event metrics.
 */
public class EventMetrics {
    private final Event event;
    private final LocalDate startDate;
    private final long duration;
    private final long talksQuantity;
    private final long speakersQuantity;

    public EventMetrics(Event event, LocalDate startDate, long duration, long talksQuantity, long speakersQuantity) {
        this.event = event;
        this.startDate = startDate;
        this.duration = duration;
        this.talksQuantity = talksQuantity;
        this.speakersQuantity = speakersQuantity;
    }

    public Event getEvent() {
        return event;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public long getDuration() {
        return duration;
    }

    public long getTalksQuantity() {
        return talksQuantity;
    }

    public long getSpeakersQuantity() {
        return speakersQuantity;
    }
}
