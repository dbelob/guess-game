package guess.service;

import guess.dao.EventTypeDao;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public EventType getEventTypeByEvent(Event event) {
        return eventTypeDao.getEventTypeByEvent(event);
    }
}
