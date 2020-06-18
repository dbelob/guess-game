package guess.dao;

import guess.domain.source.Event;
import guess.domain.source.EventType;

import java.util.List;

/**
 * Event type DAO.
 */
public interface EventTypeDao {
    List<EventType> getEventTypes();

    EventType getEventTypeById(long id);

    EventType getEventTypeByEvent(Event event);
}
