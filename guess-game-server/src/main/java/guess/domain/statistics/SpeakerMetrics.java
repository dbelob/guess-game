package guess.domain.statistics;

import guess.domain.source.Speaker;

/**
 * Speaker metrics.
 */
public class SpeakerMetrics extends AbstractSpeakerMetrics {
    private final Speaker speaker;

    public SpeakerMetrics(Speaker speaker, long talksQuantity, long eventsQuantity, long eventTypesQuantity,
                          long javaChampionsQuantity, long mvpsQuantity) {
        super(talksQuantity, eventsQuantity, eventTypesQuantity, javaChampionsQuantity, mvpsQuantity);

        this.speaker = speaker;
    }

    public Speaker getSpeaker() {
        return speaker;
    }
}
