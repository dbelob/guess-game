package guess.domain.statistics;

import java.time.LocalDate;

/**
 * Abstract event type metrics.
 */
public abstract class AbstractEventTypeMetrics {
    private final LocalDate startDate;
    private final long age;
    private final long duration;
    private final long eventsQuantity;
    private final long talksQuantity;
    private final long speakersQuantity;
    private final long javaChampionsQuantity;
    private final long mvpsQuantity;

    public AbstractEventTypeMetrics(LocalDate startDate, long age, long duration, long eventsQuantity, long talksQuantity,
                                    long speakersQuantity, long javaChampionsQuantity, long mvpsQuantity) {
        this.startDate = startDate;
        this.age = age;
        this.duration = duration;
        this.eventsQuantity = eventsQuantity;
        this.talksQuantity = talksQuantity;
        this.speakersQuantity = speakersQuantity;
        this.javaChampionsQuantity = javaChampionsQuantity;
        this.mvpsQuantity = mvpsQuantity;
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

    public long getJavaChampionsQuantity() {
        return javaChampionsQuantity;
    }

    public long getMvpsQuantity() {
        return mvpsQuantity;
    }
}
