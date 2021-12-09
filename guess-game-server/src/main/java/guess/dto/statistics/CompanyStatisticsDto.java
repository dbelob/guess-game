package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.statistics.company.CompanyStatistics;

import java.util.List;

/**
 * Company statistics DTO.
 */
public record CompanyStatisticsDto(List<CompanyMetricsDto> companyMetricsList, CompanyMetricsDto totals) {
    public static CompanyStatisticsDto convertToDto(CompanyStatistics companyStatistics, Language language) {
        return new CompanyStatisticsDto(
                CompanyMetricsDto.convertToDto(companyStatistics.companyMetricsList(), language),
                CompanyMetricsDto.convertToDto(companyStatistics.totals(), language));
    }
}
