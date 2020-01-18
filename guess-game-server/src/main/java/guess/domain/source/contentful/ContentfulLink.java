package guess.domain.source.contentful;

import guess.domain.source.contentful.ContentfulSys;

import java.util.Objects;

public class ContentfulLink {
    private ContentfulSys sys;

    public ContentfulSys getSys() {
        return sys;
    }

    public void setSys(ContentfulSys sys) {
        this.sys = sys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentfulLink that = (ContentfulLink) o;
        return Objects.equals(sys, that.sys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sys);
    }
}
