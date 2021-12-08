package guess.domain.source.load;

import guess.domain.source.NameCompany;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Load settings.
 */
public record LoadSettings(Map<NameCompany, Long> knownSpeakerIdsMap, Set<String> invalidTalksSet,
                           boolean ignoreDemoStage) {
    public static LoadSettings defaultSettings() {
        return new LoadSettings(
                Collections.emptyMap(),
                Collections.emptySet(),
                true);
    }

    public static LoadSettings knownSpeakerIdsMap(Map<NameCompany, Long> knownSpeakerIdsMap) {
        return new LoadSettings(
                knownSpeakerIdsMap,
                Collections.emptySet(),
                true);
    }

    public static LoadSettings invalidTalksSet(Set<String> invalidTalksSet) {
        return new LoadSettings(
                Collections.emptyMap(),
                invalidTalksSet,
                true);
    }

    public static LoadSettings ignoreDemoStage(boolean ignoreDemoStage) {
        return new LoadSettings(
                Collections.emptyMap(),
                Collections.emptySet(),
                ignoreDemoStage);
    }
}
