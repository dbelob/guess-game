package guess.domain.statistics.olap.measure;

import guess.domain.source.Speaker;

import java.util.Set;

/**
 * MVPs quantity measure.
 */
public class MvpsQuantityMeasure extends Measure<Speaker> {
    public MvpsQuantityMeasure(Set<Speaker> entities) {
        super(entities);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
