package guess.service;

import guess.domain.source.EventType;
import guess.domain.statistics.company.CompanyStatistics;
import guess.domain.statistics.event.EventStatistics;
import guess.domain.statistics.eventtype.EventTypeStatistics;
import guess.domain.statistics.speaker.SpeakerStatistics;

import java.util.List;

/**
 * Statistics service.
 */
public interface StatisticsService {
    List<EventType> getStatisticsEventTypes(boolean isConferences, boolean isMeetups, Long organizerId, Long eventTypeId);

    EventTypeStatistics getEventTypeStatistics(boolean isConferences, boolean isMeetups, Long organizerId);

    EventStatistics getEventStatistics(Long eventTypeId);

    SpeakerStatistics getSpeakerStatistics(boolean isConferences, boolean isMeetups, Long organizerId, Long eventTypeId);

    CompanyStatistics getCompanyStatistics(boolean isConferences, boolean isMeetups, Long organizerId, Long eventTypeId);

    List<EventType> getConferences();
}
