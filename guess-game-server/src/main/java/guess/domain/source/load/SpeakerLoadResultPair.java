package guess.domain.source.load;

import guess.domain.source.Speaker;
import guess.domain.source.image.UrlFilename;

/**
 * Pair of speaker load result.
 */
public class SpeakerLoadResultPair {
    private final LoadResult<Speaker> speakers;
    private final LoadResult<UrlFilename> urlFilenames;

    public SpeakerLoadResultPair(LoadResult<Speaker> speakers, LoadResult<UrlFilename> urlFilenames) {
        this.speakers = speakers;
        this.urlFilenames = urlFilenames;
    }

    public LoadResult<Speaker> getSpeakers() {
        return speakers;
    }

    public LoadResult<UrlFilename> getUrlFilenames() {
        return urlFilenames;
    }
}
