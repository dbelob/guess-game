package guess.service;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessMode;
import guess.domain.source.Event;
import guess.domain.source.EventType;

import java.util.List;

/**
 * Question service.
 */
public interface QuestionService {
    List<EventType> getEventTypes();

    List<Event> getEvents(List<Long> eventTypeIds);

    Event getDefaultEvent();

    List<Integer> getQuantities(List<Long> eventTypeIds, List<Long> eventIds, GuessMode guessMode) throws QuestionSetNotExistsException;
}
