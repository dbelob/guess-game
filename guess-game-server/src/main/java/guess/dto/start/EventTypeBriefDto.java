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
    private final String displayName;
    private final String logoFileName;

    public EventTypeBriefDto(long id, boolean conference, String name, String displayName, String logoFileName) {
        this.id = id;
        this.conference = conference;
        this.name = name;
        this.displayName = displayName;
        this.logoFileName = logoFileName;
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

    public String getDisplayName() {
        return displayName;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public static EventTypeBriefDto convertToBriefDto(EventType eventType, Language language) {
        final String CONFERENCES_EVENT_TYPE_TEXT = "conferencesEventTypeText";
        final String MEETUPS_EVENT_TYPE_TEXT = "meetupsEventTypeText";

        String name = LocalizationUtils.getString(eventType.getName(), language);
        String resourceKey = (eventType.isEventTypeConference()) ? CONFERENCES_EVENT_TYPE_TEXT : MEETUPS_EVENT_TYPE_TEXT;
        String displayName = String.format(LocalizationUtils.getResourceString(resourceKey, language), name);

        return new EventTypeBriefDto(
                eventType.getId(),
                eventType.isEventTypeConference(),
                name,
                displayName,
                eventType.getLogoFileName());
    }

    public static List<EventTypeBriefDto> convertToBriefDto(List<EventType> eventTypes, Language language) {
        return eventTypes.stream()
                .map(et -> convertToBriefDto(et, language))
                .collect(Collectors.toList());
    }
}
