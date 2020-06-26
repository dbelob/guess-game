package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Place;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Event DTO.
 */
public class EventDto extends EventBriefDto {
    private final String siteLink;
    private final String youtubeLink;
    private final String mapCoordinates;

    private final String vkLink;
    private final String twitterLink;
    private final String facebookLink;
    private final String telegramLink;

    public EventDto(EventSuperBriefDto eventSuperBriefDto, EventBriefDto eventBriefDto, String siteLink, String youtubeLink,
                    String mapCoordinates, String vkLink, String twitterLink, String facebookLink, String telegramLink) {
        super(eventSuperBriefDto, eventBriefDto.getDuration(), eventBriefDto.getPlaceCity(), eventBriefDto.getPlaceVenueAddress(),
                eventBriefDto.getEventTypeLogoFileName());

        this.siteLink = siteLink;
        this.youtubeLink = youtubeLink;
        this.mapCoordinates = mapCoordinates;
        this.vkLink = vkLink;
        this.twitterLink = twitterLink;
        this.facebookLink = facebookLink;
        this.telegramLink = telegramLink;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public String getMapCoordinates() {
        return mapCoordinates;
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

    public String getTelegramLink() {
        return telegramLink;
    }

    public static EventDto convertToDto(Event event, Function<Event, EventType> eventEventTypeFunction, Language language) {
        EventSuperBriefDto eventSuperBriefDto = convertToSuperBriefDto(event, language);
        Place place = event.getPlace();
        String mapCoordinates = (place != null) ? place.getMapCoordinates() : null;
        String eventSiteLink = LocalizationUtils.getString(event.getSiteLink(), language);
        String eventYoutubeLink = event.getYoutubeLink();

        EventType eventType = eventEventTypeFunction.apply(event);
        String eventTypeSiteLink = (eventType != null) ? LocalizationUtils.getString(eventType.getSiteLink(), language) : null;
        String eventTypeYoutubeLink = (eventType != null) ? eventType.getYoutubeLink() : null;
        String eventTypeVkLink = (eventType != null) ? eventType.getVkLink() : null;
        String eventTypeTwitterLink = (eventType != null) ? eventType.getTwitterLink() : null;
        String eventTypeFacebookLink = (eventType != null) ? eventType.getFacebookLink() : null;
        String eventTypeTelegramLink = (eventType != null) ? eventType.getTelegramLink() : null;

        return new EventDto(
                eventSuperBriefDto,
                convertToBriefDto(eventSuperBriefDto, event, language),
                ((eventSiteLink != null) && !eventSiteLink.isEmpty()) ? eventSiteLink : eventTypeSiteLink,
                ((eventYoutubeLink != null) && !eventYoutubeLink.isEmpty()) ? eventYoutubeLink : eventTypeYoutubeLink,
                mapCoordinates,
                eventTypeVkLink,
                eventTypeTwitterLink,
                eventTypeFacebookLink,
                eventTypeTelegramLink);
    }

    public static List<EventDto> convertToDto(List<Event> events, Function<Event, EventType> eventEventTypeFunction, Language language) {
        return events.stream()
                .map(e -> convertToDto(e, eventEventTypeFunction, language))
                .collect(Collectors.toList());
    }
}
