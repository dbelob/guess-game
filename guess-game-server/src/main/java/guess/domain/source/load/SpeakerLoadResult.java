package guess.domain.source.load;

import guess.domain.source.Speaker;
import guess.domain.source.image.UrlFilename;

import java.util.List;

/**
 * Speaker load result.
 */
public record SpeakerLoadResult(LoadResult<List<Speaker>> speakers, LoadResult<List<UrlFilename>> urlFilenames) {
}
