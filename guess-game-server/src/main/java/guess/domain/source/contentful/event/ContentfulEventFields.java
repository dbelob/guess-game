package guess.domain.source.contentful.event;

import guess.domain.source.contentful.ContentfulLink;

public class ContentfulEventFields {
    private String conferenceName;
    private String eventStart;
    private String eventEnd;
    private String conferenceLink;
    private ContentfulLink eventCity;
    private String venueAddress;
    private String youtubePlayList;
    private String addressLink;

    public String getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    public String getEventStart() {
        return eventStart;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    public String getConferenceLink() {
        return conferenceLink;
    }

    public void setConferenceLink(String conferenceLink) {
        this.conferenceLink = conferenceLink;
    }

    public ContentfulLink getEventCity() {
        return eventCity;
    }

    public void setEventCity(ContentfulLink eventCity) {
        this.eventCity = eventCity;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }

    public String getYoutubePlayList() {
        return youtubePlayList;
    }

    public void setYoutubePlayList(String youtubePlayList) {
        this.youtubePlayList = youtubePlayList;
    }

    public String getAddressLink() {
        return addressLink;
    }

    public void setAddressLink(String addressLink) {
        this.addressLink = addressLink;
    }
}
