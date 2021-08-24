package guess.domain.statistics.olap.measure;

import guess.domain.source.EventType;

import java.util.Set;

/**
 * Event types quantity measure.
 */
public class EventTypesQuantityMeasure extends Measure<EventType> {
    public EventTypesQuantityMeasure(Set<EventType> entities) {
        super(entities);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
