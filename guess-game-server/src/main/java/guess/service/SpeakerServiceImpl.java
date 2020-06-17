package guess.service;

import guess.dao.SpeakerDao;
import guess.domain.Language;
import guess.domain.source.LocaleItem;
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

    @Override
    public List<Speaker> getSpeakers(String name, String company, String twitter, String gitHub, boolean isJavaChampion, boolean isMvp) {
        String trimmedLowerCasedName = trimAndLowerCase(name);
        String trimmedLowerCasedCompany = trimAndLowerCase(company);
        String trimmedLowerCasedTwitter = trimAndLowerCase(twitter);
        String trimmedLowerCasedGitHub = trimAndLowerCase(gitHub);

        return speakerDao.getSpeakers().stream()
                .filter(s -> {
                    return (isSubstringFound(trimmedLowerCasedName, s.getName()) ||
                            isSubstringFound(trimmedLowerCasedCompany, s.getCompany()) ||
                            isSubstringFound(trimmedLowerCasedTwitter, s.getTwitter()) ||
                            isSubstringFound(trimmedLowerCasedGitHub, s.getGitHub()) ||
                            (isJavaChampion && s.isJavaChampion()) ||
                            (isMvp && s.isAnyMvp()));
                })
                .collect(Collectors.toList());

    }

    private String trimAndLowerCase(String value) {
        return (value != null) ? value.trim().toLowerCase() : null;
    }

    private boolean isSubstringFound(String trimmedLowerCasedSubstring, List<LocaleItem> localeItems) {
        if ((trimmedLowerCasedSubstring == null) || trimmedLowerCasedSubstring.isEmpty()) {
            return false;
        }

        for (LocaleItem localeItem : localeItems) {
            if (isSubstringFound(trimmedLowerCasedSubstring, localeItem.getText())) {
                return true;
            }
        }

        return false;
    }

    private boolean isSubstringFound(String trimmedLowerCasedSubstring, String item) {
        if ((trimmedLowerCasedSubstring == null) || trimmedLowerCasedSubstring.isEmpty()) {
            return false;
        }

        String trimmedLowerCasedItem = trimAndLowerCase(item);

        if ((trimmedLowerCasedItem == null) || trimmedLowerCasedItem.isEmpty()) {
            return false;
        }

        return trimmedLowerCasedItem.contains(trimmedLowerCasedSubstring);
    }
}
