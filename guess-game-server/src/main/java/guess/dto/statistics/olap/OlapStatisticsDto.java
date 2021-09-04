package guess.dto.statistics.olap;

import guess.domain.Language;
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
        return new OlapStatisticsDto(
                OlapEventTypeStatisticsDto.convertToDto(olapStatistics.getEventTypeStatistics(), language),
                OlapSpeakerStatisticsDto.convertToDto(olapStatistics.getSpeakerStatistics(), language),
                OlapCompanyStatisticsDto.convertToDto(olapStatistics.getCompanyStatistics(), language));
    }
}
