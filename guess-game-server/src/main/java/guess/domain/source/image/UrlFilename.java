package guess.domain.source.image;

import java.util.Objects;

/**
 * URL, filename pair.
 */
public class UrlFilename {
    private final String url;
    private final String filename;

    public UrlFilename(String url, String filename) {
        this.url = url;
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlFilename that = (UrlFilename) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, filename);
    }

    @Override
    public String toString() {
        return "UrlFilename{" +
                "url='" + url + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}
