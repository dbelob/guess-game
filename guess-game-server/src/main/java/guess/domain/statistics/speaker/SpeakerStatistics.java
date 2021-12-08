package guess.domain.statistics.speaker;

import java.util.List;

/**
 * Speaker statistics.
 */
public record SpeakerStatistics(List<SpeakerMetrics> speakerMetricsList, SpeakerMetrics totals) {
}
