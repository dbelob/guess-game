package guess.domain.statistics.olap.measure;

import guess.domain.source.EventType;
import guess.domain.statistics.olap.dimension.Dimension;

import java.util.Set;

/**
 * Event types quantity measure.
 */
public class EventTypesQuantityMeasure extends Measure<EventType, Long> {
    public EventTypesQuantityMeasure(Set<Dimension> dimensions, Set<EventType> entities) {
        super(dimensions, entities);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
