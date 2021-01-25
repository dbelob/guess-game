package guess.domain.source;

import guess.domain.Conference;

import java.util.ArrayList;
import java.util.List;

/**
 * Event type.
 */
public class EventType extends Descriptionable {
    public static class EventTypeLinks {
        private final List<LocaleItem> siteLink;
        private final String vkLink;
        private final String twitterLink;
        private final String facebookLink;
        private final String youtubeLink;
        private final String telegramLink;

        public EventTypeLinks(List<LocaleItem> siteLink, String vkLink, String twitterLink, String facebookLink, String youtubeLink, String telegramLink) {
            this.siteLink = siteLink;
            this.vkLink = vkLink;
            this.twitterLink = twitterLink;
            this.facebookLink = facebookLink;
            this.youtubeLink = youtubeLink;
            this.telegramLink = telegramLink;
        }
    }

    private Conference conference;

    private List<LocaleItem> siteLink;
    private String vkLink;
    private String twitterLink;
    private String facebookLink;
    private String youtubeLink;
    private String telegramLink;
    private String logoFileName;
    private List<Event> events = new ArrayList<>();
    private boolean inactive;
    private String timeZoneId;

    private long organizerId;
    private Organizer organizer;

    public EventType() {
    }

    public EventType(Descriptionable descriptionable, Conference conference, String logoFileName, EventTypeLinks links, List<Event> events,
                     boolean inactive, Organizer organizer, String timeZoneId) {
        super(descriptionable.getId(), descriptionable.getName(), descriptionable.getShortDescription(), descriptionable.getLongDescription());

        this.conference = conference;
        this.siteLink = links.siteLink;
        this.vkLink = links.vkLink;
        this.twitterLink = links.twitterLink;
        this.facebookLink = links.facebookLink;
        this.youtubeLink = links.youtubeLink;
        this.telegramLink = links.telegramLink;
        this.logoFileName = logoFileName;
        this.events = events;
        this.inactive = inactive;
        this.organizer = organizer;
        this.organizerId = organizer.getId();
        this.timeZoneId = timeZoneId;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public List<LocaleItem> getSiteLink() {
        return siteLink;
    }

    public void setSiteLink(List<LocaleItem> siteLink) {
        this.siteLink = siteLink;
    }

    public String getVkLink() {
        return vkLink;
    }

    public void setVkLink(String vkLink) {
        this.vkLink = vkLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getTelegramLink() {
        return telegramLink;
    }

    public void setTelegramLink(String telegramLink) {
        this.telegramLink = telegramLink;
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

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(long organizerId) {
        this.organizerId = organizerId;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public boolean isEventTypeConference() {
        return (this.conference != null);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "EventType{" +
                "id=" + getId() +
                ", conference=" + conference +
                ", name=" + getName() +
                ", logoFileName='" + logoFileName + '\'' +
                '}';
    }
}
