package guess.dto.statistics.olap;

import guess.domain.Language;
import guess.domain.statistics.olap.OlapEntityStatistics;
import guess.domain.statistics.olap.dimension.City;

import java.util.List;

/**
 * OLAP city statistics DTO.
 */
public class OlapCityStatisticsDto extends OlapEntityStatisticsDto<Integer, OlapCityMetricsDto> {
    public OlapCityStatisticsDto(List<Integer> dimensionValues, List<OlapCityMetricsDto> metricsList,
                                 OlapEntityMetricsDto totals) {
        super(dimensionValues, metricsList, totals);
    }

    public static OlapCityStatisticsDto convertToDto(OlapEntityStatistics<Integer, City> cityStatistics, Language language) {
        return new OlapCityStatisticsDto(
                cityStatistics.getDimensionValues(),
                OlapCityMetricsDto.convertToDto(cityStatistics.getMetricsList(), language),
                OlapEntityMetricsDto.convertToDto(cityStatistics.getTotals()));
    }
}
