package guess.domain.statistics;

import java.time.LocalDate;

/**
 * Abstract event metrics.
 */
public abstract class AbstractEventMetrics {
    private final LocalDate startDate;
    private final long duration;
    private final long talksQuantity;
    private final long speakersQuantity;
    private final long javaChampionsQuantity;
    private final long mvpsQuantity;

    public AbstractEventMetrics(LocalDate startDate, long duration, long talksQuantity, long speakersQuantity,
                                long javaChampionsQuantity, long mvpsQuantity) {
        this.startDate = startDate;
        this.duration = duration;
        this.talksQuantity = talksQuantity;
        this.speakersQuantity = speakersQuantity;
        this.javaChampionsQuantity = javaChampionsQuantity;
        this.mvpsQuantity = mvpsQuantity;
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

    public long getJavaChampionsQuantity() {
        return javaChampionsQuantity;
    }

    public long getMvpsQuantity() {
        return mvpsQuantity;
    }
}
