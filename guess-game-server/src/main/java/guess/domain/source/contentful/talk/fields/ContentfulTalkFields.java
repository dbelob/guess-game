package guess.domain.source.contentful.talk.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import guess.domain.source.contentful.ContentfulLink;

import java.time.LocalTime;
import java.util.List;

public abstract class ContentfulTalkFields {
    private String name;
    private String nameEn;

    @JsonProperty("short")
    private String shortRu;
    private String shortEn;

    @JsonProperty("long")
    private String longRu;
    private String longEn;

    private List<ContentfulLink> presentations;
    private ContentfulLink presentation;

    private List<ContentfulLink> speakers;
    private Long talkDay;
    private LocalTime trackTime;
    private Long track;
    private Boolean language;
    private String video;
    private Boolean sdTrack;
    private Boolean demoStage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getShortRu() {
        return shortRu;
    }

    public void setShortRu(String shortRu) {
        this.shortRu = shortRu;
    }

    public String getShortEn() {
        return shortEn;
    }

    public void setShortEn(String shortEn) {
        this.shortEn = shortEn;
    }

    public String getLongRu() {
        return longRu;
    }

    public void setLongRu(String longRu) {
        this.longRu = longRu;
    }

    public String getLongEn() {
        return longEn;
    }

    public void setLongEn(String longEn) {
        this.longEn = longEn;
    }

    public List<ContentfulLink> getPresentations() {
        return presentations;
    }

    public void setPresentations(List<ContentfulLink> presentations) {
        this.presentations = presentations;
    }

    public ContentfulLink getPresentation() {
        return presentation;
    }

    public void setPresentation(ContentfulLink presentation) {
        this.presentation = presentation;
    }

    public List<ContentfulLink> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<ContentfulLink> speakers) {
        this.speakers = speakers;
    }

    public Long getTalkDay() {
        return talkDay;
    }

    public void setTalkDay(Long talkDay) {
        this.talkDay = talkDay;
    }

    public LocalTime getTrackTime() {
        return trackTime;
    }

    public void setTrackTime(LocalTime trackTime) {
        this.trackTime = trackTime;
    }

    public Long getTrack() {
        return track;
    }

    public void setTrack(Long track) {
        this.track = track;
    }

    public Boolean getLanguage() {
        return language;
    }

    public void setLanguage(Boolean language) {
        this.language = language;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Boolean getSdTrack() {
        return sdTrack;
    }

    public void setSdTrack(Boolean sdTrack) {
        this.sdTrack = sdTrack;
    }

    public Boolean getDemoStage() {
        return demoStage;
    }

    public void setDemoStage(Boolean demoStage) {
        this.demoStage = demoStage;
    }
}
