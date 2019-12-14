package guess.util;

import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Localization utility methods.
 */
public class LocalizationUtils {
    private static final String BUNDLE_NAME = "LocaleStrings";

    /**
     * Gets string for language (internal implementation).
     *
     * @param localeItems locale items
     * @param language    language
     * @return name
     */
    private static Optional<LocaleItem> getStringInternal(List<LocaleItem> localeItems, Language language) {
        if ((localeItems != null) && (language != null)) {
            return localeItems.stream()
                    .filter(et -> et.getLanguage().equals(language.getCode()))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets string for language.
     *
     * @param localeItems     locale items
     * @param language        language
     * @param defaultLanguage default language
     * @return name
     */
    public static String getString(List<LocaleItem> localeItems, Language language, Language defaultLanguage) {
        Language finalLanguage = (language != null) ? language : defaultLanguage;
        Optional<LocaleItem> currentLanguageOptional = getStringInternal(localeItems, finalLanguage);

        if (currentLanguageOptional.isPresent()) {
            return currentLanguageOptional.get().getText();
        }

        if (finalLanguage != defaultLanguage) {
            Optional<LocaleItem> defaultLanguageOptional = getStringInternal(localeItems, defaultLanguage);

            if (defaultLanguageOptional.isPresent()) {
                return defaultLanguageOptional.get().getText();
            }
        }

        return "";
    }

    /**
     * Gets string for language.
     *
     * @param localeItems locale items
     * @param language    language
     * @return name
     */
    public static String getString(List<LocaleItem> localeItems, Language language) {
        return getString(localeItems, language, Language.ENGLISH);
    }

    /**
     * Gets resource string.
     *
     * @param key      key
     * @param language language
     * @return locale string
     */
    public static String getResourceString(String key, Language language) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, new Locale(language.getCode()));

        return bundle.getString(key);
    }

    /**
     * Gets speaker name with company name.
     *
     * @param speaker  speaker
     * @param language language
     * @return speaker name with company name
     */
    public static String getSpeakerNameWithCompany(Speaker speaker, Language language) {
        String name = LocalizationUtils.getString(speaker.getName(), language);
        String company = LocalizationUtils.getString(speaker.getCompany(), language);

        return ((company != null) && !company.isEmpty()) ?
                String.format("%s (%s)", name, company) :
                name;
    }

    /**
     * Gets speaker duplicates by name without company.
     *
     * @param speakers speakers
     * @param language language
     * @return speaker duplicates
     */
    public static Set<Speaker> getSpeakerDuplicatesByNameWithoutCompany(List<Speaker> speakers, Language language) {
        return speakers.stream()
                .collect(Collectors.groupingBy(s -> getString(s.getName(), language)))
                .values().stream()
                .filter(e -> e.size() > 1)  // Only duplicates
                .flatMap(Collection::stream)
                .filter(e -> {
                    // Without company
                    String company = getString(e.getCompany(), language);
                    return ((company == null) || company.isEmpty());
                })
                .collect(Collectors.toSet());
    }

    /**
     * Gets speaker duplicates by name with company.
     *
     * @param speakers speakers
     * @param language language
     * @return speaker duplicates
     */
    public static Set<Speaker> getSpeakerDuplicatesByNameWithCompany(List<Speaker> speakers, Language language) {
        return speakers.stream()
                .collect(Collectors.groupingBy(s -> getSpeakerNameWithCompany(s, language)))
                .values().stream()
                .filter(e -> e.size() > 1)  // Only duplicates
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Gets speaker name.
     *
     * @param speaker     speaker
     * @param language    language
     * @param isDuplicate {@code true} for duplicate, {@code false} otherwise
     * @return speaker name
     */
    public static String getSpeakerName(Speaker speaker, Language language, boolean isDuplicate) {
        return isDuplicate ?
                LocalizationUtils.getSpeakerNameWithCompany(speaker, language) :
                LocalizationUtils.getString(speaker.getName(), language);
    }
}
