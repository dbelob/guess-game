package guess.domain.source;

import java.util.List;

/**
 * Organizer.
 */
public class Organizer extends Nameable {
    public Organizer() {

    }

    public Organizer(long id, List<LocaleItem> name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return "Organizer{" +
                "id=" + getId() +
                "name=" + getName() +
                '}';
    }
}
