package guess.service;

import guess.dao.EventTypeDao;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Event type service implementation.
 */
@Service
public class EventTypeServiceImpl implements EventTypeService {
    private final EventTypeDao eventTypeDao;

    @Autowired
    public EventTypeServiceImpl(EventTypeDao eventTypeDao) {
        this.eventTypeDao = eventTypeDao;
    }

    @Override
    public EventType getEventTypeById(long id) {
        return eventTypeDao.getEventTypeById(id);
    }

    @Override
    public List<EventType> getEventTypes() {
        return eventTypeDao.getEventTypes();
    }

    @Override
    public List<EventType> getEventTypes(boolean isConferences, boolean isMeetups, Long organizerId) {
        return eventTypeDao.getEventTypes().stream()
                .filter(et -> ((isConferences && et.isEventTypeConference()) || (isMeetups && !et.isEventTypeConference())) &&
                        ((organizerId == null) || (et.getOrganizer().getId() == organizerId)))
                .toList();
    }

    @Override
    public EventType getEventTypeByEvent(Event event) {
        return eventTypeDao.getEventTypeByEvent(event);
    }
}
