package guess.domain.source.contentful.talk;

import guess.domain.source.contentful.talk.fields.ContentfulTalkFields;

public class ContentfulTalk<T extends ContentfulTalkFields> {
    private T fields;

    public T getFields() {
        return fields;
    }

    public void setFields(T fields) {
        this.fields = fields;
    }
}
