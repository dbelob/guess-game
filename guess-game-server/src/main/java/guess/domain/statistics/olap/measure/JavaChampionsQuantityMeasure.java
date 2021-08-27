package guess.domain.statistics.olap.measure;

import guess.domain.source.Speaker;

/**
 * Java Champions quantity measure.
 */
public class JavaChampionsQuantityMeasure extends Measure<Speaker> {
    public JavaChampionsQuantityMeasure(Object entity) {
        super(Speaker.class, entity);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
