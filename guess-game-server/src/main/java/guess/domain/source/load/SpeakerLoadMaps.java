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
    private final Map<NameCompany, Speaker> resourceRuNameCompanySpeakers;
    private final Map<NameCompany, Speaker> resourceEnNameCompanySpeakers;
    private final Map<String, Set<Speaker>> resourceRuNameSpeakers;
    private final Map<String, Set<Speaker>> resourceEnNameSpeakers;

    public SpeakerLoadMaps(Map<NameCompany, Long> knownSpeakerIdsMap,
                           Map<Long, Speaker> resourceSpeakerIdsMap,
                           Map<NameCompany, Speaker> resourceRuNameCompanySpeakers,
                           Map<NameCompany, Speaker> resourceEnNameCompanySpeakers,
                           Map<String, Set<Speaker>> resourceRuNameSpeakers,
                           Map<String, Set<Speaker>> resourceEnNameSpeakers) {
        this.knownSpeakerIdsMap = knownSpeakerIdsMap;
        this.resourceSpeakerIdsMap = resourceSpeakerIdsMap;
        this.resourceRuNameCompanySpeakers = resourceRuNameCompanySpeakers;
        this.resourceEnNameCompanySpeakers = resourceEnNameCompanySpeakers;
        this.resourceRuNameSpeakers = resourceRuNameSpeakers;
        this.resourceEnNameSpeakers = resourceEnNameSpeakers;
    }

    public Map<NameCompany, Long> getKnownSpeakerIdsMap() {
        return knownSpeakerIdsMap;
    }

    public Map<Long, Speaker> getResourceSpeakerIdsMap() {
        return resourceSpeakerIdsMap;
    }

    public Map<NameCompany, Speaker> getResourceRuNameCompanySpeakers() {
        return resourceRuNameCompanySpeakers;
    }

    public Map<NameCompany, Speaker> getResourceEnNameCompanySpeakers() {
        return resourceEnNameCompanySpeakers;
    }

    public Map<String, Set<Speaker>> getResourceRuNameSpeakers() {
        return resourceRuNameSpeakers;
    }

    public Map<String, Set<Speaker>> getResourceEnNameSpeakers() {
        return resourceEnNameSpeakers;
    }
}
