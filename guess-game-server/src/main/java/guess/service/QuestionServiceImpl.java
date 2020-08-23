package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.dao.QuestionDao;
import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.question.Question;
import guess.domain.source.Event;
import guess.domain.source.LocaleItem;
import guess.domain.source.Place;
import guess.util.LocalizationUtils;
import guess.util.QuestionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Question service implementation.
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    /**
     * Event, date, minimal track time.
     */
    static class EventDateMinTrackTime {
        private final Event event;
        private final LocalDate date;
        private final LocalTime minTrackTime;

        public EventDateMinTrackTime(Event event, LocalDate date, LocalTime minTrackTime) {
            this.event = event;
            this.date = date;
            this.minTrackTime = minTrackTime;
        }

        public Event getEvent() {
            return event;
        }

        public LocalDate getDate() {
            return date;
        }

        public LocalTime getMinTrackTime() {
            return minTrackTime;
        }
    }

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
    public List<Event> getEvents(List<Long> eventTypeIds) {
        return eventTypeDao.getItemsByEventTypeIds(eventTypeIds,
                eventDao::getEventsByEventTypeId,
                v -> {
                    final String ALL_EVENTS_OPTION_TEXT = "allEventsOptionText";

                    List<LocaleItem> name = new ArrayList<>();
                    name.add(new LocaleItem(
                            Language.ENGLISH.getCode(),
                            LocalizationUtils.getResourceString(ALL_EVENTS_OPTION_TEXT, Language.ENGLISH)));
                    name.add(new LocaleItem(
                            Language.RUSSIAN.getCode(),
                            LocalizationUtils.getResourceString(ALL_EVENTS_OPTION_TEXT, Language.RUSSIAN)));

                    return Collections.singletonList(
                            new Event(
                                    -1L,
                                    null,
                                    name,
                                    new Event.EventDates(
                                            null,
                                            null
                                    ),
                                    new Event.EventLinks(
                                            null,
                                            null
                                    ),
                                    new Place(
                                            -1L,
                                            null,
                                            null,
                                            null
                                    ),
                                    Collections.emptyList()
                            )
                    );
                });
    }

    @Override
    public List<Integer> getQuantities(List<Long> eventTypeIds, List<Long> eventIds, GuessMode guessMode) throws QuestionSetNotExistsException {
        List<Question> uniqueQuestions = questionDao.getQuestionByIds(eventTypeIds, eventIds, guessMode);

        return QuestionUtils.getQuantities(uniqueQuestions.size());
    }
}
