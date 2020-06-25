package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.Place;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Event DTO.
 */
public class EventDto extends EventBriefDto {
    private final String siteLink;
    private final String youtubeLink;
    private final String mapCoordinates;

    public EventDto(EventSuperBriefDto eventSuperBriefDto, EventBriefDto eventBriefDto, String siteLink,
                    String youtubeLink, String mapCoordinates) {
        super(eventSuperBriefDto, eventBriefDto.getDuration(), eventBriefDto.getPlaceCity(), eventBriefDto.getPlaceVenueAddress(),
                eventBriefDto.getEventTypeLogoFileName());

        this.siteLink = siteLink;
        this.youtubeLink = youtubeLink;
        this.mapCoordinates = mapCoordinates;
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

    public static EventDto convertToDto(Event event, Language language) {
        EventSuperBriefDto eventSuperBriefDto = convertToSuperBriefDto(event, language);
        Place place = event.getPlace();
        String mapCoordinates = (place != null) ? place.getMapCoordinates() : null;

        return new EventDto(
                convertToSuperBriefDto(event, language),
                convertToBriefDto(eventSuperBriefDto, event, language),
                LocalizationUtils.getString(event.getSiteLink(), language),
                event.getYoutubeLink(),
                mapCoordinates);
    }

    public static List<EventDto> convertToDto(List<Event> events, Language language) {
        return events.stream()
                .map(e -> convertToDto(e, language))
                .collect(Collectors.toList());
    }
}
