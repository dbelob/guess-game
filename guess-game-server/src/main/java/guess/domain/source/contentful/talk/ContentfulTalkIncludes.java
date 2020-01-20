package guess.domain.source.contentful.talk;

import com.fasterxml.jackson.annotation.JsonProperty;
import guess.domain.source.contentful.ContentfulIncludes;
import guess.domain.source.contentful.speaker.ContentfulSpeaker;

import java.util.List;

public class ContentfulTalkIncludes extends ContentfulIncludes {
    @JsonProperty("Entry")
    private List<ContentfulSpeaker> entry;

    public List<ContentfulSpeaker> getEntry() {
        return entry;
    }

    public void setEntry(List<ContentfulSpeaker> entry) {
        this.entry = entry;
    }
}
