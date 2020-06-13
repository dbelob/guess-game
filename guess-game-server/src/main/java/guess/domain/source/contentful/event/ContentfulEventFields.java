package guess.domain.source.contentful.event;

import guess.domain.source.contentful.ContentfulLink;

import java.util.Map;

public class ContentfulEventFields {
    private Map<String, String> conferenceName;
    private Map<String, String> eventStart;
    private Map<String, String> eventEnd;
    private Map<String, String> conferenceLink;
    private Map<String, ContentfulLink> eventCity;
    private Map<String, String> venueAddress;
    private Map<String, String> youtubePlayList;
    private Map<String, String> addressLink;

    public Map<String, String> getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(Map<String, String> conferenceName) {
        this.conferenceName = conferenceName;
    }

    public Map<String, String> getEventStart() {
        return eventStart;
    }

    public void setEventStart(Map<String, String> eventStart) {
        this.eventStart = eventStart;
    }

    public Map<String, String> getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(Map<String, String> eventEnd) {
        this.eventEnd = eventEnd;
    }

    public Map<String, String> getConferenceLink() {
        return conferenceLink;
    }

    public void setConferenceLink(Map<String, String> conferenceLink) {
        this.conferenceLink = conferenceLink;
    }

    public Map<String, ContentfulLink> getEventCity() {
        return eventCity;
    }

    public void setEventCity(Map<String, ContentfulLink> eventCity) {
        this.eventCity = eventCity;
    }

    public Map<String, String> getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(Map<String, String> venueAddress) {
        this.venueAddress = venueAddress;
    }

    public Map<String, String> getYoutubePlayList() {
        return youtubePlayList;
    }

    public void setYoutubePlayList(Map<String, String> youtubePlayList) {
        this.youtubePlayList = youtubePlayList;
    }

    public Map<String, String> getAddressLink() {
        return addressLink;
    }

    public void setAddressLink(Map<String, String> addressLink) {
        this.addressLink = addressLink;
    }
}
