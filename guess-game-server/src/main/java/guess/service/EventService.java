package guess.service;

import guess.domain.source.Event;
import guess.domain.source.Talk;

/**
 * Event service.
 */
public interface EventService {
    Event getDefaultEvent();

    Event getEventByTalk(Talk talk);
}
