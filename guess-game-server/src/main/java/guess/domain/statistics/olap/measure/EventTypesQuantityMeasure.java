package guess.domain.statistics.olap.measure;

import guess.domain.source.EventType;

/**
 * Event types quantity measure.
 */
public class EventTypesQuantityMeasure extends Measure<EventType> {
    public EventTypesQuantityMeasure(Object entity) {
        super(EventType.class, entity);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
