package guess.domain.statistics.olap;

import guess.domain.source.Company;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;

import java.util.Objects;

/**
 * OLAP statistics.
 */
public class OlapStatistics {
    private final OlapEntityStatistics<Integer, EventType> eventTypeStatistics;
    private final OlapEntityStatistics<Integer, Speaker> speakerStatistics;
    private final OlapEntityStatistics<Integer, Company> companyStatistics;

    public OlapStatistics(OlapEntityStatistics<Integer, EventType> eventTypeStatistics,
                          OlapEntityStatistics<Integer, Speaker> speakerStatistics,
                          OlapEntityStatistics<Integer, Company> companyStatistics) {
        this.eventTypeStatistics = eventTypeStatistics;
        this.speakerStatistics = speakerStatistics;
        this.companyStatistics = companyStatistics;
    }

    public OlapEntityStatistics<Integer, EventType> getEventTypeStatistics() {
        return eventTypeStatistics;
    }

    public OlapEntityStatistics<Integer, Speaker> getSpeakerStatistics() {
        return speakerStatistics;
    }

    public OlapEntityStatistics<Integer, Company> getCompanyStatistics() {
        return companyStatistics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OlapStatistics)) return false;
        OlapStatistics that = (OlapStatistics) o;
        return Objects.equals(getEventTypeStatistics(), that.getEventTypeStatistics()) && Objects.equals(getSpeakerStatistics(), that.getSpeakerStatistics()) && Objects.equals(getCompanyStatistics(), that.getCompanyStatistics());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventTypeStatistics(), getSpeakerStatistics(), getCompanyStatistics());
    }

    @Override
    public String toString() {
        return "OlapStatistics{" +
                "eventTypeStatistics=" + eventTypeStatistics +
                ", speakerStatistics=" + speakerStatistics +
                ", companyStatistics=" + companyStatistics +
                '}';
    }
}
