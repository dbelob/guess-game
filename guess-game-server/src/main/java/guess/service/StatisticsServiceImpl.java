package guess.service;

import guess.dao.EventTypeDao;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.statistics.EventTypeMetrics;
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
    public List<EventTypeMetrics> getEventTypeMetrics(boolean isConferences, boolean isMeetups) {
        List<EventType> eventTypes = eventTypeDao.getEventTypes().stream()
                .filter(et -> ((isConferences && et.isEventTypeConference() || (isMeetups && !et.isEventTypeConference()))))
                .collect(Collectors.toList());
        List<EventTypeMetrics> eventTypeMetricsList = new ArrayList<>();

        for (EventType eventType : eventTypes) {
            LocalDate currentDate = LocalDate.now();
            LocalDate startDate = currentDate;
            long duration = 0;
            long talksQuantity = 0;
            Set<Speaker> speakers = new HashSet<>();

            for (Event event : eventType.getEvents()) {
                if (event.getStartDate().isBefore(startDate)) {
                    startDate = event.getStartDate();
                }

                duration += (ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1);
                talksQuantity += event.getTalks().size();
                event.getTalks().forEach(t -> speakers.addAll(t.getSpeakers()));
            }

            eventTypeMetricsList.add(new EventTypeMetrics(
                    eventType,
                    startDate,
                    ChronoUnit.YEARS.between(startDate, currentDate),
                    duration,
                    eventType.getEvents().size(),
                    talksQuantity,
                    speakers.size()));
        }

        return eventTypeMetricsList;
    }
}
