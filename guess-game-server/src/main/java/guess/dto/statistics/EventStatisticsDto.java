package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.statistics.event.EventStatistics;

import java.util.List;

/**
 * Event statistics DTO.
 */
public record EventStatisticsDto(List<EventMetricsDto> eventMetricsList, EventMetricsDto totals) {
    public static EventStatisticsDto convertToDto(EventStatistics eventStatistics, Language language) {
        return new EventStatisticsDto(
                EventMetricsDto.convertToDto(eventStatistics.eventMetricsList(), language),
                EventMetricsDto.convertToDto(eventStatistics.totals(), language));
    }
}
