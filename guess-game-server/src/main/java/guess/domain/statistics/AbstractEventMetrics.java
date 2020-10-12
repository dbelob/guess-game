package guess.domain.statistics;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstract event metrics.
 */
public abstract class AbstractEventMetrics extends Metrics {
    private final LocalDate startDate;
    private final long duration;
    private final long speakersQuantity;

    protected AbstractEventMetrics(LocalDate startDate, long duration, long talksQuantity, long speakersQuantity,
                                   long javaChampionsQuantity, long mvpsQuantity) {
        super(talksQuantity, javaChampionsQuantity, mvpsQuantity);

        this.startDate = startDate;
        this.duration = duration;
        this.speakersQuantity = speakersQuantity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public long getDuration() {
        return duration;
    }

    public long getSpeakersQuantity() {
        return speakersQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEventMetrics)) return false;
        if (!super.equals(o)) return false;
        AbstractEventMetrics that = (AbstractEventMetrics) o;
        return duration == that.duration &&
                speakersQuantity == that.speakersQuantity &&
                Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startDate, duration, speakersQuantity);
    }

    @Override
    public String toString() {
        return "AbstractEventMetrics{" +
                "startDate=" + startDate +
                ", duration=" + duration +
                ", speakersQuantity=" + speakersQuantity +
                '}';
    }
}
