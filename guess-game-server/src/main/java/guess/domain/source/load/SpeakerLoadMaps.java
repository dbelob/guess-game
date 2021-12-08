package guess.domain.source.load;

import guess.domain.source.NameCompany;
import guess.domain.source.Speaker;

import java.util.Map;
import java.util.Set;

/**
 * Speaker load maps.
 */
public record SpeakerLoadMaps(Map<NameCompany, Long> knownSpeakerIdsMap,
                              Map<Long, Speaker> resourceSpeakerIdsMap,
                              Map<NameCompany, Speaker> resourceNameCompanySpeakers,
                              Map<String, Set<Speaker>> resourceNameSpeakers) {
}
