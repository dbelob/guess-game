package guess.service;

import guess.domain.source.Event;
import guess.domain.source.Talk;

import java.util.List;

/**
 * Event service.
 */
public interface EventService {
    List<Event> getEvents(long eventTypeId);

    Event getDefaultEvent();

    Event getEventByTalk(Talk talk);
}
