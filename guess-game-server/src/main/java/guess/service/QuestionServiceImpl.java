package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.dao.QuestionDao;
import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessType;
import guess.domain.question.Question;
import guess.domain.question.QuestionSet;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.util.QuestionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Question service implementation.
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionDao questionDao;
    private final EventTypeDao eventTypeDao;
    private final EventDao eventDao;

    @Autowired
    public QuestionServiceImpl(QuestionDao questionDao, EventTypeDao eventTypeDao, EventDao eventDao) {
        this.questionDao = questionDao;
        this.eventTypeDao = eventTypeDao;
        this.eventDao = eventDao;
    }

    @Override
    public List<QuestionSet> getQuestionSets() {
        return questionDao.getQuestionSets();
    }

    @Override
    public Long getDefaultQuestionSetId(LocalDate date) {
        return questionDao.getDefaultQuestionSetId(date);
    }

    @Override
    public List<EventType> getEventTypes() {
        return eventTypeDao.getEventTypes();
    }

    @Override
    public List<Event> getEvents(long eventTypeId) {
        return eventDao.getEvents(eventTypeId);
    }

    @Override
    public Event getDefaultEvent(LocalDateTime dateTime) {
        List<Event> eventsFromDate = eventDao.getEventsFromDate(dateTime.toLocalDate());

        if (eventsFromDate.isEmpty()) {
            return null;
        } else {
            // Sort by start date
            eventsFromDate.sort(Comparator.comparing(Event::getStartDate));

            // Find events for first start date
            LocalDate firstStartDate = eventsFromDate.get(0).getStartDate();
            List<Event> eventsForFirstStartDate = eventsFromDate.stream()
                    .filter(e -> e.getStartDate().equals(firstStartDate))
                    .collect(Collectors.toList());

            if (eventsForFirstStartDate.size() > 1) {
                //TODO: find event by their talks track times
                return eventsForFirstStartDate.get(0);
            } else if (eventsForFirstStartDate.size() == 1) {
                // Single event
                return eventsForFirstStartDate.get(0);
            }

            return null;
        }
    }

    @Override
    public List<Integer> getQuantities(List<Long> questionSetIds, GuessType guessType) throws QuestionSetNotExistsException {
        List<Question> uniqueQuestions = questionDao.getQuestionByIds(questionSetIds, guessType);

        return QuestionUtils.getQuantities(uniqueQuestions.size());
    }
}
