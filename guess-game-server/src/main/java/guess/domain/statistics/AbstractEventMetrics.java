package guess.domain.statistics;

import java.time.LocalDate;

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
}
