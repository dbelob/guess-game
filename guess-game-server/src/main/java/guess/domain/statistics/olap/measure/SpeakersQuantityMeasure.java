package guess.domain.statistics.olap.measure;

import guess.domain.source.Speaker;

import java.util.Set;

/**
 * Speakers quantity measure.
 */
public class SpeakersQuantityMeasure extends Measure<Speaker> {
    public SpeakersQuantityMeasure(Set<Object> entities) {
        super(Speaker.class, entities);
    }

    @Override
    public long calculateValue() {
        return entities.size();
    }
}
