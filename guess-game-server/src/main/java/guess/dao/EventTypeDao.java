package guess.dao;

import guess.domain.source.Event;
import guess.domain.source.EventType;

import java.util.List;
import java.util.function.Function;
import java.util.function.LongFunction;

/**
 * Event type DAO.
 */
public interface EventTypeDao {
    List<EventType> getEventTypes();

    EventType getEventTypeById(long id);

    EventType getEventTypeByEvent(Event event);

    <T> List<T> getItemsByEventTypeIds(List<Long> eventTypeIds,
                                       LongFunction<List<T>> eventTypeConferenceFunction,
                                       Function<Void, List<T>> resultFunction);
}
