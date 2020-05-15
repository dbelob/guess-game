package guess.dto.start;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Event type DTO.
 */
public class EventTypeBriefDto {
    private final long id;
    private final boolean conference;
    private final String name;

    public EventTypeBriefDto(long id, boolean conference, String name) {
        this.id = id;
        this.conference = conference;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public boolean isConference() {
        return conference;
    }

    public String getName() {
        return name;
    }


    public static EventTypeBriefDto convertToBriefDto(EventType eventType, Language language) {
        return new EventTypeBriefDto(
                eventType.getId(),
                eventType.isEventTypeConference(),
                LocalizationUtils.getString(eventType.getName(), language));
    }

    public static List<EventTypeBriefDto> convertToBriefDto(List<EventType> eventTypes, Language language) {
        return eventTypes.stream()
                .map(et -> convertToBriefDto(et, language))
                .collect(Collectors.toList());
    }
}
