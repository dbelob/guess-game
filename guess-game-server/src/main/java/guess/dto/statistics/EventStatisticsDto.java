package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.statistics.event.EventStatistics;

import java.util.List;

/**
 * Event statistics DTO.
 */
public class EventStatisticsDto {
    private final List<EventMetricsDto> eventMetricsList;
    private final EventMetricsDto totals;

    public EventStatisticsDto(List<EventMetricsDto> eventMetricsList, EventMetricsDto totals) {
        this.eventMetricsList = eventMetricsList;
        this.totals = totals;
    }

    public List<EventMetricsDto> getEventMetricsList() {
        return eventMetricsList;
    }

    public EventMetricsDto getTotals() {
        return totals;
    }

    public static EventStatisticsDto convertToDto(EventStatistics eventStatistics, Language language) {
        return new EventStatisticsDto(
                EventMetricsDto.convertToDto(eventStatistics.eventMetricsList(), language),
                EventMetricsDto.convertToDto(eventStatistics.totals(), language));
    }
}
