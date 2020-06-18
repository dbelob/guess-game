package guess.dto.speaker;

import guess.domain.Language;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Speaker DTO.
 */
public class SpeakerDto extends SpeakerBriefDto {
    private final String name;
    private final String bio;

    public SpeakerDto(SpeakerBriefDto speakerBriefDto, String name, String bio) {
        super(speakerBriefDto.getId(), speakerBriefDto.getFileName(), speakerBriefDto.getDisplayName(),
                speakerBriefDto.getCompany(), speakerBriefDto.getTwitter(), speakerBriefDto.getGitHub(),
                speakerBriefDto.isJavaChampion(), speakerBriefDto.isMvp(), speakerBriefDto.isMvpReconnect(),
                speakerBriefDto.isAnyMvp());

        this.name = name;
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public static SpeakerDto convertToDto(Speaker speaker, Language language) {
        return new SpeakerDto(
                convertToBriefDto(speaker, language),
                LocalizationUtils.getString(speaker.getName(), language),
                LocalizationUtils.getString(speaker.getBio(), language));
    }

    public static List<SpeakerDto> convertToDto(List<Speaker> speakers, Language language) {
        return speakers.stream()
                .map(s -> convertToDto(s, language))
                .collect(Collectors.toList());
    }
}
