package guess.domain.statistics.olap;

import guess.domain.source.Company;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;

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
}
