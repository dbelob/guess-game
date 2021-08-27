package guess.domain.statistics.olap.measure;

import guess.domain.source.Speaker;

/**
 * Speakers quantity measure.
 */
public class SpeakersQuantityMeasure extends Measure<Speaker> {
    public SpeakersQuantityMeasure(Object entity) {
        super(Speaker.class, entity);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
