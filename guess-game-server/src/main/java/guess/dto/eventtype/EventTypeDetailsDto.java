package guess.dto.eventtype;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.dto.event.EventBriefDto;

import java.util.List;

/**
 * Event type details DTO.
 */
public class EventTypeDetailsDto {
    private final EventTypeDto eventType;
    private final List<EventBriefDto> events;

    public EventTypeDetailsDto(EventTypeDto eventType, List<EventBriefDto> events) {
        this.eventType = eventType;
        this.events = events;
    }

    public EventTypeDto getEventType() {
        return eventType;
    }

    public List<EventBriefDto> getEvents() {
        return events;
    }

    public static EventTypeDetailsDto convertToDto(EventType eventType, List<Event> events, Language language) {
        return new EventTypeDetailsDto(
                EventTypeDto.convertToDto(eventType, language),
                EventBriefDto.convertToBriefDto(events, language));
    }
}
