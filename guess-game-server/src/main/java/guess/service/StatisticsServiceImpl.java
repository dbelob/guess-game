package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.statistics.EventMetrics;
import guess.domain.statistics.EventStatistics;
import guess.domain.statistics.EventTypeMetrics;
import guess.domain.statistics.EventTypeStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Statistics service implementation.
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final EventTypeDao eventTypeDao;
    private final EventDao eventDao;

    @Autowired
    public StatisticsServiceImpl(EventTypeDao eventTypeDao, EventDao eventDao) {
        this.eventTypeDao = eventTypeDao;
        this.eventDao = eventDao;
    }

    @Override
    public EventTypeStatistics getEventTypeStatistics(boolean isConferences, boolean isMeetups) {
        List<EventType> eventTypes = eventTypeDao.getEventTypes().stream()
                .filter(et -> ((isConferences && et.isEventTypeConference() || (isMeetups && !et.isEventTypeConference()))))
                .collect(Collectors.toList());
        List<EventTypeMetrics> eventTypeMetricsList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate totalsStartDate = currentDate;
        long totalsDuration = 0;
        long totalsEventsQuantity = 0;
        long totalsTalksQuantity = 0;
        Set<Speaker> totalsSpeakers = new HashSet<>();

        for (EventType eventType : eventTypes) {
            // Event type metrics
            LocalDate eventTypeStartDate = currentDate;
            long eventTypeDuration = 0;
            long eventTypeTalksQuantity = 0;
            Set<Speaker> eventTypeSpeakers = new HashSet<>();

            for (Event event : eventType.getEvents()) {
                if (event.getStartDate().isBefore(eventTypeStartDate)) {
                    eventTypeStartDate = event.getStartDate();
                }

                eventTypeDuration += (ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1);
                eventTypeTalksQuantity += event.getTalks().size();
                event.getTalks().forEach(t -> eventTypeSpeakers.addAll(t.getSpeakers()));
            }

            eventTypeMetricsList.add(new EventTypeMetrics(
                    eventType,
                    eventTypeStartDate,
                    ChronoUnit.YEARS.between(eventTypeStartDate, currentDate),
                    eventTypeDuration,
                    eventType.getEvents().size(),
                    eventTypeTalksQuantity,
                    eventTypeSpeakers.size()));

            // Totals metrics
            if (eventTypeStartDate.isBefore(totalsStartDate)) {
                totalsStartDate = eventTypeStartDate;
            }

            totalsDuration += eventTypeDuration;
            totalsEventsQuantity += eventType.getEvents().size();
            totalsTalksQuantity += eventTypeTalksQuantity;
            totalsSpeakers.addAll(eventTypeSpeakers);
        }

        return new EventTypeStatistics(
                eventTypeMetricsList,
                new EventTypeMetrics(
                        new EventType(),
                        totalsStartDate,
                        ChronoUnit.YEARS.between(totalsStartDate, currentDate),
                        totalsDuration,
                        totalsEventsQuantity,
                        totalsTalksQuantity,
                        totalsSpeakers.size()));
    }

    @Override
    public EventStatistics getEventStatistics(Long eventTypeId) {
        List<Event> events = eventDao.getEvents().stream()
                .filter(e -> ((eventTypeId == null) || (e.getEventType().getId() == eventTypeId)))
                .collect(Collectors.toList());
        List<EventMetrics> eventMetricsList = new ArrayList<>();
        LocalDate totalsStartDate = LocalDate.now();
        long totalsDuration = 0;
        long totalsTalksQuantity = 0;
        Set<Speaker> totalsSpeakers = new HashSet<>();

        for (Event event : events) {
            // Event metrics
            long eventDuration = (ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1);
            long eventTalksQuantity = event.getTalks().size();
            Set<Speaker> eventSpeakers = new HashSet<>();

            event.getTalks().forEach(t -> eventSpeakers.addAll(t.getSpeakers()));

            eventMetricsList.add(new EventMetrics(
                    event,
                    event.getStartDate(),
                    eventDuration,
                    eventTalksQuantity,
                    eventSpeakers.size()));

            // Totals metrics
            if (event.getStartDate().isBefore(totalsStartDate)) {
                totalsStartDate = event.getStartDate();
            }

            totalsDuration += eventDuration;
            totalsTalksQuantity += eventTalksQuantity;
            totalsSpeakers.addAll(eventSpeakers);
        }

        return new EventStatistics(
                eventMetricsList,
                new EventMetrics(
                        new Event(),
                        totalsStartDate,
                        totalsDuration,
                        totalsTalksQuantity,
                        totalsSpeakers.size()));
    }

    @Override
    public List<EventType> getConferences() {
        return eventTypeDao.getEventTypes().stream()
                .filter(EventType::isEventTypeConference)
                .collect(Collectors.toList());
    }
}
