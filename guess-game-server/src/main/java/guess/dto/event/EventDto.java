package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.Place;
import guess.util.LocalizationUtils;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Event DTO.
 */
public class EventDto extends EventBriefDto {
    private final String siteLink;
    private final String youtubeLink;

    private final String placeCity;
    private final String placeVenueAddress;
    private final String mapCoordinates;

    private final String logoFileName;
    private final long duration;

    public EventDto(EventBriefDto eventBriefDto, String siteLink, String youtubeLink, String placeCity, String placeVenueAddress,
                    String mapCoordinates, String logoFileName, long duration) {
        super(eventBriefDto.getId(), eventBriefDto.getEventTypeId(), eventBriefDto.getName(), eventBriefDto.getStartDate(),
                eventBriefDto.getEndDate());

        this.siteLink = siteLink;
        this.youtubeLink = youtubeLink;
        this.placeCity = placeCity;
        this.placeVenueAddress = placeVenueAddress;
        this.mapCoordinates = mapCoordinates;
        this.logoFileName = logoFileName;
        this.duration = duration;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public String getPlaceCity() {
        return placeCity;
    }

    public String getPlaceVenueAddress() {
        return placeVenueAddress;
    }

    public String getMapCoordinates() {
        return mapCoordinates;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public long getDuration() {
        return duration;
    }

    public static EventDto convertToDto(Event event, Language language) {
        Place place = event.getPlace();
        String placeCity = (place != null) ? LocalizationUtils.getString(place.getCity(), language) : null;
        String placeVenueAddress = (place != null) ? LocalizationUtils.getString(place.getVenueAddress(), language) : null;
        String mapCoordinates = (place != null) ? place.getMapCoordinates() : null;
        String logoFileName = (event.getEventType() != null) ? event.getEventType().getLogoFileName() : null;
        long duration = (ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1);

        return new EventDto(
                convertToBriefDto(event, language),
                LocalizationUtils.getString(event.getSiteLink(), language),
                event.getYoutubeLink(),
                placeCity,
                placeVenueAddress,
                mapCoordinates,
                logoFileName,
                duration);
    }

    public static List<EventDto> convertToDto(List<Event> events, Language language) {
        return events.stream()
                .map(e -> convertToDto(e, language))
                .collect(Collectors.toList());
    }
}
