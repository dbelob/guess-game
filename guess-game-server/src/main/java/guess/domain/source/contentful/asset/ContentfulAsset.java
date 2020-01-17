package guess.domain.source.contentful.asset;

import guess.domain.source.contentful.ContentfulSys;

public class ContentfulAsset {
    private ContentfulSys sys;
    private ContentfulAssetFields fields;

    public ContentfulSys getSys() {
        return sys;
    }

    public void setSys(ContentfulSys sys) {
        this.sys = sys;
    }

    public ContentfulAssetFields getFields() {
        return fields;
    }

    public void setFields(ContentfulAssetFields fields) {
        this.fields = fields;
    }
}
