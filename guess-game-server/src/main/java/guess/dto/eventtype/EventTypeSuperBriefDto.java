package guess.dto.eventtype;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.util.LocalizationUtils;

import java.util.List;

/**
 * Event type DTO (super brief).
 */
public class EventTypeSuperBriefDto {
    private final long id;
    private final boolean conference;
    private final String name;
    private final String displayName;
    private final String logoFileName;
    private final boolean inactive;

    public EventTypeSuperBriefDto(long id, boolean conference, String name, String displayName, String logoFileName,
                                  boolean inactive) {
        this.id = id;
        this.conference = conference;
        this.name = name;
        this.displayName = displayName;
        this.logoFileName = logoFileName;
        this.inactive = inactive;
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

    public boolean isInactive() {
        return inactive;
    }

    public static EventTypeSuperBriefDto convertToSuperBriefDto(EventType eventType, Language language) {
        var name = LocalizationUtils.getString(eventType.getName(), language);
        String resourceKey = (eventType.isEventTypeConference()) ? LocalizationUtils.CONFERENCE_EVENT_TYPE_TEXT : LocalizationUtils.MEETUP_EVENT_TYPE_TEXT;
        var displayName = String.format(LocalizationUtils.getResourceString(resourceKey, language), name);

        return new EventTypeSuperBriefDto(
                eventType.getId(),
                eventType.isEventTypeConference(),
                name,
                displayName,
                eventType.getLogoFileName(),
                eventType.isInactive());
    }

    public static List<EventTypeSuperBriefDto> convertToSuperBriefDto(List<EventType> eventTypes, Language language) {
        return eventTypes.stream()
                .map(et -> convertToSuperBriefDto(et, language))
                .toList();
    }
}
