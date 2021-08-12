package guess.domain.statistics.olap.dimension;

import guess.domain.source.EventType;
import guess.domain.statistics.olap.Dimension;
import guess.domain.statistics.olap.DimensionType;

/**
 * Event type dimension.
 */
public class EventTypeDimension extends Dimension<EventType> {
    public EventTypeDimension(EventType value) {
        super(DimensionType.EVENT_TYPE, value);
    }
}
