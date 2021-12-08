package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.statistics.company.CompanyStatistics;

import java.util.List;

/**
 * Company statistics DTO.
 */
public class CompanyStatisticsDto {
    private final List<CompanyMetricsDto> companyMetricsList;
    private final CompanyMetricsDto totals;

    public CompanyStatisticsDto(List<CompanyMetricsDto> companyMetricsList, CompanyMetricsDto totals) {
        this.companyMetricsList = companyMetricsList;
        this.totals = totals;
    }

    public List<CompanyMetricsDto> getCompanyMetricsList() {
        return companyMetricsList;
    }

    public CompanyMetricsDto getTotals() {
        return totals;
    }

    public static CompanyStatisticsDto convertToDto(CompanyStatistics companyStatistics, Language language) {
        return new CompanyStatisticsDto(
                CompanyMetricsDto.convertToDto(companyStatistics.companyMetricsList(), language),
                CompanyMetricsDto.convertToDto(companyStatistics.totals(), language));
    }
}
