package guess.domain.statistics;

import java.util.List;
import java.util.Objects;

/**
 * Company statistics.
 */
public class CompanyStatistics {
    private final List<CompanyMetrics> companyMetricsList;
    private final CompanyMetrics totals;

    public CompanyStatistics(List<CompanyMetrics> companyMetricsList, CompanyMetrics totals) {
        this.companyMetricsList = companyMetricsList;
        this.totals = totals;
    }

    public List<CompanyMetrics> getCompanyMetricsList() {
        return companyMetricsList;
    }

    public CompanyMetrics getTotals() {
        return totals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyStatistics)) return false;
        CompanyStatistics that = (CompanyStatistics) o;
        return Objects.equals(companyMetricsList, that.companyMetricsList) &&
                Objects.equals(totals, that.totals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyMetricsList, totals);
    }

    @Override
    public String toString() {
        return "CompanyStatistics{" +
                "companyMetricsList=" + companyMetricsList +
                ", totals=" + totals +
                '}';
    }
}
