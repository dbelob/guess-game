package guess.dto.start;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Event DTO.
 */
public class EventDto extends EventBriefDto {
    private final String siteLink;
    private final String youtubeLink;

    public EventDto(EventBriefDto eventBriefDto, String siteLink, String youtubeLink) {
        super(eventBriefDto.getId(), eventBriefDto.getEventTypeId(), eventBriefDto.getName(), eventBriefDto.getStartDate(), eventBriefDto.getEndDate());

        this.siteLink = siteLink;
        this.youtubeLink = youtubeLink;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public static EventDto convertToDto(Event event, Language language) {
        return new EventDto(
                convertToBriefDto(event, language),
                LocalizationUtils.getString(event.getSiteLink(), language),
                event.getYoutubeLink());
    }

    public static List<EventDto> convertToDto(List<Event> events, Language language) {
        return events.stream()
                .map(e -> convertToDto(e, language))
                .collect(Collectors.toList());
    }
}
