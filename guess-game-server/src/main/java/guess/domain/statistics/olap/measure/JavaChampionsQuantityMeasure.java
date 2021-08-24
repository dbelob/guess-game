package guess.domain.statistics.olap.measure;

import guess.domain.source.Speaker;

import java.util.Set;

/**
 * Java Champions quantity measure.
 */
public class JavaChampionsQuantityMeasure extends Measure<Speaker> {
    public JavaChampionsQuantityMeasure(Set<Speaker> entities) {
        super(entities);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
