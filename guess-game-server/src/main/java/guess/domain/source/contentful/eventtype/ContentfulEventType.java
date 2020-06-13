package guess.domain.source.contentful.eventtype;

import guess.domain.source.contentful.ContentfulEntity;

public class ContentfulEventType extends ContentfulEntity {
    private ContentfulEventTypeFields fields;

    public ContentfulEventTypeFields getFields() {
        return fields;
    }

    public void setFields(ContentfulEventTypeFields fields) {
        this.fields = fields;
    }
}
