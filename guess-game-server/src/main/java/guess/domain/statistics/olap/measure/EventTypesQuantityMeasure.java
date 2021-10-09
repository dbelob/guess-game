package guess.domain.statistics.olap.measure;

import guess.domain.source.EventType;

import java.util.Set;

/**
 * Event types quantity measure.
 */
public class EventTypesQuantityMeasure extends Measure<EventType> {
    public EventTypesQuantityMeasure(Set<Object> entities) {
        super(EventType.class, entities);
    }

    @Override
    public long calculateValue() {
        return entities.size();
    }
}
