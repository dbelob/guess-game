package guess.domain.statistics.company;

import java.util.List;

/**
 * Company statistics.
 */
public record CompanyStatistics(List<CompanyMetrics> companyMetricsList, CompanyMetrics totals) {
}
