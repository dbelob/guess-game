package guess.dto.speaker;

import guess.domain.Language;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Speaker DTO (brief).
 */
public class SpeakerBriefDto {
    private final long id;
    private final String fileName;
    private final String displayName;

    public SpeakerBriefDto(long id, String fileName, String displayName) {
        this.id = id;
        this.fileName = fileName;
        this.displayName = displayName;
    }

    public long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SpeakerBriefDto convertToBriefDto(Speaker speaker, Language language) {
        return new SpeakerBriefDto(
                speaker.getId(),
                speaker.getFileName(),
                LocalizationUtils.getSpeakerNameWithLastNameFirstWithCompany(speaker, language));
    }

    public static List<SpeakerBriefDto> convertToBriefDto(List<Speaker> speakers, Language language) {
        return speakers.stream()
                .map(s -> convertToBriefDto(s, language))
                .collect(Collectors.toList());
    }
}
