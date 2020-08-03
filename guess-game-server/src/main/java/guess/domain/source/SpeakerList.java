package guess.domain.source;

import java.util.List;

/**
 * Speaker list.
 */
public class SpeakerList {
    private List<Speaker> speakers;

    public SpeakerList() {
    }

    public SpeakerList(List<Speaker> speakers) {
        this.speakers = speakers;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }
}
