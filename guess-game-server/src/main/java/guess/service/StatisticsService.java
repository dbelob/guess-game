package guess.service;

import guess.domain.statistics.EventStatistics;
import guess.domain.statistics.EventTypeStatistics;

/**
 * Statistics service.
 */
public interface StatisticsService {
    EventTypeStatistics getEventTypeStatistics(boolean isConferences, boolean isMeetups);

    EventStatistics getEventStatistics(Long eventId);
}
