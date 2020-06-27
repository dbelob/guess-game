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

    public SpeakerDto(SpeakerSuperBriefDto speakerSuperBriefDto, SpeakerBriefDto speakerBriefDto, String name, String bio) {
        super(speakerSuperBriefDto, speakerBriefDto.getPhotoFileName(), speakerBriefDto.getCompany(),
                speakerBriefDto.getTwitter(), speakerBriefDto.getGitHub(), speakerBriefDto.isJavaChampion(),
                speakerBriefDto.isMvp(), speakerBriefDto.isMvpReconnect(), speakerBriefDto.isAnyMvp());

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
        SpeakerSuperBriefDto speakerSuperBriefDto = convertToSuperBriefDto(speaker, language);

        return new SpeakerDto(
                speakerSuperBriefDto,
                convertToBriefDto(speakerSuperBriefDto, speaker, language),
                LocalizationUtils.getString(speaker.getName(), language),
                LocalizationUtils.getString(speaker.getBio(), language));
    }

    public static List<SpeakerDto> convertToDto(List<Speaker> speakers, Language language) {
        return speakers.stream()
                .map(s -> convertToDto(s, language))
                .collect(Collectors.toList());
    }
}
