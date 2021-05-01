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
    public static class EventTypeDtoLinks {
        private final String siteLink;
        private final String vkLink;
        private final String twitterLink;
        private final String facebookLink;
        private final String youtubeLink;
        private final String telegramLink;

        public EventTypeDtoLinks(String siteLink, String vkLink, String twitterLink, String facebookLink, String youtubeLink,
                                 String telegramLink) {
            this.siteLink = siteLink;
            this.vkLink = vkLink;
            this.twitterLink = twitterLink;
            this.facebookLink = facebookLink;
            this.youtubeLink = youtubeLink;
            this.telegramLink = telegramLink;
        }
    }

    private final String description;
    private final EventTypeDtoLinks links;

    public EventTypeDto(EventTypeSuperBriefDto eventTypeSuperBriefDto, EventTypeBriefDto eventTypeBriefDto, String description,
                        EventTypeDtoLinks links) {
        super(eventTypeSuperBriefDto, eventTypeBriefDto.getShortDescription(), eventTypeBriefDto.getOrganizerName());

        this.description = description;
        this.links = links;
    }

    public String getDescription() {
        return description;
    }

    public String getSiteLink() {
        return links.siteLink;
    }

    public String getVkLink() {
        return links.vkLink;
    }

    public String getTwitterLink() {
        return links.twitterLink;
    }

    public String getFacebookLink() {
        return links.facebookLink;
    }

    public String getYoutubeLink() {
        return links.youtubeLink;
    }

    public String getTelegramLink() {
        return links.telegramLink;
    }

    public static EventTypeDto convertToDto(EventType eventType, Language language) {
        var eventTypeSuperBriefDto = convertToSuperBriefDto(eventType, language);
        var description = LocalizationUtils.getString(eventType.getLongDescription(), language);

        if ((description == null) || description.isEmpty()) {
            description = LocalizationUtils.getString(eventType.getShortDescription(), language);
        }

        return new EventTypeDto(
                eventTypeSuperBriefDto,
                convertToBriefDto(eventTypeSuperBriefDto, eventType, language),
                description,
                new EventTypeDtoLinks(
                        LocalizationUtils.getString(eventType.getSiteLink(), language),
                        eventType.getVkLink(),
                        eventType.getTwitterLink(),
                        eventType.getFacebookLink(),
                        eventType.getYoutubeLink(),
                        eventType.getTelegramLink()
                ));
    }

    public static List<EventTypeDto> convertToDto(List<EventType> eventTypes, Language language) {
        return eventTypes.stream()
                .map(et -> convertToDto(et, language))
                .collect(Collectors.toList());
    }
}
