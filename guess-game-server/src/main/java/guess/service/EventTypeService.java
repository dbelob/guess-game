package guess.service;

import guess.domain.source.Event;
import guess.domain.source.EventType;

import java.util.List;

/**
 * Event type service.
 */
public interface EventTypeService {
    List<EventType> getEventTypes();

    EventType getEventTypeByEvent(Event event);
}
