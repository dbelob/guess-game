package guess.service;

import guess.domain.statistics.EventTypeMetrics;

import java.util.List;

/**
 * Statistics service.
 */
public interface StatisticsService {
    List<EventTypeMetrics> getEventTypeMetrics(boolean isConferences, boolean isMeetups);
}
