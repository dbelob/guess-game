package guess.service;

import guess.dao.EventDao;
import guess.domain.source.Event;
import guess.domain.source.Talk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Event service implementation.
 */
@Service
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;

    @Autowired
    public EventServiceImpl(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public Event getEventByTalk(Talk talk) {
        return eventDao.getEventByTalk(talk);
    }
}
