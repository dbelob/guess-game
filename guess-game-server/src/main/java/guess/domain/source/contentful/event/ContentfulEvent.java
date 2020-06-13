package guess.domain.source.contentful.event;

import guess.domain.source.contentful.ContentfulEntity;

public class ContentfulEvent extends ContentfulEntity {
    private ContentfulEventFields fields;

    public ContentfulEventFields getFields() {
        return fields;
    }

    public void setFields(ContentfulEventFields fields) {
        this.fields = fields;
    }
}
