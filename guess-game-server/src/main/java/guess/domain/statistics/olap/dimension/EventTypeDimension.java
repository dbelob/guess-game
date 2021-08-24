package guess.domain.statistics.olap.dimension;

import guess.domain.source.EventType;

/**
 * Event type dimension.
 */
public class EventTypeDimension extends Dimension<EventType> {
    public EventTypeDimension(EventType value) {
        super(value);
    }
}
