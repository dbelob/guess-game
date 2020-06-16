package guess.service;

import guess.domain.Language;
import guess.domain.source.Speaker;

import java.util.List;

/**
 * Speaker service.
 */
public interface SpeakerService {
    List<Speaker> getSpeakersByFirstLetter(String firstLetter, Language language);
}
