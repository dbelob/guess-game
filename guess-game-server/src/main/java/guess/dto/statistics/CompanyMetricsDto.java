package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.statistics.AbstractSpeakerCompanyMetrics;
import guess.domain.statistics.company.CompanyMetrics;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.Objects;

/**
 * Company metrics DTO.
 */
public class CompanyMetricsDto extends AbstractSpeakerCompanyMetrics {
    private final long id;
    private final String name;
    private final long speakersQuantity;

    public CompanyMetricsDto(long id, String name, CompanyMetrics companyMetrics) {
        super(companyMetrics.getTalksQuantity(), companyMetrics.getEventsQuantity(), companyMetrics.getEventTypesQuantity(),
                companyMetrics.getJavaChampionsQuantity(), companyMetrics.getMvpsQuantity());

        this.id = id;
        this.name = name;
        this.speakersQuantity = companyMetrics.getSpeakersQuantity();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getSpeakersQuantity() {
        return speakersQuantity;
    }

    public static CompanyMetricsDto convertToDto(CompanyMetrics companyMetrics, Language language) {
        var company = companyMetrics.getCompany();
        var name = LocalizationUtils.getString(company.getName(), language);

        return new CompanyMetricsDto(
                company.getId(),
                name,
                companyMetrics);
    }

    public static List<CompanyMetricsDto> convertToDto(List<CompanyMetrics> companyMetricsList, Language language) {
        return companyMetricsList.stream()
                .map(cm -> convertToDto(cm, language))
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyMetricsDto)) return false;
        if (!super.equals(o)) return false;
        CompanyMetricsDto that = (CompanyMetricsDto) o;
        return id == that.id &&
                speakersQuantity == that.speakersQuantity &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, speakersQuantity);
    }
}
