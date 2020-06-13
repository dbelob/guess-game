package guess.domain.source.contentful;

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

        return sys != null ? sys.equals(that.sys) : that.sys == null;
    }

    @Override
    public int hashCode() {
        return sys != null ? sys.hashCode() : 0;
    }
}
