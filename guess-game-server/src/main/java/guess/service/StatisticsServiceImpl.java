package guess.service;

import guess.dao.EventTypeDao;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
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

    @Autowired
    public StatisticsServiceImpl(EventTypeDao eventTypeDao) {
        this.eventTypeDao = eventTypeDao;
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
}
