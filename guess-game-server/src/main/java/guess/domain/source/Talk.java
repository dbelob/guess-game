package guess.domain.source;

import java.util.ArrayList;
import java.util.List;

/**
 * Talk.
 */
public class Talk {
    private long id;
    private List<LocaleItem> name;
    private List<LocaleItem> shortDescription;
    private List<LocaleItem> longDescription;
    private String videoLink;

    private List<Long> speakerIds;
    private List<Speaker> speakers = new ArrayList<>();

    public Talk() {
    }

    public Talk(long id, List<LocaleItem> name, List<LocaleItem> shortDescription, List<LocaleItem> longDescription,
                String videoLink, List<Speaker> speakers) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.videoLink = videoLink;
        this.speakers = speakers;
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

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
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
                ", shortDescription=" + shortDescription +
                ", longDescription=" + longDescription +
                ", videoLink=" + videoLink +
                ", speakers=" + speakers +
                '}';
    }
}
