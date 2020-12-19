package guess.domain.source.load;

import guess.domain.source.NameCompany;
import guess.domain.source.Speaker;

import java.util.Map;
import java.util.Set;

/**
 * Speaker load maps.
 */
public class SpeakerLoadMaps {
    private final Map<NameCompany, Long> knownSpeakerIdsMap;
    private final Map<Long, Speaker> resourceSpeakerIdsMap;
    private final Map<NameCompany, Speaker> resourceNameCompanySpeakers;
    private final Map<String, Set<Speaker>> resourceNameSpeakers;

    public SpeakerLoadMaps(Map<NameCompany, Long> knownSpeakerIdsMap,
                           Map<Long, Speaker> resourceSpeakerIdsMap,
                           Map<NameCompany, Speaker> resourceNameCompanySpeakers,
                           Map<String, Set<Speaker>> resourceNameSpeakers) {
        this.knownSpeakerIdsMap = knownSpeakerIdsMap;
        this.resourceSpeakerIdsMap = resourceSpeakerIdsMap;
        this.resourceNameCompanySpeakers = resourceNameCompanySpeakers;
        this.resourceNameSpeakers = resourceNameSpeakers;
    }

    public Map<NameCompany, Long> getKnownSpeakerIdsMap() {
        return knownSpeakerIdsMap;
    }

    public Map<Long, Speaker> getResourceSpeakerIdsMap() {
        return resourceSpeakerIdsMap;
    }

    public Map<NameCompany, Speaker> getResourceNameCompanySpeakers() {
        return resourceNameCompanySpeakers;
    }

    public Map<String, Set<Speaker>> getResourceNameSpeakers() {
        return resourceNameSpeakers;
    }
}
