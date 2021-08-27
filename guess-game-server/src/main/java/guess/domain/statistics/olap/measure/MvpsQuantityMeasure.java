package guess.domain.statistics.olap.measure;

import guess.domain.source.Speaker;

/**
 * MVPs quantity measure.
 */
public class MvpsQuantityMeasure extends Measure<Speaker> {
    public MvpsQuantityMeasure(Object entity) {
        super(Speaker.class, entity);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
