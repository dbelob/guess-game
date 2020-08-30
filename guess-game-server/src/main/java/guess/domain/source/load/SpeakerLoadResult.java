package guess.domain.source.load;

import guess.domain.source.Speaker;
import guess.domain.source.image.UrlFilename;

import java.util.List;

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
}
