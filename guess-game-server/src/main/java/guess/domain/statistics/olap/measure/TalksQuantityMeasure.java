package guess.domain.statistics.olap.measure;

import guess.domain.source.Talk;

/**
 * Talks quantity measure.
 */
public class TalksQuantityMeasure extends Measure<Talk> {
    public TalksQuantityMeasure(Object entity) {
        super(Talk.class, entity);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
