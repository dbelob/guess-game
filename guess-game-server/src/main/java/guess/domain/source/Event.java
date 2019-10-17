package guess.domain.source;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Event.
 */
public class Event {
    private long eventTypeId;
    private EventType eventType;

    private List<LocaleItem> name;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<LocaleItem> city;
    private List<LocaleItem> place;

    private List<Long> talkIds;
    private final List<Talk> talks = new ArrayList<>();

    public long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public List<LocaleItem> getName() {
        return name;
    }

    public void setName(List<LocaleItem> name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<LocaleItem> getCity() {
        return city;
    }

    public void setCity(List<LocaleItem> city) {
        this.city = city;
    }

    public List<LocaleItem> getPlace() {
        return place;
    }

    public void setPlace(List<LocaleItem> place) {
        this.place = place;
    }

    public List<Long> getTalkIds() {
        return talkIds;
    }

    public void setTalkIds(List<Long> talkIds) {
        this.talkIds = talkIds;
    }

    public List<Talk> getTalks() {
        return talks;
    }

}
