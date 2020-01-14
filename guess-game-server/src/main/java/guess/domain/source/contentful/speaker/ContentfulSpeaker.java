package guess.domain.source.contentful.speaker;

public class ContentfulSpeaker {
    private ContentfulSpeakerSys sys;
    private ContentfulSpeakerFields fields;

    public ContentfulSpeakerSys getSys() {
        return sys;
    }

    public void setSys(ContentfulSpeakerSys sys) {
        this.sys = sys;
    }

    public ContentfulSpeakerFields getFields() {
        return fields;
    }

    public void setFields(ContentfulSpeakerFields fields) {
        this.fields = fields;
    }
}
