package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.util.LocalizationUtils;

import java.time.LocalDate;

/**
 * Event home info DTO.
 */
public record EventHomeInfoDto(long id, String name, LocalDate startDate, LocalDate endDate,
                               String eventTypeLogoFileName) {
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
