package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.util.LocalizationUtils;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Event DTO (brief).
 */
public class EventBriefDto extends EventSuperBriefDto {
    private final long duration;
    private final String placeCity;
    private final String placeVenueAddress;
    private final String eventTypeLogoFileName;

    public EventBriefDto(EventSuperBriefDto eventSuperBriefDto, long duration, String placeCity, String placeVenueAddress,
                         String eventTypeLogoFileName) {
        super(eventSuperBriefDto.getId(), eventSuperBriefDto.getEventTypeId(), eventSuperBriefDto.getOrganizerId(),
                eventSuperBriefDto.getName(), eventSuperBriefDto.getStartDate(), eventSuperBriefDto.getEndDate());
        this.duration = duration;
        this.placeCity = placeCity;
        this.placeVenueAddress = placeVenueAddress;
        this.eventTypeLogoFileName = eventTypeLogoFileName;
    }

    public long getDuration() {
        return duration;
    }

    public String getPlaceCity() {
        return placeCity;
    }

    public String getPlaceVenueAddress() {
        return placeVenueAddress;
    }

    public String getEventTypeLogoFileName() {
        return eventTypeLogoFileName;
    }

    public static EventBriefDto convertToBriefDto(EventSuperBriefDto eventSuperBriefDto, Event event, Language language) {
        long duration = (ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1);
        var place = event.getPlace();
        String placeCity = (place != null) ? LocalizationUtils.getString(place.getCity(), language) : null;
        String placeVenueAddress = (place != null) ? LocalizationUtils.getString(place.getVenueAddress(), language) : null;
        String logoFileName = (event.getEventType() != null) ? event.getEventType().getLogoFileName() : null;

        return new EventBriefDto(
                eventSuperBriefDto,
                duration,
                placeCity,
                placeVenueAddress,
                logoFileName);
    }

    public static EventBriefDto convertToBriefDto(Event event, Language language) {
        return convertToBriefDto(convertToSuperBriefDto(event, language), event, language);
    }

    public static List<EventBriefDto> convertToBriefDto(List<Event> events, Language language) {
        return events.stream()
                .map(e -> convertToBriefDto(e, language))
                .toList();
    }
}
