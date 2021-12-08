package guess.dto.eventtype;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.util.LocalizationUtils;

import java.util.List;

/**
 * Event type DTO.
 */
public class EventTypeDto extends EventTypeBriefDto {
    public record EventTypeDtoSocialLinks(String vkLink, String twitterLink, String facebookLink, String telegramLink,
                                          String habrLink) {
    }

    public record EventTypeDtoLinks(String siteLink, String youtubeLink, String speakerdeckLink,
                                    EventTypeDtoSocialLinks socialLinks) {
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
        return links.socialLinks.vkLink;
    }

    public String getTwitterLink() {
        return links.socialLinks.twitterLink;
    }

    public String getFacebookLink() {
        return links.socialLinks.facebookLink;
    }

    public String getYoutubeLink() {
        return links.youtubeLink;
    }

    public String getTelegramLink() {
        return links.socialLinks.telegramLink;
    }

    public String getSpeakerdeckLink() {
        return links.speakerdeckLink;
    }

    public String getHabrLink() {
        return links.socialLinks.habrLink;
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
                        eventType.getYoutubeLink(),
                        eventType.getSpeakerdeckLink(),
                        new EventTypeDtoSocialLinks(
                                eventType.getVkLink(),
                                eventType.getTwitterLink(),
                                eventType.getFacebookLink(),
                                eventType.getTelegramLink(),
                                eventType.getHabrLink()
                        )
                ));
    }

    public static List<EventTypeDto> convertToDto(List<EventType> eventTypes, Language language) {
        return eventTypes.stream()
                .map(et -> convertToDto(et, language))
                .toList();
    }
}
