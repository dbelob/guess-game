package guess.domain.statistics;

import guess.domain.source.Speaker;

import java.util.HashSet;
import java.util.Set;

/**
 * Internal company metrics.
 */
public class CompanyMetricsInternal extends SpeakerMetricsInternal {
    private final Set<Speaker> speakers = new HashSet<>();

    public Set<Speaker> getSpeakers() {
        return speakers;
    }
}
