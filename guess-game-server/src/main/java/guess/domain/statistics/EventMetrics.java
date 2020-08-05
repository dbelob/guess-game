package guess.domain.statistics;

import guess.domain.source.Event;

import java.time.LocalDate;

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
}
