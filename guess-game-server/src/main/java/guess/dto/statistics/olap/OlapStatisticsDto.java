package guess.dto.statistics.olap;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.statistics.olap.OlapEntityStatistics;
import guess.domain.statistics.olap.OlapStatistics;

/**
 * OLAP statistics DTO.
 */
public record OlapStatisticsDto(OlapEventTypeStatisticsDto eventTypeStatistics,
                                OlapSpeakerStatisticsDto speakerStatistics,
                                OlapCompanyStatisticsDto companyStatistics) {
    public static OlapStatisticsDto convertToDto(OlapStatistics olapStatistics, Language language) {
        OlapEntityStatistics<Integer, EventType> eventTypeStatistics = olapStatistics.eventTypeStatistics();
        OlapEntityStatistics<Integer, Speaker> speakerStatistics = olapStatistics.speakerStatistics();
        OlapEntityStatistics<Integer, Company> companyStatistics = olapStatistics.companyStatistics();

        return new OlapStatisticsDto(
                (eventTypeStatistics != null) ? OlapEventTypeStatisticsDto.convertToDto(eventTypeStatistics, language) : null,
                (speakerStatistics != null) ? OlapSpeakerStatisticsDto.convertToDto(speakerStatistics, language) : null,
                (companyStatistics != null) ? OlapCompanyStatisticsDto.convertToDto(companyStatistics, language) : null);
    }
}
