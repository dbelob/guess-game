package guess.service;

import guess.domain.Language;
import guess.domain.source.Speaker;

import java.util.List;

/**
 * Speaker service.
 */
public interface SpeakerService {
    Speaker getSpeakerById(long id);

    List<Speaker> getSpeakerByIds(List<Long> ids);

    List<Speaker> getSpeakersByFirstLetter(String firstLetter, Language language);

    List<Speaker> getSpeakersByFirstLetters(String firstLetters, Language language);

    List<Speaker> getSpeakers(String name, String company, String twitter, String gitHub, boolean isJavaChampion, boolean isMvp);

    List<Speaker> getSpeakersByCompanyId(long companyId);
}
