package guess.domain.statistics.olap.measure;

import guess.domain.source.EventType;
import guess.domain.statistics.olap.MeasureType;

import java.util.Set;

/**
 * Event types quantity measure.
 */
public class EventTypesQuantityMeasure extends Measure<EventType, Long> {
    public EventTypesQuantityMeasure(Set<EventType> entities) {
        super(MeasureType.EVENT_TYPES_QUANTITY, entities);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
