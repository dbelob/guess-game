package guess.dto.eventtype;

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

    public EventTypeDto(EventTypeSuperBriefDto eventTypeSuperBriefDto, EventTypeBriefDto eventTypeBriefDto, String description,
                        String siteLink, String vkLink, String twitterLink, String facebookLink, String youtubeLink,
                        String telegramLink) {
        super(eventTypeSuperBriefDto, eventTypeBriefDto.getShortDescription());

        this.description = description;
        this.siteLink = siteLink;
        this.vkLink = vkLink;
        this.twitterLink = twitterLink;
        this.facebookLink = facebookLink;
        this.youtubeLink = youtubeLink;
        this.telegramLink = telegramLink;
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

    public static EventTypeDto convertToDto(EventType eventType, Language language) {
        EventTypeSuperBriefDto eventTypeSuperBriefDto = convertToSuperBriefDto(eventType, language);
        String description = LocalizationUtils.getString(eventType.getLongDescription(), language);

        if ((description == null) || description.isEmpty()) {
            description = LocalizationUtils.getString(eventType.getShortDescription(), language);
        }

        return new EventTypeDto(
                eventTypeSuperBriefDto,
                convertToBriefDto(eventTypeSuperBriefDto, eventType, language),
                description,
                LocalizationUtils.getString(eventType.getSiteLink(), language),
                eventType.getVkLink(),
                eventType.getTwitterLink(),
                eventType.getFacebookLink(),
                eventType.getYoutubeLink(),
                eventType.getTelegramLink());
    }

    public static List<EventTypeDto> convertToDto(List<EventType> eventTypes, Language language) {
        return eventTypes.stream()
                .map(et -> convertToDto(et, language))
                .collect(Collectors.toList());
    }
}
