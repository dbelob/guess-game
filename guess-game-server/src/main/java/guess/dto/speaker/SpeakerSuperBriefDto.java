package guess.dto.speaker;

import guess.domain.Language;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Speaker DTO (super brief).
 */
public class SpeakerSuperBriefDto {
    private final long id;
    private final String displayName;

    public SpeakerSuperBriefDto(long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SpeakerSuperBriefDto convertToSuperBriefDto(Speaker speaker, Language language, Set<Speaker> speakerDuplicates) {
        return new SpeakerSuperBriefDto(
                speaker.getId(),
                LocalizationUtils.getSpeakerNameWithLastNameFirst(speaker, language, speakerDuplicates));
    }

    public static SpeakerSuperBriefDto convertToSuperBriefDto(Speaker speaker, Language language) {
        return convertToSuperBriefDto(speaker, language, Collections.emptySet());
    }

    public static List<SpeakerSuperBriefDto> convertToSuperBriefDto(List<Speaker> speakers, Language language, Set<Speaker> speakerDuplicates) {
        return speakers.stream()
                .map(s -> convertToSuperBriefDto(s, language, speakerDuplicates))
                .toList();
    }

    public static List<SpeakerSuperBriefDto> convertToSuperBriefDto(List<Speaker> speakers, Language language) {
        return convertToSuperBriefDto(speakers, language, Collections.emptySet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpeakerSuperBriefDto)) return false;
        SpeakerSuperBriefDto that = (SpeakerSuperBriefDto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
