package guess.domain.statistics.olap.measure;

import guess.domain.source.Event;
import guess.domain.statistics.olap.MeasureType;

import java.util.Set;

/**
 * Event quantity measure.
 */
public class EventsQuantityMeasure extends Measure<Event, Long> {
    public EventsQuantityMeasure(Set<Event> entities) {
        super(MeasureType.EVENTS_QUANTITY, entities);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
