package guess.dto.start;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Event type DTO.
 */
public class EventTypeDto extends EventTypeBriefDto {
    private final String description;

    private final String siteLink;
    private final String vkLink;
    private final String twitterLink;
    private final String facebookLink;
    private final String youtubeLink;
    private final String telegramLink;

    private final String logoFileName;

    public EventTypeDto(EventTypeBriefDto eventTypeBriefDto, String description, String siteLink,
                        String vkLink, String twitterLink, String facebookLink, String youtubeLink, String telegramLink,
                        String logoFileName) {
        super(eventTypeBriefDto.getId(), eventTypeBriefDto.isConference(), eventTypeBriefDto.getName(), eventTypeBriefDto.getDisplayName());

        this.description = description;
        this.siteLink = siteLink;
        this.vkLink = vkLink;
        this.twitterLink = twitterLink;
        this.facebookLink = facebookLink;
        this.youtubeLink = youtubeLink;
        this.telegramLink = telegramLink;
        this.logoFileName = logoFileName;
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

    public static EventTypeDto convertToDto(EventType eventType, Language language) {
        return new EventTypeDto(
                convertToBriefDto(eventType, language),
                LocalizationUtils.getString(eventType.getDescription(), language),
                LocalizationUtils.getString(eventType.getSiteLink(), language),
                eventType.getVkLink(),
                eventType.getTwitterLink(),
                eventType.getFacebookLink(),
                eventType.getYoutubeLink(),
                eventType.getTelegramLink(),
                eventType.getLogoFileName());
    }

    public static List<EventTypeDto> convertToDto(List<EventType> eventTypes, Language language) {
        return eventTypes.stream()
                .map(et -> convertToDto(et, language))
                .collect(Collectors.toList());
    }
}
