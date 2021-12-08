package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.statistics.speaker.SpeakerStatistics;

import java.util.Collections;
import java.util.List;

/**
 * Speaker statistics DTO.
 */
public class SpeakerStatisticsDto {
    private final List<SpeakerMetricsDto> speakerMetricsList;
    private final SpeakerMetricsDto totals;

    public SpeakerStatisticsDto(List<SpeakerMetricsDto> speakerMetricsList, SpeakerMetricsDto totals) {
        this.speakerMetricsList = speakerMetricsList;
        this.totals = totals;
    }

    public List<SpeakerMetricsDto> getSpeakerMetricsList() {
        return speakerMetricsList;
    }

    public SpeakerMetricsDto getTotals() {
        return totals;
    }

    public static SpeakerStatisticsDto convertToDto(SpeakerStatistics speakerStatistics, Language language) {
        return new SpeakerStatisticsDto(
                SpeakerMetricsDto.convertToDto(speakerStatistics.speakerMetricsList(), language),
                SpeakerMetricsDto.convertToDto(speakerStatistics.totals(), language, Collections.emptySet()));
    }
}
