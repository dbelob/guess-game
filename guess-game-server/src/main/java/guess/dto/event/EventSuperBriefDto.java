package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.util.LocalizationUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Event DTO (super brief).
 */
public class EventSuperBriefDto {
    private final long id;
    private final long eventTypeId;
    private final long organizerId;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public EventSuperBriefDto(long id, long eventTypeId, long organizerId, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.eventTypeId = eventTypeId;
        this.organizerId = organizerId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getId() {
        return id;
    }

    public long getEventTypeId() {
        return eventTypeId;
    }

    public long getOrganizerId() {
        return organizerId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public static EventSuperBriefDto convertToSuperBriefDto(Event event, Language language) {
        return new EventSuperBriefDto(
                event.getId(),
                event.getEventType().getId(),
                event.getEventType().getOrganizer().getId(),
                LocalizationUtils.getString(event.getName(), language),
                event.getStartDate(),
                event.getEndDate());
    }

    public static List<EventSuperBriefDto> convertToSuperBriefDto(List<Event> events, Language language) {
        return events.stream()
                .map(e -> convertToSuperBriefDto(e, language))
                .toList();
    }
}
