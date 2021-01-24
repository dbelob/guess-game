package guess.domain.source;

import java.util.List;

/**
 * Organizer list.
 */
public class OrganizerList {
    private List<Organizer> organizers;

    public OrganizerList() {
    }

    public OrganizerList(List<Organizer> organizers) {
        this.organizers = organizers;
    }

    public List<Organizer> getOrganizers() {
        return organizers;
    }

    public void setOrganizers(List<Organizer> organizers) {
        this.organizers = organizers;
    }
}
