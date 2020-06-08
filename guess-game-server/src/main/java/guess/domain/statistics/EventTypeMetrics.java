package guess.domain.statistics;

import guess.domain.source.EventType;
import guess.domain.source.LocaleItem;

import java.time.LocalDate;
import java.util.List;

/**
 * Event type metrics.
 */
public class EventTypeMetrics {
    private final EventType eventType;
    private final LocalDate startDate;
    private final long age;
    private final long duration;
    private final long eventsQuantity;
    private final long talksQuantity;
    private final long speakersQuantity;

    public EventTypeMetrics(EventType eventType, LocalDate startDate, long age, long duration, long eventsQuantity, long talksQuantity, long speakersQuantity) {
        this.eventType = eventType;
        this.startDate = startDate;
        this.age = age;
        this.duration = duration;
        this.eventsQuantity = eventsQuantity;
        this.talksQuantity = talksQuantity;
        this.speakersQuantity = speakersQuantity;
    }

    public EventType getEventType() {
        return eventType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public long getAge() {
        return age;
    }

    public long getDuration() {
        return duration;
    }

    public long getEventsQuantity() {
        return eventsQuantity;
    }

    public long getTalksQuantity() {
        return talksQuantity;
    }

    public long getSpeakersQuantity() {
        return speakersQuantity;
    }
}
