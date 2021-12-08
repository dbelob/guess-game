package guess.domain.statistics.olap;

import guess.domain.source.Company;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;

/**
 * OLAP statistics.
 */
public record OlapStatistics(OlapEntityStatistics<Integer, EventType> eventTypeStatistics,
                             OlapEntityStatistics<Integer, Speaker> speakerStatistics,
                             OlapEntityStatistics<Integer, Company> companyStatistics) {
}
