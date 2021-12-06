package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.domain.auxiliary.EventDateMinTrackTime;
import guess.domain.auxiliary.EventMinTrackTimeEndDayTime;
import guess.domain.source.Event;
import guess.domain.source.Talk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
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
    public List<Event> getEvents(boolean isConferences, boolean isMeetups, Long organizerId, Long eventTypeId) {
        return eventTypeDao.getEventTypes().stream()
                .filter(et -> ((isConferences && et.isEventTypeConference()) || (isMeetups && !et.isEventTypeConference())) &&
                        ((organizerId == null) || (et.getOrganizer().getId() == organizerId)) &&
                        ((eventTypeId == null) || (et.getId() == eventTypeId)))
                .flatMap(et -> et.getEvents().stream())
                .toList();
    }

    @Override
    public Event getDefaultEvent(boolean isConferences, boolean isMeetups) {
        return getDefaultEvent(isConferences, isMeetups, LocalDateTime.now(ZoneId.of("UTC")));
    }

    List<Event> getEventsFromDateTime(boolean isConferences, boolean isMeetups, LocalDateTime dateTime) {
        // Find current and future events
        List<Event> eventsFromDate = eventDao.getEventsFromDateTime(dateTime);

        // Select conferences only
        return eventsFromDate.stream()
                .filter(e -> ((isConferences && e.getEventType().isEventTypeConference()) || (isMeetups && !e.getEventType().isEventTypeConference())))
                .collect(Collectors.toList());
    }

    Event getDefaultEvent(boolean isConferences, boolean isMeetups, LocalDateTime dateTime) {
        // Find current and future conferences
        List<Event> conferencesFromDate = getEventsFromDateTime(isConferences, isMeetups, dateTime);
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
        if (eventMinTrackTimeEndDayTimeList.isEmpty()) {
            return null;
        }

        // Find current and future event days, sort by minimal track date time and end day date time
        List<EventMinTrackTimeEndDayTime> eventMinTrackTimeEndDayTimeListFromDateOrdered = eventMinTrackTimeEndDayTimeList.stream()
                .filter(edt -> dateTime.isBefore(edt.endDayDateTime()))
                .sorted(Comparator.comparing(EventMinTrackTimeEndDayTime::minTrackDateTime).thenComparing(EventMinTrackTimeEndDayTime::endDayDateTime))
                .toList();
        if (eventMinTrackTimeEndDayTimeListFromDateOrdered.isEmpty()) {
            return null;
        }

        // Find first date
        LocalDateTime firstDateTime = eventMinTrackTimeEndDayTimeListFromDateOrdered.get(0).minTrackDateTime();

        if (dateTime.isBefore(firstDateTime)) {
            // No current day events, return nearest first event
            return eventMinTrackTimeEndDayTimeListFromDateOrdered.get(0).event();
        } else {
            // Current day events exist, find happened time, sort by reversed minimal track date time
            List<EventMinTrackTimeEndDayTime> eventMinTrackTimeEndDayTimeListOnCurrentDate = eventMinTrackTimeEndDayTimeListFromDateOrdered.stream()
                    .filter(edt -> !dateTime.isBefore(edt.minTrackDateTime()))
                    .sorted(Comparator.comparing(EventMinTrackTimeEndDayTime::minTrackDateTime).reversed())
                    .collect(Collectors.toList());

            // Return nearest last event
            return eventMinTrackTimeEndDayTimeListOnCurrentDate.get(0).event();
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
            var event = entry.getKey();

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

                    var minTrackTime = localTimeOptional.orElse(LocalTime.of(0, 0));

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
        return eventDateMinTrackTimeList.stream()
                .map(edt -> {
                    var minTrackDateTime = ZonedDateTime.of(
                                    edt.date(),
                                    edt.minTrackTime(),
                                    edt.event().getFinalTimeZoneId())
                            .withZoneSameInstant(ZoneId.of("UTC"))
                            .toLocalDateTime();
                    var endDayDateTime = ZonedDateTime.of(
                                    edt.date().plus(1, ChronoUnit.DAYS),
                                    LocalTime.of(0, 0, 0),
                                    edt.event().getFinalTimeZoneId())
                            .withZoneSameInstant(ZoneId.of("UTC"))
                            .toLocalDateTime();

                    return new EventMinTrackTimeEndDayTime(
                            edt.event(),
                            minTrackDateTime,
                            endDayDateTime
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public Event getEventByTalk(Talk talk) {
        return eventDao.getEventByTalk(talk);
    }
}
