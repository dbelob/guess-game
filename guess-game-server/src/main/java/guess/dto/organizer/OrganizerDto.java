package guess.dto.organizer;

import guess.domain.Language;
import guess.domain.source.Organizer;
import guess.util.LocalizationUtils;

import java.util.List;

/**
 * Organizer DTO.
 */
public class OrganizerDto {
    private final long id;
    private final String name;

    public OrganizerDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

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
