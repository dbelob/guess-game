package guess.service;

import guess.dao.EventTypeDao;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<EventType> getEventTypes(boolean isConferences, boolean isMeetups) {
        return eventTypeDao.getEventTypes().stream()
                .filter(et -> ((isConferences && et.isEventTypeConference()) || (isMeetups && !et.isEventTypeConference())))
                .collect(Collectors.toList());
    }

    @Override
    public EventType getEventTypeByEvent(Event event) {
        return eventTypeDao.getEventTypeByEvent(event);
    }
}
