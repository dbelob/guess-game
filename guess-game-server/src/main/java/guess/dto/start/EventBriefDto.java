package guess.dto.start;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.util.LocalizationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Event DTO (brief).
 */
public class EventBriefDto {
    private final long id;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public EventBriefDto(long id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getId() {
        return id;
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

    public static EventBriefDto convertToBriefDto(Event event, Language language) {
        return new EventBriefDto(
                event.getId(),
                LocalizationUtils.getString(event.getName(), language),
                event.getStartDate(),
                event.getEndDate());
    }

    public static List<EventBriefDto> convertToBriefDto(List<Event> events, Language language) {
        return events.stream()
                .map(e -> convertToBriefDto(e, language))
                .collect(Collectors.toList());
    }
}
