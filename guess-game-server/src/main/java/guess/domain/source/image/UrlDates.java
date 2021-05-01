package guess.domain.source.image;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * URL and dates.
 */
public class UrlDates {
    private final String url;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public UrlDates(String filename, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.url = filename;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getUrl() {
        return url;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UrlDates)) return false;
        var urlDates = (UrlDates) o;
        return Objects.equals(url, urlDates.url) && Objects.equals(createdAt, urlDates.createdAt) && Objects.equals(updatedAt, urlDates.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "UrlDates{" +
                "url='" + url + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
