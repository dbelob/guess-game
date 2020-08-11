package guess.domain.source.contentful.asset;

import guess.domain.source.contentful.ContentfulEntity;

public class ContentfulAsset extends ContentfulEntity {
    private ContentfulAssetFields fields;

    public ContentfulAssetFields getFields() {
        return fields;
    }

    public void setFields(ContentfulAssetFields fields) {
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
