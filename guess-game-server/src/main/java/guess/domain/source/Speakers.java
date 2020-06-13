package guess.domain.source;

import java.util.List;

/**
 * Speakers.
 */
public class Speakers {
    private List<Speaker> speakers;

    public Speakers() {
    }

    public Speakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }
}
