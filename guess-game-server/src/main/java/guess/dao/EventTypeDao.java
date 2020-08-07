package guess.dao;

import guess.domain.source.Event;
import guess.domain.source.EventType;

import java.util.List;
import java.util.function.Function;

/**
 * Event type DAO.
 */
public interface EventTypeDao {
    List<EventType> getEventTypes();

    EventType getEventTypeById(long id);

    EventType getEventTypeByEvent(Event event);

    <T> List<T> getItemsByEventTypeIds(List<Long> eventTypeIds,
                                       Function<Long, List<T>> eventTypeConferenceFunction,
                                       Function<Void, List<T>> resultFunction);
}
