package guess.domain.statistics.olap;

import guess.domain.source.Company;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;

/**
 * OLAP statistics.
 */
public class OlapStatistics {
    private final EntityOlapStatistics<Integer, EventType> eventTypeStatistics;
    private final EntityOlapStatistics<Integer, Speaker> speakerStatistics;
    private final EntityOlapStatistics<Integer, Company> companyStatistics;

    public OlapStatistics(EntityOlapStatistics<Integer, EventType> eventTypeStatistics,
                          EntityOlapStatistics<Integer, Speaker> speakerStatistics,
                          EntityOlapStatistics<Integer, Company> companyStatistics) {
        this.eventTypeStatistics = eventTypeStatistics;
        this.speakerStatistics = speakerStatistics;
        this.companyStatistics = companyStatistics;
    }

    public EntityOlapStatistics<Integer, EventType> getEventTypeStatistics() {
        return eventTypeStatistics;
    }

    public EntityOlapStatistics<Integer, Speaker> getSpeakerStatistics() {
        return speakerStatistics;
    }

    public EntityOlapStatistics<Integer, Company> getCompanyStatistics() {
        return companyStatistics;
    }
}
