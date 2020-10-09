package guess.domain.source.load;

import guess.domain.source.Speaker;
import guess.domain.source.image.UrlFilename;

import java.util.List;
import java.util.Objects;

/**
 * Speaker load result.
 */
public class SpeakerLoadResult {
    private final LoadResult<List<Speaker>> speakers;
    private final LoadResult<List<UrlFilename>> urlFilenames;

    public SpeakerLoadResult(LoadResult<List<Speaker>> speakers, LoadResult<List<UrlFilename>> urlFilenames) {
        this.speakers = speakers;
        this.urlFilenames = urlFilenames;
    }

    public LoadResult<List<Speaker>> getSpeakers() {
        return speakers;
    }

    public LoadResult<List<UrlFilename>> getUrlFilenames() {
        return urlFilenames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpeakerLoadResult that = (SpeakerLoadResult) o;
        return Objects.equals(speakers, that.speakers) &&
                Objects.equals(urlFilenames, that.urlFilenames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(speakers, urlFilenames);
    }

    @Override
    public String toString() {
        return "SpeakerLoadResult{" +
                "speakers=" + speakers +
                ", urlFilenames=" + urlFilenames +
                '}';
    }
}
