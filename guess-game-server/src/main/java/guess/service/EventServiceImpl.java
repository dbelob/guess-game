package guess.service;

import guess.dao.EventDao;
import guess.domain.source.Event;
import guess.domain.source.Talk;
import guess.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    public Event getDefaultEvent() {
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of(DateTimeUtils.EVENTS_ZONE_ID));
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
            List<QuestionServiceImpl.EventDateMinTrackTime> eventDateMinTrackTimeList = getConferenceDateMinTrackTimeList(conferencesFromDate);

            if (eventDateMinTrackTimeList.isEmpty()) {
                return null;
            } else {
                // Find current and future event days, sort by date and minimal track time
                List<QuestionServiceImpl.EventDateMinTrackTime> eventDateMinTrackTimeListFromDateOrdered = eventDateMinTrackTimeList.stream()
                        .filter(e -> !e.getDate().isBefore(date))
                        .sorted(Comparator.comparing(QuestionServiceImpl.EventDateMinTrackTime::getDate).thenComparing(QuestionServiceImpl.EventDateMinTrackTime::getMinTrackTime))
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
                        List<QuestionServiceImpl.EventDateMinTrackTime> eventDateMinTrackTimeListOnCurrentDate = eventDateMinTrackTimeListFromDateOrdered.stream()
                                .filter(e -> (e.getDate().equals(date) && !e.getMinTrackTime().isAfter(time)))
                                .sorted(Comparator.comparing(QuestionServiceImpl.EventDateMinTrackTime::getMinTrackTime).reversed())
                                .collect(Collectors.toList());

                        if (eventDateMinTrackTimeListOnCurrentDate.isEmpty()) {
                            // No happened day events, return nearest first event
                            return eventDateMinTrackTimeListFromDateOrdered.get(0).getEvent();
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
    private List<QuestionServiceImpl.EventDateMinTrackTime> getConferenceDateMinTrackTimeList(List<Event> events) {
        List<QuestionServiceImpl.EventDateMinTrackTime> result = new ArrayList<>();
        Map<Event, Map<Long, Optional<LocalTime>>> minTrackTimeInTalkDaysForConferences = new HashMap<>();

        // Calculate start time minimum for each days of each event
        for (Event event : events) {
            List<Talk> talks = event.getTalks();
            Map<Long, Optional<LocalTime>> minStartTimeInTalkDays = talks.stream()
                    .filter(t -> (t.getTalkDay() != null))
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
                long days = ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1;
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

                    result.add(new QuestionServiceImpl.EventDateMinTrackTime(event, date, minTrackTime));
                }
            }
        }

        return result;
    }

    @Override
    public Event getEventByTalk(Talk talk) {
        return eventDao.getEventByTalk(talk);
    }
}
