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
import guess.domain.source.Talk;
import guess.util.QuestionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Question service implementation.
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    class EventDateMinTrackTime {
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
        // Find current and future events
        List<Event> eventsFromDate = eventDao.getEventsFromDate(dateTime.toLocalDate());

        // Only conferences
        List<Event> conferencesFromDate = eventsFromDate.stream()
                .filter(e -> e.getEventType().isEventTypeConference())
                .collect(Collectors.toList());

        if (conferencesFromDate.isEmpty()) {
            return null;
        } else {
            List<EventDateMinTrackTime> eventDateMinTrackTimeList = getConferenceDateMinTrackTimeList(conferencesFromDate);

            //TODO: implement

            // Sort by start date
            conferencesFromDate.sort(Comparator.comparing(Event::getStartDate));

            // Find first start date
            LocalDate firstStartDate = conferencesFromDate.get(0).getStartDate();

            if (dateTime.toLocalDate().isBefore(firstStartDate)) {
                // No current events
                //...
            } else {
                // Current events exist
                //...
            }

            // Find events for first start date
            List<Event> eventsForFirstStartDate = conferencesFromDate.stream()
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

    private List<EventDateMinTrackTime> getConferenceDateMinTrackTimeList(List<Event> events) {
        List<EventDateMinTrackTime> result = new ArrayList<>();
        Map<Event, Map<Long, Optional<LocalTime>>> minTrackTimeInTalkDaysForConferences = new HashMap<>();

        // Calculate start time minimum for each days of each event
        for (Event event : events) {
            List<Talk> talks = event.getTalks();
            Map<Long, Optional<LocalTime>> minStartTimeInTalkDays = talks.stream()
                    .collect(
                            Collectors.groupingBy(
                                    Talk::getTalkDay,
                                    Collectors.mapping(
                                            Talk::getTrackTime,
                                            Collectors.minBy(Comparator.naturalOrder())
                                    )
                            )
                    );

            // Fill map (event, (trackTime, minTrackTime))
            minTrackTimeInTalkDaysForConferences.put(event, minStartTimeInTalkDays);
        }

        // Transform to (event, day, minTrackTime) list
        for (Event event : minTrackTimeInTalkDaysForConferences.keySet()) {
            if ((event.getStartDate() != null) && (event.getEndDate() != null) && (!event.getStartDate().isAfter(event.getEndDate()))) {
                long days = DAYS.between(event.getStartDate(), event.getEndDate()) + 1;
                Map<Long, Optional<LocalTime>> minTrackTimeInTalkDays = minTrackTimeInTalkDaysForConferences.get(event);

                for (long i = 1; i <= days; i++) {
                    LocalTime minTrackTime = LocalTime.of(0, 0, 0);

                    if (minTrackTimeInTalkDays != null) {
                        Optional<LocalTime> minTrackTimeInTalkDay = minTrackTimeInTalkDays.get(i);

                        if (minTrackTimeInTalkDay.isPresent()) {
                            minTrackTime = minTrackTimeInTalkDay.get();
                        }
                    }

                    LocalDate date = event.getStartDate().plusDays(i - 1);

                    result.add(new EventDateMinTrackTime(event, date, minTrackTime));
                }
            }
        }

        return result;
    }

    @Override
    public List<Integer> getQuantities(List<Long> questionSetIds, GuessType guessType) throws QuestionSetNotExistsException {
        List<Question> uniqueQuestions = questionDao.getQuestionByIds(questionSetIds, guessType);

        return QuestionUtils.getQuantities(uniqueQuestions.size());
    }
}
