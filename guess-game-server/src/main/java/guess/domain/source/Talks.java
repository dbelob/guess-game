package guess.domain.source;

import java.util.List;

/**
 * Talks.
 */
public class Talks {
    private List<Talk> talks;

    public Talks() {
    }

    public Talks(List<Talk> talks) {
        this.talks = talks;
    }

    public List<Talk> getTalks() {
        return talks;
    }

    public void setTalks(List<Talk> talks) {
        this.talks = talks;
    }
}
