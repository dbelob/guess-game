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

    public SpeakerMetrics(Speaker speaker, long talksQuantity, long eventsQuantity, long eventTypesQuantity) {
        this.speaker = speaker;
        this.talksQuantity = talksQuantity;
        this.eventsQuantity = eventsQuantity;
        this.eventTypesQuantity = eventTypesQuantity;
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
}
