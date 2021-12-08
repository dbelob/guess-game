package guess.dto.organizer;

import guess.domain.Language;
import guess.domain.source.Organizer;
import guess.util.LocalizationUtils;

import java.util.List;

/**
 * Organizer DTO.
 */
public record OrganizerDto(long id, String name) {
    public static OrganizerDto convertToDto(Organizer organizer, Language language) {
        var name = LocalizationUtils.getString(organizer.getName(), language);

        return new OrganizerDto(
                organizer.getId(),
                name);
    }

    public static List<OrganizerDto> convertToDto(List<Organizer> organizers, Language language) {
        return organizers.stream()
                .map(o -> convertToDto(o, language))
                .toList();
    }
}
