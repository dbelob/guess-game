package guess.domain.statistics;

import guess.domain.source.Speaker;

/**
 * Speaker metrics.
 */
public class SpeakerMetrics {
    private final Speaker speaker;
    private final long talksQuantity;
    private final long eventsQuantity;
    private final long eventTypesQuantity;
    private final long javaChampionsQuantity;
    private final long mvpsQuantity;

    public SpeakerMetrics(Speaker speaker, long talksQuantity, long eventsQuantity, long eventTypesQuantity,
                          long javaChampionsQuantity, long mvpsQuantity) {
        this.speaker = speaker;
        this.talksQuantity = talksQuantity;
        this.eventsQuantity = eventsQuantity;
        this.eventTypesQuantity = eventTypesQuantity;
        this.javaChampionsQuantity = javaChampionsQuantity;
        this.mvpsQuantity = mvpsQuantity;
    }

    public Speaker getSpeaker() {
        return speaker;
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
