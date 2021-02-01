package guess.service;

import guess.domain.source.Event;
import guess.domain.source.Talk;

import java.util.List;

/**
 * Event service.
 */
public interface EventService {
    Event getEventById(long id);

    List<Event> getEvents(boolean isConferences, boolean isMeetups, Long eventTypeId);

    Event getDefaultEvent(boolean isConferences, boolean isMeetups);

    Event getDefaultEvent2(boolean isConferences, boolean isMeetups);

    Event getEventByTalk(Talk talk);
}
