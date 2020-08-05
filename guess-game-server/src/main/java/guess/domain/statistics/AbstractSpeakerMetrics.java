package guess.domain.statistics;

/**
 * Abstract speaker metrics.
 */
public abstract class AbstractSpeakerMetrics {
    private final long talksQuantity;
    private final long eventsQuantity;
    private final long eventTypesQuantity;
    private final long javaChampionsQuantity;
    private final long mvpsQuantity;

    public AbstractSpeakerMetrics(long talksQuantity, long eventsQuantity, long eventTypesQuantity, long javaChampionsQuantity,
                                  long mvpsQuantity) {
        this.talksQuantity = talksQuantity;
        this.eventsQuantity = eventsQuantity;
        this.eventTypesQuantity = eventTypesQuantity;
        this.javaChampionsQuantity = javaChampionsQuantity;
        this.mvpsQuantity = mvpsQuantity;
    }

    public long getTalksQuantity() {
        return talksQuantity;
    }

    public long getEventsQuantity() {
        return eventsQuantity;
    }

    public long getEventTypesQuantity() {
        return eventTypesQuantity;
    }

    public long getJavaChampionsQuantity() {
        return javaChampionsQuantity;
    }

    public long getMvpsQuantity() {
        return mvpsQuantity;
    }
}
