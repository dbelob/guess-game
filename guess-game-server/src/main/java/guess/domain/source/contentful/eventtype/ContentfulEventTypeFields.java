package guess.domain.source.contentful.eventtype;

import java.util.Map;

public class ContentfulEventTypeFields {
    private Map<String, String> eventName;
    private Map<String, String> eventDescriptions;
    private Map<String, String> siteLink;
    private Map<String, String> vkLink;
    private Map<String, String> twLink;
    private Map<String, String> fbLink;
    private Map<String, String> youtubeLink;
    private Map<String, String> telegramLink;

    public Map<String, String> getEventName() {
        return eventName;
    }

    public void setEventName(Map<String, String> eventName) {
        this.eventName = eventName;
    }

    public Map<String, String> getEventDescriptions() {
        return eventDescriptions;
    }

    public void setEventDescriptions(Map<String, String> eventDescriptions) {
        this.eventDescriptions = eventDescriptions;
    }

    public Map<String, String> getSiteLink() {
        return siteLink;
    }

    public void setSiteLink(Map<String, String> siteLink) {
        this.siteLink = siteLink;
    }

    public Map<String, String> getVkLink() {
        return vkLink;
    }

    public void setVkLink(Map<String, String> vkLink) {
        this.vkLink = vkLink;
    }

    public Map<String, String> getTwLink() {
        return twLink;
    }

    public void setTwLink(Map<String, String> twLink) {
        this.twLink = twLink;
    }

    public Map<String, String> getFbLink() {
        return fbLink;
    }

    public void setFbLink(Map<String, String> fbLink) {
        this.fbLink = fbLink;
    }

    public Map<String, String> getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(Map<String, String> youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public Map<String, String> getTelegramLink() {
        return telegramLink;
    }

    public void setTelegramLink(Map<String, String> telegramLink) {
        this.telegramLink = telegramLink;
    }
}
