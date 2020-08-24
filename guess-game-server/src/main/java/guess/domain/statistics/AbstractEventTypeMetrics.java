package guess.domain.statistics;

import java.time.LocalDate;

/**
 * Abstract event type metrics.
 */
public abstract class AbstractEventTypeMetrics extends Metrics {
    private final LocalDate startDate;
    private final long age;
    private final long duration;
    private final long eventsQuantity;
    private final long speakersQuantity;

    public AbstractEventTypeMetrics(LocalDate startDate, long age, long duration, long eventsQuantity,
                                    long speakersQuantity, Metrics metrics) {
        super(metrics.getTalksQuantity(), metrics.getJavaChampionsQuantity(), metrics.getMvpsQuantity());

        this.startDate = startDate;
        this.age = age;
        this.duration = duration;
        this.eventsQuantity = eventsQuantity;
        this.speakersQuantity = speakersQuantity;
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

    public long getSpeakersQuantity() {
        return speakersQuantity;
    }
}
