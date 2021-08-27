package guess.domain.statistics.olap.measure;

import guess.domain.source.Event;

import java.time.temporal.ChronoUnit;

/**
 * Duration measure.
 */
public class DurationMeasure extends Measure<Event> {
    public DurationMeasure(Object entity) {
        super(Event.class, entity);
    }

    @Override
    public Long calculateValue() {
        return entities.stream()
                .mapToLong(e -> ChronoUnit.DAYS.between(e.getStartDate(), e.getEndDate()) + 1)
                .sum();
    }
}
