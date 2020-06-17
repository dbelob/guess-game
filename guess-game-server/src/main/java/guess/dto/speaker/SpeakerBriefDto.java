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
    private final String company;
    private final String twitter;
    private final String gitHub;
    private final boolean javaChampion;
    private final boolean mvp;
    private final boolean mvpReconnect;
    private final boolean anyMvp;

    public SpeakerBriefDto(long id, String fileName, String displayName, String company, String twitter, String gitHub,
                           boolean javaChampion, boolean mvp, boolean mvpReconnect, boolean anyMvp) {
        this.id = id;
        this.fileName = fileName;
        this.displayName = displayName;
        this.company = company;
        this.twitter = twitter;
        this.gitHub = gitHub;
        this.javaChampion = javaChampion;
        this.mvp = mvp;
        this.mvpReconnect = mvpReconnect;
        this.anyMvp = anyMvp;
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

    public String getCompany() {
        return company;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getGitHub() {
        return gitHub;
    }

    public boolean isJavaChampion() {
        return javaChampion;
    }

    public boolean isMvp() {
        return mvp;
    }

    public boolean isMvpReconnect() {
        return mvpReconnect;
    }

    public boolean isAnyMvp() {
        return anyMvp;
    }

    public static SpeakerBriefDto convertToBriefDto(Speaker speaker, Language language) {
        return new SpeakerBriefDto(
                speaker.getId(),
                speaker.getFileName(),
                LocalizationUtils.getString(speaker.getNameWithLastNameFirst(), language),
                LocalizationUtils.getString(speaker.getCompany(), language),
                speaker.getTwitter(),
                speaker.getGitHub(),
                speaker.isJavaChampion(),
                speaker.isMvp(),
                speaker.isMvpReconnect(),
                speaker.isAnyMvp());
    }

    public static List<SpeakerBriefDto> convertToBriefDto(List<Speaker> speakers, Language language) {
        return speakers.stream()
                .map(s -> convertToBriefDto(s, language))
                .collect(Collectors.toList());
    }
}
