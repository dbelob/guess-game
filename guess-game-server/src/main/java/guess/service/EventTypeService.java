package guess.service;

import guess.domain.source.Event;
import guess.domain.source.EventType;

import java.util.List;

/**
 * Event type service.
 */
public interface EventTypeService {
    EventType getEventTypeById(long id);

    List<EventType> getEventTypes();

    List<EventType> getEventTypes(boolean isConferences, boolean isMeetups);

    EventType getEventTypeByEvent(Event event);
}
