package guess.domain.statistics.olap.measure;

import guess.domain.source.Event;

import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Duration measure.
 */
public class DurationMeasure extends Measure<Event> {
    public DurationMeasure(Set<Object> entities) {
        super(Event.class, entities);
    }

    @Override
    public long calculateValue() {
        return entities.stream()
                .mapToLong(e -> ChronoUnit.DAYS.between(e.getStartDate(), e.getEndDate()) + 1)
                .sum();
    }
}
