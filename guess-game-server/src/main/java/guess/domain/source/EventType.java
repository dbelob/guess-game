package guess.domain.source;

import guess.domain.Conference;

import java.util.ArrayList;
import java.util.List;

/**
 * Event type.
 */
public class EventType {
    private long id;
    private Conference conference;
    private List<LocaleItem> name;
    private String logoFileName;
    private List<Event> events = new ArrayList<>();

    public EventType() {
    }

    public EventType(long id, List<LocaleItem> name, String logoFileName, List<Event> events) {
        this.id = id;
        this.name = name;
        this.logoFileName = logoFileName;
        this.events = events;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
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

    @Override
    public String toString() {
        return "EventType{" +
                "id=" + id +
                ", conference=" + conference +
                ", name=" + name +
                ", logoFileName='" + logoFileName + '\'' +
                '}';
    }
}
