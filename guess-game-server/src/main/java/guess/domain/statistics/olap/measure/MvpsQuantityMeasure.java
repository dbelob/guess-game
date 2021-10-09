package guess.domain.statistics.olap.measure;

import guess.domain.source.Speaker;

import java.util.Set;

/**
 * MVPs quantity measure.
 */
public class MvpsQuantityMeasure extends Measure<Speaker> {
    public MvpsQuantityMeasure(Set<Object> entities) {
        super(Speaker.class, entities);
    }

    @Override
    public long calculateValue() {
        return entities.size();
    }
}
