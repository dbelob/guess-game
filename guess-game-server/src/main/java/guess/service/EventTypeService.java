package guess.service;

import guess.domain.source.Event;
import guess.domain.source.EventType;

/**
 * Event type service.
 */
public interface EventTypeService {
    EventType getEventTypeByEvent(Event event);
}
