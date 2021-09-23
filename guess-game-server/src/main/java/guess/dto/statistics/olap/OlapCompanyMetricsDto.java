package guess.dto.statistics.olap;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.domain.statistics.olap.OlapEntityMetrics;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * OLAP company metrics DTO.
 */
public class OlapCompanyMetricsDto extends OlapEntityMetricsDto {
    public OlapCompanyMetricsDto(long id, String name, List<Long> measureValues, Long total) {
        super(id, name, measureValues, total);
    }

    public static OlapCompanyMetricsDto convertToDto(OlapEntityMetrics<Company> companyMetrics, Language language) {
        var company = companyMetrics.getEntity();
        var name = LocalizationUtils.getString(company.getName(), language);

        return new OlapCompanyMetricsDto(
                company.getId(),
                name,
                companyMetrics.getMeasureValues(),
                companyMetrics.getTotal());
    }

    public static List<OlapCompanyMetricsDto> convertToDto(List<OlapEntityMetrics<Company>> companyMetricsList, Language language) {
        return companyMetricsList.stream()
                .map(cm -> convertToDto(cm, language))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OlapCompanyMetricsDto)) return false;
        if (!super.equals(o)) return false;
        OlapCompanyMetricsDto that = (OlapCompanyMetricsDto) o;
        return getId() == that.getId() && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getName());
    }

    @Override
    public String toString() {
        return "OlapCompanyMetricsDto{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                '}';
    }
}
