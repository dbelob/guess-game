package guess.dto.start;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Event type DTO.
 */
public class EventTypeDto {
    private final long id;
    private final boolean conference;
    private final String name;
    private final String description;

    private final String siteLink;
    private final String vkLink;
    private final String twitterLink;
    private final String facebookLink;
    private final String youtubeLink;
    private final String telegramLink;

    private final String logoFileName;

    public EventTypeDto(long id, boolean conference, String name, String description, String siteLink,
                        String vkLink, String twitterLink, String facebookLink, String youtubeLink, String telegramLink,
                        String logoFileName) {
        this.id = id;
        this.conference = conference;
        this.name = name;
        this.description = description;
        this.siteLink = siteLink;
        this.vkLink = vkLink;
        this.twitterLink = twitterLink;
        this.facebookLink = facebookLink;
        this.youtubeLink = youtubeLink;
        this.telegramLink = telegramLink;
        this.logoFileName = logoFileName;
    }

    public long getId() {
        return id;
    }

    public boolean isConference() {
        return conference;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public String getVkLink() {
        return vkLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public String getTelegramLink() {
        return telegramLink;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public static List<EventTypeDto> convertToDto(List<EventType> eventTypes, Language language) {
        return eventTypes.stream()
                .map(et -> new EventTypeDto(
                        et.getId(),
                        et.isEventTypeConference(),
                        LocalizationUtils.getString(et.getName(), language),
                        LocalizationUtils.getString(et.getDescription(), language),
                        LocalizationUtils.getString(et.getSiteLink(), language),
                        et.getVkLink(),
                        et.getTwitterLink(),
                        et.getFacebookLink(),
                        et.getYoutubeLink(),
                        et.getTelegramLink(),
                        et.getLogoFileName()
                ))
                .collect(Collectors.toList());
    }
}
