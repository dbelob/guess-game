package guess.dto.start;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.util.LocalizationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Event DTO.
 */
public class EventDto {
    private final long id;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String siteLink;
    private final String youtubeLink;

    public EventDto(long id, String name, LocalDate startDate, LocalDate endDate, String siteLink, String youtubeLink) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.siteLink = siteLink;
        this.youtubeLink = youtubeLink;
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

    public String getSiteLink() {
        return siteLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public static List<EventDto> convertToDto(List<Event> events, Language language) {
        return events.stream()
                .map(e -> new EventDto(
                        e.getId(),
                        LocalizationUtils.getString(e.getName(), language),
                        e.getStartDate(),
                        e.getEndDate(),
                        LocalizationUtils.getString(e.getSiteLink(), language),
                        e.getYoutubeLink()
                ))
                .collect(Collectors.toList());
    }
}
