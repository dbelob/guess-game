package guess.domain.statistics.company;

import guess.domain.source.Company;
import guess.domain.statistics.AbstractSpeakerCompanyMetrics;

import java.util.Objects;

/**
 * Company metrics.
 */
public class CompanyMetrics extends AbstractSpeakerCompanyMetrics {
    private final Company company;
    private final long speakersQuantity;

    public CompanyMetrics(Company company, long speakersQuantity, long talksQuantity, long eventsQuantity,
                          long eventTypesQuantity, long javaChampionsQuantity, long mvpsQuantity) {
        super(talksQuantity, eventsQuantity, eventTypesQuantity, javaChampionsQuantity, mvpsQuantity);

        this.company = company;
        this.speakersQuantity = speakersQuantity;
    }

    public Company getCompany() {
        return company;
    }

    public long getSpeakersQuantity() {
        return speakersQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyMetrics)) return false;
        if (!super.equals(o)) return false;
        CompanyMetrics that = (CompanyMetrics) o;
        return speakersQuantity == that.speakersQuantity &&
                Objects.equals(company, that.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), company, speakersQuantity);
    }

    @Override
    public String toString() {
        return "CompanyMetrics{" +
                "company=" + company +
                ", speakersQuantity=" + speakersQuantity +
                '}';
    }
}
