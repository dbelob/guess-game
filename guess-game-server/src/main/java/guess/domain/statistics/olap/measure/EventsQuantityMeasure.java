package guess.domain.statistics.olap.measure;

import guess.domain.source.Event;

import java.util.Set;

/**
 * Events quantity measure.
 */
public class EventsQuantityMeasure extends Measure<Event> {
    public EventsQuantityMeasure(Set<Object> entities) {
        super(Event.class, entities);
    }

    @Override
    public long calculateValue() {
        return entities.size();
    }
}
