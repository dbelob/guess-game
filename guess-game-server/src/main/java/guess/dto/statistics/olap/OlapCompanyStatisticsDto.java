package guess.dto.statistics.olap;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.domain.statistics.olap.OlapEntityStatistics;

import java.util.List;

/**
 * OLAP company statistics DTO.
 */
public class OlapCompanyStatisticsDto extends OlapEntityStatisticsDto<Integer, OlapCompanyMetricsDto> {
    public OlapCompanyStatisticsDto(List<Integer> dimensionValues, List<OlapCompanyMetricsDto> metricsList) {
        super(dimensionValues, metricsList);
    }

    public static OlapCompanyStatisticsDto convertToDto(OlapEntityStatistics<Integer, Company> companyStatistics, Language language) {
        return new OlapCompanyStatisticsDto(
                companyStatistics.getDimensionValues(),
                OlapCompanyMetricsDto.convertToDto(companyStatistics.getMetricsList(), language));
    }
}
