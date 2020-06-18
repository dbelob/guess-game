package guess.service;

import guess.dao.SpeakerDao;
import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
                    String nameFirstLetter;

                    if (name != null) {
                        String trimmedName = name.trim();

                        nameFirstLetter = (trimmedName.length() > 0) ? trimmedName.substring(0, 1) : null;
                    } else {
                        nameFirstLetter = null;
                    }

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
        boolean isNameSet = isStringSet(trimmedLowerCasedName);
        boolean isCompanySet = isStringSet(trimmedLowerCasedCompany);
        boolean isTwitterSet = isStringSet(trimmedLowerCasedTwitter);
        boolean isGitHubSet = isStringSet(trimmedLowerCasedGitHub);

        if (!isNameSet && !isCompanySet && !isTwitterSet && !isGitHubSet && !isJavaChampion && !isMvp) {
            return Collections.emptyList();
        } else {
            return speakerDao.getSpeakers().stream()
                    .filter(s -> ((!isNameSet || isSubstringFound(trimmedLowerCasedName, s.getName())) &&
                            (!isCompanySet || isSubstringFound(trimmedLowerCasedCompany, s.getCompany())) &&
                            (!isTwitterSet || isSubstringFound(trimmedLowerCasedTwitter, s.getTwitter())) &&
                            (!isGitHubSet || isSubstringFound(trimmedLowerCasedGitHub, s.getGitHub())) &&
                            (!isJavaChampion || s.isJavaChampion()) &&
                            (!isMvp || s.isAnyMvp())))
                    .collect(Collectors.toList());
        }
    }

    private String trimAndLowerCase(String value) {
        return (value != null) ? value.trim().toLowerCase() : null;
    }

    private boolean isStringSet(String string) {
        return ((string != null) && !string.isEmpty());
    }

    private boolean isSubstringFound(String trimmedLowerCasedSubstring, List<LocaleItem> localeItems) {
        if (!isStringSet(trimmedLowerCasedSubstring) || (localeItems == null)) {
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
        if (!isStringSet(trimmedLowerCasedSubstring)) {
            return false;
        }

        String trimmedLowerCasedItem = trimAndLowerCase(item);

        if (!isStringSet(trimmedLowerCasedItem)) {
            return false;
        }

        return trimmedLowerCasedItem.contains(trimmedLowerCasedSubstring);
    }

    @Override
    public Speaker getSpeakerById(long id) {
        return speakerDao.getSpeakerById(id);
    }
}
