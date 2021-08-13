package guess.domain.statistics.olap.dimension;

import guess.domain.source.EventType;
import guess.domain.statistics.olap.DimensionType;

import java.util.Set;

/**
 * Event type dimension.
 */
public class EventTypeDimension extends Dimension<EventType> {
    public EventTypeDimension(Set<EventType> values) {
        super(DimensionType.EVENT_TYPE, values);
    }
}
