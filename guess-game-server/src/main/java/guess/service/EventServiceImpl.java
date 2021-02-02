package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.domain.auxiliary.EventDateMinTrackTime;
import guess.domain.auxiliary.EventMinTrackTimeEndDayTime;
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
    private final EventTypeDao eventTypeDao;

    @Autowired
    public EventServiceImpl(EventDao eventDao, EventTypeDao eventTypeDao) {
        this.eventDao = eventDao;
        this.eventTypeDao = eventTypeDao;
    }

    @Override
    public Event getEventById(long id) {
        return eventDao.getEventById(id);
    }

    @Override
    public List<Event> getEvents(boolean isConferences, boolean isMeetups, Long eventTypeId) {
        return eventTypeDao.getEventTypes().stream()
                .filter(et -> ((isConferences && et.isEventTypeConference()) || (isMeetups && !et.isEventTypeConference())) &&
                        ((eventTypeId == null) || (et.getId() == eventTypeId)))
                .flatMap(et -> et.getEvents().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Event getDefaultEvent(boolean isConferences, boolean isMeetups) {
        return getDefaultEvent(isConferences, isMeetups, LocalDateTime.now(ZoneId.of(DateTimeUtils.MOSCOW_TIME_ZONE)));
    }

    @Override
    //TODO: rename
    public Event getDefaultEvent2(boolean isConferences, boolean isMeetups) {
        return getDefaultEvent2(isConferences, isMeetups, LocalDateTime.now(ZoneId.of("UTC")));
    }

    List<Event> getConferencesFromDate(boolean isConferences, boolean isMeetups, LocalDateTime dateTime) {
        // Find current and future events
        List<Event> eventsFromDate = eventDao.getEventsFromDateTime(dateTime);

        // Select conferences only
        return eventsFromDate.stream()
                .filter(e -> ((isConferences && e.getEventType().isEventTypeConference()) || (isMeetups && !e.getEventType().isEventTypeConference())))
                .collect(Collectors.toList());
    }

    //TODO: rename
    Event getDefaultEvent2(boolean isConferences, boolean isMeetups, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        // Find current and future conferences
        List<Event> conferencesFromDate = getConferencesFromDate(isConferences, isMeetups, dateTime);
        if (conferencesFromDate.isEmpty()) {
            // Conferences not exist
            return null;
        }

        // Find (event, date, minimal track time) items
        List<EventDateMinTrackTime> eventDateMinTrackTimeList = getEventDateMinTrackTimeList(conferencesFromDate);
        if (eventDateMinTrackTimeList.isEmpty()) {
            return null;
        }

        //Transform to (event, minimal track time, end date time) items
        List<EventMinTrackTimeEndDayTime> eventMinTrackTimeEndDayTimeList = getEventMinTrackTimeEndDayTimeList(eventDateMinTrackTimeList);

        //TODO: implement

        return null;
    }

    Event getDefaultEvent(boolean isConferences, boolean isMeetups, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        // Find current and future events
        List<Event> eventsFromDate = eventDao.getEventsFromDate(date);

        // Select conferences only
        List<Event> conferencesFromDate = eventsFromDate.stream()
                .filter(e -> ((isConferences && e.getEventType().isEventTypeConference()) || (isMeetups && !e.getEventType().isEventTypeConference())))
                .collect(Collectors.toList());
        if (conferencesFromDate.isEmpty()) {
            // Conferences not exist
            return null;
        }

        // Find (event, date, minimal track time) items
        List<EventDateMinTrackTime> eventDateMinTrackTimeList = getEventDateMinTrackTimeList(conferencesFromDate);
        if (eventDateMinTrackTimeList.isEmpty()) {
            return null;
        }

        // Find current and future event days, sort by date and minimal track time
        List<EventDateMinTrackTime> eventDateMinTrackTimeListFromDateOrdered = eventDateMinTrackTimeList.stream()
                .filter(e -> !e.getDate().isBefore(date))
                .sorted(Comparator.comparing(EventDateMinTrackTime::getDate).thenComparing(EventDateMinTrackTime::getMinTrackTime))
                .collect(Collectors.toList());
        if (eventDateMinTrackTimeListFromDateOrdered.isEmpty()) {
            return null;
        }

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
                // No happened day events, return nearest first event
                return eventDateMinTrackTimeListFromDateOrdered.get(0).getEvent();
            } else {
                // Return nearest last event
                return eventDateMinTrackTimeListOnCurrentDate.get(0).getEvent();
            }
        }
    }

    /**
     * Gets list of (event, date, minimal track time) items.
     *
     * @param events events
     * @return list of (event, date, minimal track time) items
     */
    List<EventDateMinTrackTime> getEventDateMinTrackTimeList(List<Event> events) {
        List<EventDateMinTrackTime> result = new ArrayList<>();
        Map<Event, Map<Long, Optional<LocalTime>>> minTrackTimeInTalkDaysForConferences = new LinkedHashMap<>();

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
        for (Map.Entry<Event, Map<Long, Optional<LocalTime>>> entry : minTrackTimeInTalkDaysForConferences.entrySet()) {
            Event event = entry.getKey();

            if ((event.getStartDate() != null) && (event.getEndDate() != null) && (!event.getStartDate().isAfter(event.getEndDate()))) {
                long days = ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1;
                Map<Long, Optional<LocalTime>> minTrackTimeInTalkDays = entry.getValue();

                for (long i = 1; i <= days; i++) {
                    LocalDate date = event.getStartDate().plusDays(i - 1);

                    Optional<LocalTime> localTimeOptional;
                    if (minTrackTimeInTalkDays.containsKey(i)) {
                        localTimeOptional = minTrackTimeInTalkDays.get(i);
                    } else {
                        localTimeOptional = Optional.empty();
                    }

                    LocalTime minTrackTime = localTimeOptional.orElse(LocalTime.of(0, 0));

                    result.add(new EventDateMinTrackTime(event, date, minTrackTime));
                }
            }
        }

        return result;
    }

    /**
     * Gets list of (event, minimal track time, end date time) items.
     *
     * @param eventDateMinTrackTimeList list of (event, date, minimal track time) items
     * @return list of (event, minimal track time, end date time) items
     */
    List<EventMinTrackTimeEndDayTime> getEventMinTrackTimeEndDayTimeList(List<EventDateMinTrackTime> eventDateMinTrackTimeList) {
        //TODO: implement
        return Collections.emptyList();
    }

    @Override
    public Event getEventByTalk(Talk talk) {
        return eventDao.getEventByTalk(talk);
    }
}
