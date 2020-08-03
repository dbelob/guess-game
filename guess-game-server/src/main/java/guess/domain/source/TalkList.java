package guess.domain.source;

import java.util.List;

/**
 * Talk list.
 */
public class TalkList {
    private List<Talk> talks;

    public TalkList() {
    }

    public TalkList(List<Talk> talks) {
        this.talks = talks;
    }

    public List<Talk> getTalks() {
        return talks;
    }

    public void setTalks(List<Talk> talks) {
        this.talks = talks;
    }
}
