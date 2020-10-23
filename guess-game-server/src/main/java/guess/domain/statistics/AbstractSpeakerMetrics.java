package guess.domain.statistics;

import java.util.Objects;

/**
 * Abstract speaker metrics.
 */
public abstract class AbstractSpeakerMetrics extends Metrics {
    private final long eventsQuantity;
    private final long eventTypesQuantity;

    protected AbstractSpeakerMetrics(long talksQuantity, long eventsQuantity, long eventTypesQuantity, long javaChampionsQuantity,
                                     long mvpsQuantity) {
        super(talksQuantity, javaChampionsQuantity, mvpsQuantity);

        this.eventsQuantity = eventsQuantity;
        this.eventTypesQuantity = eventTypesQuantity;
    }

    public long getEventsQuantity() {
        return eventsQuantity;
    }

    public long getEventTypesQuantity() {
        return eventTypesQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractSpeakerMetrics)) return false;
        if (!super.equals(o)) return false;
        AbstractSpeakerMetrics that = (AbstractSpeakerMetrics) o;
        return eventsQuantity == that.eventsQuantity &&
                eventTypesQuantity == that.eventTypesQuantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), eventsQuantity, eventTypesQuantity);
    }

    @Override
    public String toString() {
        return "AbstractSpeakerMetrics{" +
                "eventsQuantity=" + eventsQuantity +
                ", eventTypesQuantity=" + eventTypesQuantity +
                '}';
    }
}
