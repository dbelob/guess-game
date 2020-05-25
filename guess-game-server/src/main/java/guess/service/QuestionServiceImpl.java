package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.dao.QuestionDao;
import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.question.Question;
import guess.domain.question.QuestionSet;
import guess.domain.source.*;
import guess.util.LocalizationUtils;
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
    public List<EventType> getEventTypes() {
        return eventTypeDao.getEventTypes();
    }

    @Override
    public List<Event> getEvents(List<Long> eventTypeIds) {
        final String ALL_EVENTS_OPTION_TEXT = "allEventsOptionText";

        if (eventTypeIds.isEmpty()) {
            return Collections.emptyList();
        } else {
            if (eventTypeIds.size() == 1) {
                Long eventTypeId = eventTypeIds.get(0);

                if (eventTypeId == null) {
                    return Collections.emptyList();
                }

                EventType eventType = eventTypeDao.getEventTypeById(eventTypeId);

                if (eventType == null) {
                    return Collections.emptyList();
                }

                if (eventType.isEventTypeConference()) {
                    return eventDao.getEvents(eventTypeId);
                }
            }

            List<LocaleItem> name = new ArrayList<>() {{
                add(new LocaleItem(
                        Language.ENGLISH.getCode(),
                        LocalizationUtils.getResourceString(ALL_EVENTS_OPTION_TEXT, Language.ENGLISH)));
                add(new LocaleItem(
                        Language.RUSSIAN.getCode(),
                        LocalizationUtils.getResourceString(ALL_EVENTS_OPTION_TEXT, Language.RUSSIAN)));
            }};

            return Collections.singletonList(
                    new Event(
                            -1L,
                            null,
                            name,
                            null,
                            null,
                            null,
                            null,
                            new Place(
                                    -1L,
                                    null,
                                    null,
                                    null
                            ),
                            Collections.emptyList()
                    )
            );
        }
    }

    @Override
    public Event getDefaultEvent(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        // Find current and future events
        List<Event> eventsFromDate = eventDao.getEventsFromDate(date);

        // Select conferences only
        List<Event> conferencesFromDate = eventsFromDate.stream()
                .filter(e -> e.getEventType().isEventTypeConference())
                .collect(Collectors.toList());

        if (conferencesFromDate.isEmpty()) {
            // Conferences not exist
            return null;
        } else {
            List<EventDateMinTrackTime> eventDateMinTrackTimeList = getConferenceDateMinTrackTimeList(conferencesFromDate);

            if (eventDateMinTrackTimeList.isEmpty()) {
                return null;
            } else {
                // Find current and future event days, sort by date and minimal track time
                List<EventDateMinTrackTime> eventDateMinTrackTimeListFromDateOrdered = eventDateMinTrackTimeList.stream()
                        .filter(e -> !e.getDate().isBefore(date))
                        .sorted(Comparator.comparing(EventDateMinTrackTime::getDate).thenComparing(EventDateMinTrackTime::getMinTrackTime))
                        .collect(Collectors.toList());

                if (eventDateMinTrackTimeListFromDateOrdered.isEmpty()) {
                    return null;
                } else {
                    // Find first date
                    LocalDate firstDate = eventDateMinTrackTimeListFromDateOrdered.get(0).getDate();

                    if (date.isBefore(firstDate)) {
                        // No current day events, return nearest first event
                        return eventDateMinTrackTimeListFromDateOrdered.get(0).getEvent();
                    } else {
                        // Current day events exist, find happened time, sort by reversed minimal track time
                        List<EventDateMinTrackTime> eventDateMinTrackTimeListOnCurrentDate = eventDateMinTrackTimeListFromDateOrdered.stream()
                                .filter(e -> (e.getDate().equals(date) && !e.getMinTrackTime().isAfter(time)))
                                .sorted(Comparator.comparing(EventDateMinTrackTime::getMinTrackTime).reversed())
                                .collect(Collectors.toList());

                        if (eventDateMinTrackTimeListOnCurrentDate.isEmpty()) {
                            return null;
                        } else {
                            // Return nearest last event
                            return eventDateMinTrackTimeListOnCurrentDate.get(0).getEvent();
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets list of (event, date, minimal track time) items.
     *
     * @param events events
     * @return list of (event, date, minimal track time) items
     */
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
                    LocalTime minTrackTime = LocalTime.of(0, 0);

                    if (minTrackTimeInTalkDays != null) {
                        Optional<LocalTime> minTrackTimeInTalkDay = minTrackTimeInTalkDays.get(i);

                        if ((minTrackTimeInTalkDay != null) && minTrackTimeInTalkDay.isPresent()) {
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
    public List<Integer> getQuantities(List<Long> eventTypeIds, List<Long> eventIds, GuessMode guessMode) throws QuestionSetNotExistsException {
        List<Question> uniqueQuestions = questionDao.getQuestionByIds(eventTypeIds, eventIds, guessMode);

        return QuestionUtils.getQuantities(uniqueQuestions.size());
    }
}
