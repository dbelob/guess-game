package guess.dto.statistics.olap;

import guess.domain.Language;
import guess.domain.source.Speaker;
import guess.domain.statistics.olap.OlapEntityStatistics;

import java.util.List;

/**
 * OLAP speaker statistics DTO.
 */
public class OlapSpeakerStatisticsDto extends OlapEntityStatisticsDto<Integer, OlapSpeakerMetricsDto> {
    public OlapSpeakerStatisticsDto(List<Integer> dimensionValues, List<OlapSpeakerMetricsDto> metricsList,
                                    OlapEntityMetricsDto totals) {
        super(dimensionValues, metricsList, totals);
    }

    public static OlapSpeakerStatisticsDto convertToDto(OlapEntityStatistics<Integer, Speaker> speakerStatistics, Language language) {
        return new OlapSpeakerStatisticsDto(
                speakerStatistics.getDimensionValues(),
                OlapSpeakerMetricsDto.convertToDto(speakerStatistics.getMetricsList(), language),
                OlapEntityMetricsDto.convertToDto(speakerStatistics.getTotals()));
    }
}
