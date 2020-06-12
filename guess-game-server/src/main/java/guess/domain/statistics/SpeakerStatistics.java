package guess.domain.statistics;

import java.util.List;

/**
 * Speaker statistics.
 */
public class SpeakerStatistics {
    private final List<SpeakerMetrics> speakerMetricsList;
    private final SpeakerMetrics totals;

    public SpeakerStatistics(List<SpeakerMetrics> speakerMetricsList, SpeakerMetrics totals) {
        this.speakerMetricsList = speakerMetricsList;
        this.totals = totals;
    }

    public List<SpeakerMetrics> getSpeakerMetricsList() {
        return speakerMetricsList;
    }

    public SpeakerMetrics getTotals() {
        return totals;
    }
}
