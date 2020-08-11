package guess.domain.source.contentful.city;

import guess.domain.source.contentful.ContentfulEntity;

public class ContentfulCity extends ContentfulEntity {
    private ContentfulCityFields fields;

    public ContentfulCityFields getFields() {
        return fields;
    }

    public void setFields(ContentfulCityFields fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
