package guess.domain.source.contentful.error;

import guess.domain.source.contentful.ContentfulSys;

public class ContentfulError {
    private ContentfulSys sys;
    private ContentfulErrorDetails details;

    public ContentfulSys getSys() {
        return sys;
    }

    public void setSys(ContentfulSys sys) {
        this.sys = sys;
    }

    public ContentfulErrorDetails getDetails() {
        return details;
    }

    public void setDetails(ContentfulErrorDetails details) {
        this.details = details;
    }
}
