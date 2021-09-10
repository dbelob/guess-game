package guess.dto.statistics.olap;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.domain.statistics.olap.OlapEntityStatistics;

import java.util.List;

/**
 * OLAP event type statistics DTO.
 */
public class OlapEventTypeStatisticsDto extends OlapEntityStatisticsDto<Integer, OlapEventTypeMetricsDto> {
    public OlapEventTypeStatisticsDto(List<Integer> dimensionValues, List<OlapEventTypeMetricsDto> metricsList,
                                      OlapEntityMetricsDto totals) {
        super(dimensionValues, metricsList, totals);
    }

    public static OlapEventTypeStatisticsDto convertToDto(OlapEntityStatistics<Integer, EventType> eventTypeStatistics, Language language) {
        return new OlapEventTypeStatisticsDto(
                eventTypeStatistics.getDimensionValues(),
                OlapEventTypeMetricsDto.convertToDto(eventTypeStatistics.getMetricsList(), language),
                OlapEntityMetricsDto.convertToDto(eventTypeStatistics.getTotals()));
    }
}
