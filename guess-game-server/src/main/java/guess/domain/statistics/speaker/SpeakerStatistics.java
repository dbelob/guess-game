package guess.domain.statistics.speaker;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpeakerStatistics)) return false;
        SpeakerStatistics that = (SpeakerStatistics) o;
        return Objects.equals(speakerMetricsList, that.speakerMetricsList) &&
                Objects.equals(totals, that.totals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(speakerMetricsList, totals);
    }

    @Override
    public String toString() {
        return "SpeakerStatistics{" +
                "speakerMetricsList=" + speakerMetricsList +
                ", totals=" + totals +
                '}';
    }
}
