package guess.domain.statistics.eventtype;

import guess.domain.statistics.Metrics;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstract event type metrics.
 */
public abstract class AbstractEventTypeMetrics extends Metrics {
    private final LocalDate startDate;
    private final long age;
    private final long duration;
    private final long eventsQuantity;
    private final long speakersQuantity;

    protected AbstractEventTypeMetrics(LocalDate startDate, long age, long duration, long eventsQuantity,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEventTypeMetrics)) return false;
        if (!super.equals(o)) return false;
        AbstractEventTypeMetrics that = (AbstractEventTypeMetrics) o;
        return age == that.age &&
                duration == that.duration &&
                eventsQuantity == that.eventsQuantity &&
                speakersQuantity == that.speakersQuantity &&
                Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startDate, age, duration, eventsQuantity, speakersQuantity);
    }

    @Override
    public String toString() {
        return "AbstractEventTypeMetrics{" +
                "startDate=" + startDate +
                ", age=" + age +
                ", duration=" + duration +
                ", eventsQuantity=" + eventsQuantity +
                ", speakersQuantity=" + speakersQuantity +
                ", talksQuantity=" + getTalksQuantity() +
                ", javaChampionsQuantity=" + getJavaChampionsQuantity() +
                ", mvpsQuantity=" + getMvpsQuantity() +
                '}';
    }
}
