package guess.service;

import guess.dao.SpeakerDao;
import guess.domain.Language;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Speaker service implementation.
 */
@Service
public class SpeakerServiceImpl implements SpeakerService {
    private final SpeakerDao speakerDao;

    @Autowired
    public SpeakerServiceImpl(SpeakerDao speakerDao) {
        this.speakerDao = speakerDao;
    }

    @Override
    public List<Speaker> getSpeakersByFirstLetter(String firstLetter, Language language) {
        return speakerDao.getSpeakers().stream()
                .filter(s -> {
                    String name = LocalizationUtils.getString(s.getNameWithLastNameFirst(), language);
                    String nameFirstLetter = ((name != null) && (name.length() > 0)) ? name.substring(0, 1) : null;

                    return firstLetter.equalsIgnoreCase(nameFirstLetter);
                })
                .collect(Collectors.toList());
    }
}
