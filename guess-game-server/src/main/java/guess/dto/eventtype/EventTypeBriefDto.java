package guess.dto.eventtype;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Event type DTO (brief).
 */
public class EventTypeBriefDto extends EventTypeSuperBriefDto {
    private final String shortDescription;

    public EventTypeBriefDto(EventTypeSuperBriefDto eventTypeSuperBriefDto, String shortDescription) {
        super(eventTypeSuperBriefDto.getId(), eventTypeSuperBriefDto.isConference(), eventTypeSuperBriefDto.getName(),
                eventTypeSuperBriefDto.getDisplayName(), eventTypeSuperBriefDto.getLogoFileName(), eventTypeSuperBriefDto.isInactive());
        this.shortDescription = shortDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public static EventTypeBriefDto convertToBriefDto(EventTypeSuperBriefDto eventTypeSuperBriefDto, EventType eventType,
                                                      Language language) {
        String shortDescription = LocalizationUtils.getString(eventType.getShortDescription(), language);

        return new EventTypeBriefDto(
                eventTypeSuperBriefDto,
                shortDescription);
    }

    public static EventTypeBriefDto convertToBriefDto(EventType eventType, Language language) {
        return convertToBriefDto(convertToSuperBriefDto(eventType, language), eventType, language);
    }

    public static List<EventTypeBriefDto> convertToBriefDto(List<EventType> eventTypes, Language language) {
        return eventTypes.stream()
                .map(et -> convertToBriefDto(et, language))
                .collect(Collectors.toList());
    }
}
