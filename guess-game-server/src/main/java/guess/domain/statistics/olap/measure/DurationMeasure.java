package guess.domain.statistics.olap.measure;

import guess.domain.source.Event;
import guess.domain.statistics.olap.MeasureType;

import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Duration measure.
 */
public class DurationMeasure extends Measure<Event, Long> {
    public DurationMeasure(Set<Event> entities) {
        super(MeasureType.DURATION, entities);
    }

    @Override
    public Long calculateValue() {
        return entities.stream()
                .mapToLong(e -> ChronoUnit.DAYS.between(e.getStartDate(), e.getEndDate()) + 1)
                .sum();
    }
}
