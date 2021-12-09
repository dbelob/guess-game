package guess.domain.source;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Talk.
 */
public class Talk extends Descriptionable {
    public static class TalkLinks {
        private final List<String> presentationLinks;
        private final List<String> materialLinks;
        private final List<String> videoLinks;

        public TalkLinks(List<String> presentationLinks, List<String> materialLinks, List<String> videoLinks) {
            this.presentationLinks = presentationLinks;
            this.materialLinks = materialLinks;
            this.videoLinks = videoLinks;
        }
    }

    private Long talkDay;
    private LocalTime trackTime;
    private Long track;
    private String language;
    private List<String> presentationLinks;
    private List<String> materialLinks;
    private List<String> videoLinks;

    private List<Long> speakerIds;
    private List<Speaker> speakers = new ArrayList<>();

    public Talk() {
    }

    public Talk(Descriptionable descriptionable, Long talkDay, LocalTime trackTime, Long track, String language, TalkLinks links,
                List<Speaker> speakers) {
        super(descriptionable.getId(), descriptionable.getName(), descriptionable.getShortDescription(), descriptionable.getLongDescription());

        this.talkDay = talkDay;
        this.trackTime = trackTime;
        this.track = track;
        this.language = language;
        this.presentationLinks = links.presentationLinks;
        this.materialLinks = links.materialLinks;
        this.videoLinks = links.videoLinks;
        this.speakers = speakers;
        this.speakerIds = speakers.stream()
                .map(Speaker::getId)
                .collect(Collectors.toCollection(ArrayList::new));
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getPresentationLinks() {
        return presentationLinks;
    }

    public void setPresentationLinks(List<String> presentationLinks) {
        this.presentationLinks = presentationLinks;
    }

    public List<String> getMaterialLinks() {
        return materialLinks;
    }

    public void setMaterialLinks(List<String> materialLinks) {
        this.materialLinks = materialLinks;
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
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Talk{" +
                "id=" + getId() +
                ", name=" + getName() +
                '}';
    }
}
