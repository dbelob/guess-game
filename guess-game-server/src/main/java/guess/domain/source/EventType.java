package guess.domain.source;

import guess.domain.Conference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Event type.
 */
public class EventType {
    private long id;
    private Conference conference;
    private List<LocaleItem> name;
    private List<LocaleItem> description;

    private List<LocaleItem> siteLink;
    private String vkLink;
    private String twitterLink;
    private String facebookLink;
    private String youtubeLink;
    private String telegramLink;

    private String logoFileName;

    private List<Event> events = new ArrayList<>();

    public EventType() {
    }

    public EventType(long id, Conference conference, List<LocaleItem> name, String logoFileName, List<LocaleItem> description, List<LocaleItem> siteLink,
                     String vkLink, String twitterLink, String facebookLink, String youtubeLink, String telegramLink,
                     List<Event> events) {
        this.id = id;
        this.conference = conference;
        this.name = name;
        this.description = description;
        this.siteLink = siteLink;
        this.vkLink = vkLink;
        this.twitterLink = twitterLink;
        this.facebookLink = facebookLink;
        this.youtubeLink = youtubeLink;
        this.telegramLink = telegramLink;
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

    public List<LocaleItem> getDescription() {
        return description;
    }

    public void setDescription(List<LocaleItem> description) {
        this.description = description;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventType eventType = (EventType) o;
        return id == eventType.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
