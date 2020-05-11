package guess.dao;

import guess.domain.source.EventType;

import java.util.List;

/**
 * Event type DAO.
 */
public interface EventTypeDao {
    List<EventType> getEventTypes();
}
