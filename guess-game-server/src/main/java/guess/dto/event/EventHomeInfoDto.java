package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.util.LocalizationUtils;

import java.time.LocalDate;

/**
 * Event home info DTO.
 */
public class EventHomeInfoDto {
    private final long id;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String eventTypeLogoFileName;

    public EventHomeInfoDto(long id, String name, LocalDate startDate, LocalDate endDate, String eventTypeLogoFileName) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventTypeLogoFileName = eventTypeLogoFileName;
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

    public String getEventTypeLogoFileName() {
        return eventTypeLogoFileName;
    }

    public static EventHomeInfoDto convertToDto(Event event, Language language) {
        String logoFileName = (event.getEventType() != null) ? event.getEventType().getLogoFileName() : null;

        return new EventHomeInfoDto(
                event.getId(),
                LocalizationUtils.getString(event.getName(), language),
                event.getStartDate(),
                event.getEndDate(),
                logoFileName);
    }
}
