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
                .filter(s -> LocalizationUtils.getString(s.getNameWithLastNameFirst(), language).startsWith(firstLetter))
                .collect(Collectors.toList());
    }
}
