package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.statistics.eventtype.EventTypeStatistics;

import java.util.List;

/**
 * Event type statistics DTO.
 */
public class EventTypeStatisticsDto {
    private final List<EventTypeMetricsDto> eventTypeMetricsList;
    private final EventTypeMetricsDto totals;

    public EventTypeStatisticsDto(List<EventTypeMetricsDto> eventTypeMetricsList, EventTypeMetricsDto totals) {
        this.eventTypeMetricsList = eventTypeMetricsList;
        this.totals = totals;
    }

    public List<EventTypeMetricsDto> getEventTypeMetricsList() {
        return eventTypeMetricsList;
    }

    public EventTypeMetricsDto getTotals() {
        return totals;
    }

    public static EventTypeStatisticsDto convertToDto(EventTypeStatistics eventTypeStatistics, Language language) {
        return new EventTypeStatisticsDto(
                EventTypeMetricsDto.convertToDto(eventTypeStatistics.eventTypeMetricsList(), language),
                EventTypeMetricsDto.convertToDto(eventTypeStatistics.totals(), language));
    }
}
