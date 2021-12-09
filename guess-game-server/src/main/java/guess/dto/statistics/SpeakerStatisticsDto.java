package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.statistics.speaker.SpeakerStatistics;

import java.util.Collections;
import java.util.List;

/**
 * Speaker statistics DTO.
 */
public record SpeakerStatisticsDto(List<SpeakerMetricsDto> speakerMetricsList, SpeakerMetricsDto totals) {
    public static SpeakerStatisticsDto convertToDto(SpeakerStatistics speakerStatistics, Language language) {
        return new SpeakerStatisticsDto(
                SpeakerMetricsDto.convertToDto(speakerStatistics.speakerMetricsList(), language),
                SpeakerMetricsDto.convertToDto(speakerStatistics.totals(), language, Collections.emptySet()));
    }
}
