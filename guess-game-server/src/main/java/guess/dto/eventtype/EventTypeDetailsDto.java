package guess.dto.eventtype;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.dto.event.EventBriefDto;

import java.util.List;

/**
 * Event type details DTO.
 */
public record EventTypeDetailsDto(EventTypeDto eventType, List<EventBriefDto> events) {
    public static EventTypeDetailsDto convertToDto(EventType eventType, List<Event> events, Language language) {
        return new EventTypeDetailsDto(
                EventTypeDto.convertToDto(eventType, language),
                EventBriefDto.convertToBriefDto(events, language));
    }
}
