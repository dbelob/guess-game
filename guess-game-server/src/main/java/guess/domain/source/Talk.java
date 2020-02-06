package guess.domain.source;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Talk.
 */
public class Talk {
    private long id;
    private List<LocaleItem> name;
    private List<LocaleItem> shortDescription;
    private List<LocaleItem> longDescription;
    private List<String> presentationLinks;
    private List<String> videoLinks;

    private List<Long> speakerIds;
    private List<Speaker> speakers = new ArrayList<>();

    public Talk() {
    }

    public Talk(long id, List<LocaleItem> name, List<LocaleItem> shortDescription, List<LocaleItem> longDescription,
                List<String> presentationLinks, List<String> videoLinks, List<Speaker> speakers) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.presentationLinks = presentationLinks;
        this.videoLinks = videoLinks;
        this.speakers = speakers;
        this.speakerIds = speakers.stream()
                .map(Speaker::getId)
                .collect(Collectors.toList());
    }

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

    public List<LocaleItem> getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(List<LocaleItem> shortDescription) {
        this.shortDescription = shortDescription;
    }

    public List<LocaleItem> getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(List<LocaleItem> longDescription) {
        this.longDescription = longDescription;
    }

    public List<String> getPresentationLinks() {
        return presentationLinks;
    }

    public void setPresentationLinks(List<String> presentationLinks) {
        this.presentationLinks = presentationLinks;
    }

    public List<String> getVideoLinks() {
        return videoLinks;
    }

    public void setVideoLinks(List<String> videoLinks) {
        this.videoLinks = videoLinks;
    }

    public List<Long> getSpeakerIds() {
        return speakerIds;
    }

    public void setSpeakerIds(List<Long> speakerIds) {
        this.speakerIds = speakerIds;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }

    @Override
    public String toString() {
        return "Talk{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
