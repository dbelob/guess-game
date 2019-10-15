package guess.domain.source;

import java.util.ArrayList;
import java.util.List;

/**
 * Event type.
 */
public class EventType {
    private long id;
    private List<LocaleItem> name;
    private String logoFileName;
    private List<Event> events = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<LocaleItem> getName() {
        return name;
    }

    public void setName(List<LocaleItem> name) {
        this.name = name;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public void setLogoFileName(String logoFileName) {
        this.logoFileName = logoFileName;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
