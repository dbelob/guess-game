package guess.dao;

import guess.domain.source.Event;

import java.time.LocalDate;
import java.util.List;

/**
 * Event DAO.
 */
public interface EventDao {
    List<Event> getEvents();

    List<Event> getEvents(long eventTypeId);

    List<Event> getEventsFromDate(LocalDate date);
}
