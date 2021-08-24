package guess.domain.statistics.olap.measure;

import guess.domain.source.Event;

import java.util.Set;

/**
 * Events quantity measure.
 */
public class EventsQuantityMeasure extends Measure<Event> {
    public EventsQuantityMeasure(Set<Event> entities) {
        super(entities);
    }

    @Override
    public Long calculateValue() {
        return (long) entities.size();
    }
}
