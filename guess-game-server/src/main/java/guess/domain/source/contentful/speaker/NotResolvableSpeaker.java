package guess.domain.source.contentful.speaker;

import guess.domain.source.Speaker;
import guess.util.ContentfulUtils;

public abstract class NotResolvableSpeaker {
    private final ContentfulUtils.ConferenceSpaceInfo conferenceSpaceInfo;
    private final String entryId;

    protected NotResolvableSpeaker(ContentfulUtils.ConferenceSpaceInfo conferenceSpaceInfo, String entryId) {
        this.conferenceSpaceInfo = conferenceSpaceInfo;
        this.entryId = entryId;
    }

    public ContentfulUtils.ConferenceSpaceInfo getConferenceSpaceInfo() {
        return conferenceSpaceInfo;
    }

    public String getEntryId() {
        return entryId;
    }

    public abstract Speaker createSpeaker(long id);
}
