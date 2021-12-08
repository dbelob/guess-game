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
public class OlapStatisticsDto {
    private final OlapEntityStatisticsDto<Integer, OlapEventTypeMetricsDto> eventTypeStatistics;
    private final OlapEntityStatisticsDto<Integer, OlapSpeakerMetricsDto> speakerStatistics;
    private final OlapEntityStatisticsDto<Integer, OlapCompanyMetricsDto> companyStatistics;

    public OlapStatisticsDto(OlapEventTypeStatisticsDto eventTypeStatistics, OlapSpeakerStatisticsDto speakerStatistics,
                             OlapCompanyStatisticsDto companyStatistics) {
        this.eventTypeStatistics = eventTypeStatistics;
        this.speakerStatistics = speakerStatistics;
        this.companyStatistics = companyStatistics;
    }

    public OlapEntityStatisticsDto<Integer, OlapEventTypeMetricsDto> getEventTypeStatistics() {
        return eventTypeStatistics;
    }

    public OlapEntityStatisticsDto<Integer, OlapSpeakerMetricsDto> getSpeakerStatistics() {
        return speakerStatistics;
    }

    public OlapEntityStatisticsDto<Integer, OlapCompanyMetricsDto> getCompanyStatistics() {
        return companyStatistics;
    }

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
