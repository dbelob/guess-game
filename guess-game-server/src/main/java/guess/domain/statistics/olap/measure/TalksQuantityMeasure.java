package guess.domain.statistics.olap.measure;

import guess.domain.source.Talk;

import java.util.Set;

/**
 * Talks quantity measure.
 */
public class TalksQuantityMeasure extends Measure<Talk> {
    public TalksQuantityMeasure(Set<Object> entities) {
        super(Talk.class, entities);
    }

    @Override
    public long calculateValue() {
        return entities.size();
    }
}
