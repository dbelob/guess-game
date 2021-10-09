package guess.domain.statistics.olap.measure;

import guess.domain.source.Speaker;

import java.util.Set;

/**
 * Java Champions quantity measure.
 */
public class JavaChampionsQuantityMeasure extends Measure<Speaker> {
    public JavaChampionsQuantityMeasure(Set<Object> entities) {
        super(Speaker.class, entities);
    }

    @Override
    public long calculateValue() {
        return entities.size();
    }
}
