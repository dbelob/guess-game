package guess.service;

import guess.domain.source.EventType;
import guess.domain.statistics.EventStatistics;
import guess.domain.statistics.EventTypeStatistics;

import java.util.List;

/**
 * Statistics service.
 */
public interface StatisticsService {
    EventTypeStatistics getEventTypeStatistics(boolean isConferences, boolean isMeetups);

    EventStatistics getEventStatistics(Long eventTypeId);

    List<EventType> getConferences();
}
