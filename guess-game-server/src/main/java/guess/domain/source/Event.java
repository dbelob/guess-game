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
    private List<LocaleItem> siteLink;
    private List<LocaleItem> city;
    private List<LocaleItem> venueAddress;
    private String youtubeLink;
    private String mapCoordinates;

    private List<Long> talkIds;
    private List<Talk> talks = new ArrayList<>();

    public Event() {
    }

    public Event(EventType eventType, List<LocaleItem> name, LocalDate startDate, LocalDate endDate, List<LocaleItem> siteLink,
                 List<LocaleItem> city, List<LocaleItem> venueAddress, String youtubeLink, String mapCoordinates, List<Talk> talks) {
        this.eventType = eventType;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.siteLink = siteLink;
        this.city = city;
        this.venueAddress = venueAddress;
        this.youtubeLink = youtubeLink;
        this.mapCoordinates = mapCoordinates;
        this.talks = talks;
    }

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

    public List<LocaleItem> getSiteLink() {
        return siteLink;
    }

    public void setSiteLink(List<LocaleItem> siteLink) {
        this.siteLink = siteLink;
    }

    public List<LocaleItem> getCity() {
        return city;
    }

    public void setCity(List<LocaleItem> city) {
        this.city = city;
    }

    public List<LocaleItem> getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(List<LocaleItem> venueAddress) {
        this.venueAddress = venueAddress;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getMapCoordinates() {
        return mapCoordinates;
    }

    public void setMapCoordinates(String mapCoordinates) {
        this.mapCoordinates = mapCoordinates;
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

    @Override
    public String toString() {
        return "Event{" +
                "eventType=" + eventType +
                ", name=" + name +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", city=" + city +
                ", venueAddress=" + venueAddress +
                ", talks=" + talks +
                '}';
    }
}
