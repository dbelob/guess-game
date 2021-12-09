package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.statistics.eventtype.EventTypeStatistics;

import java.util.List;

/**
 * Event type statistics DTO.
 */
public record EventTypeStatisticsDto(List<EventTypeMetricsDto> eventTypeMetricsList, EventTypeMetricsDto totals) {
    public static EventTypeStatisticsDto convertToDto(EventTypeStatistics eventTypeStatistics, Language language) {
        return new EventTypeStatisticsDto(
                EventTypeMetricsDto.convertToDto(eventTypeStatistics.eventTypeMetricsList(), language),
                EventTypeMetricsDto.convertToDto(eventTypeStatistics.totals(), language));
    }
}
