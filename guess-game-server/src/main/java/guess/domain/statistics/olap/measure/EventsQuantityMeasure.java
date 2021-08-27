package guess.domain.statistics.olap.measure;

import guess.domain.source.Event;

/**
 * Events quantity measure.
 */
public class EventsQuantityMeasure extends Measure<Event> {
    public EventsQuantityMeasure(Object entity) {
        super(Event.class, entity);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
