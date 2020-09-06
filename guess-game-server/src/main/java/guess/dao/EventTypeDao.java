package guess.dao;

import guess.domain.source.Event;
import guess.domain.source.EventType;

import java.util.Collections;
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

    static <T> List<T> getItemsByEventTypeIds(List<Long> eventTypeIds,
                                              LongFunction<List<T>> eventTypeConferenceFunction,
                                              Function<Void, List<T>> resultFunction,
                                              EventTypeDao eventTypeDao) {
        if (eventTypeIds.isEmpty()) {
            return Collections.emptyList();
        } else {
            if (eventTypeIds.size() == 1) {
                Long eventTypeId = eventTypeIds.get(0);

                if (eventTypeId == null) {
                    return Collections.emptyList();
                }

                EventType eventType = eventTypeDao.getEventTypeById(eventTypeId);

                if (eventType.isEventTypeConference()) {
                    return eventTypeConferenceFunction.apply(eventTypeId);
                }
            }

            return resultFunction.apply(null);
        }
    }
}
