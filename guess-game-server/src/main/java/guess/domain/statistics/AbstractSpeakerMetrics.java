package guess.domain.statistics;

/**
 * Abstract speaker metrics.
 */
public abstract class AbstractSpeakerMetrics extends AbstractMetrics {
    private final long eventsQuantity;
    private final long eventTypesQuantity;

    public AbstractSpeakerMetrics(long talksQuantity, long eventsQuantity, long eventTypesQuantity, long javaChampionsQuantity,
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
}
