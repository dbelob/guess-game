package guess.domain.source.contentful;

import java.util.Objects;

public class ContentfulEntity {
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

        ContentfulEntity that = (ContentfulEntity) o;

        return Objects.equals(sys, that.sys);
    }

    @Override
    public int hashCode() {
        return sys != null ? sys.hashCode() : 0;
    }
}
