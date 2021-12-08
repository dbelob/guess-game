package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.function.Function;

/**
 * Event DTO.
 */
public class EventDto extends EventBriefDto {
    public record EventDtoSocialLinks(String vkLink, String twitterLink, String facebookLink, String telegramLink,
                                      String habrLink) {
    }

    public record EventDtoLinks(String siteLink, String youtubeLink, String speakerdeckLink,
                                EventDtoSocialLinks socialLinks) {
    }

    private final EventDtoLinks links;
    private final String mapCoordinates;
    private final String description;

    public EventDto(EventSuperBriefDto eventSuperBriefDto, EventBriefDto eventBriefDto, EventDtoLinks links,
                    String mapCoordinates, String description) {
        super(eventSuperBriefDto, eventBriefDto.getDuration(), eventBriefDto.getPlaceCity(), eventBriefDto.getPlaceVenueAddress(),
                eventBriefDto.getEventTypeLogoFileName());

        this.links = links;
        this.mapCoordinates = mapCoordinates;
        this.description = description;
    }

    public String getSiteLink() {
        return links.siteLink;
    }

    public String getYoutubeLink() {
        return links.youtubeLink;
    }

    public String getMapCoordinates() {
        return mapCoordinates;
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

    public String getTelegramLink() {
        return links.socialLinks.telegramLink;
    }

    public String getSpeakerdeckLink() {
        return links.speakerdeckLink;
    }

    public String getHabrLink() {
        return links.socialLinks.habrLink;
    }

    public String getDescription() {
        return description;
    }

    public static EventDto convertToDto(Event event, Function<Event, EventType> eventEventTypeFunction, Language language) {
        var eventSuperBriefDto = convertToSuperBriefDto(event, language);
        var place = event.getPlace();
        String mapCoordinates = (place != null) ? place.getMapCoordinates() : null;
        var eventSiteLink = LocalizationUtils.getString(event.getSiteLink(), language);
        String eventYoutubeLink = event.getYoutubeLink();

        var eventType = eventEventTypeFunction.apply(event);
        String eventTypeSiteLink = (eventType != null) ? LocalizationUtils.getString(eventType.getSiteLink(), language) : null;
        String eventTypeYoutubeLink = (eventType != null) ? eventType.getYoutubeLink() : null;
        String eventTypeVkLink = (eventType != null) ? eventType.getVkLink() : null;
        String eventTypeTwitterLink = (eventType != null) ? eventType.getTwitterLink() : null;
        String eventTypeFacebookLink = (eventType != null) ? eventType.getFacebookLink() : null;
        String eventTypeTelegramLink = (eventType != null) ? eventType.getTelegramLink() : null;
        String eventTypeSpeakerdeckLink = (eventType != null) ? eventType.getSpeakerdeckLink() : null;
        String eventTypeHabrLink = (eventType != null) ? eventType.getHabrLink() : null;
        String description = (eventType != null) ? LocalizationUtils.getString(eventType.getShortDescription(), language) : null;

        return new EventDto(
                eventSuperBriefDto,
                convertToBriefDto(eventSuperBriefDto, event, language),
                new EventDtoLinks(
                        ((eventSiteLink != null) && !eventSiteLink.isEmpty()) ? eventSiteLink : eventTypeSiteLink,
                        ((eventYoutubeLink != null) && !eventYoutubeLink.isEmpty()) ? eventYoutubeLink : eventTypeYoutubeLink,
                        eventTypeSpeakerdeckLink,
                        new EventDtoSocialLinks(
                                eventTypeVkLink,
                                eventTypeTwitterLink,
                                eventTypeFacebookLink,
                                eventTypeTelegramLink,
                                eventTypeHabrLink
                        )
                ),
                mapCoordinates,
                description);
    }

    public static List<EventDto> convertToDto(List<Event> events, Function<Event, EventType> eventEventTypeFunction, Language language) {
        return events.stream()
                .map(e -> convertToDto(e, eventEventTypeFunction, language))
                .toList();
    }
}
