package guess.util;

import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Localization utility methods.
 */
public class LocalizationUtils {
    private static final String BUNDLE_NAME = "LocaleStrings";

    /**
     * Gets name for language (internal implementation).
     *
     * @param localeItems locale items
     * @param language    language
     * @return name
     */
    private static Optional<LocaleItem> getNameInternal(List<LocaleItem> localeItems, Language language) {
        if ((localeItems != null) && (language != null)) {
            return localeItems.stream()
                    .filter(et -> et.getLanguage().equals(language.getCode()))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets name for language.
     *
     * @param localeItems     locale items
     * @param language        language
     * @param defaultLanguage default language
     * @return name
     */
    public static String getName(List<LocaleItem> localeItems, Language language, Language defaultLanguage) {
        Language finalLanguage = (language != null) ? language : defaultLanguage;
        Optional<LocaleItem> currentLanguageOptional = getNameInternal(localeItems, finalLanguage);

        if (currentLanguageOptional.isPresent()) {
            return currentLanguageOptional.get().getText();
        }

        if (finalLanguage != defaultLanguage) {
            Optional<LocaleItem> defaultLanguageOptional = getNameInternal(localeItems, defaultLanguage);

            if (defaultLanguageOptional.isPresent()) {
                return defaultLanguageOptional.get().getText();
            }
        }

        return "";
    }

    /**
     * Gets name for language.
     *
     * @param localeItems locale items
     * @param language    language
     * @return name
     */
    public static String getName(List<LocaleItem> localeItems, Language language) {
        return getName(localeItems, language, Language.ENGLISH);
    }

    /**
     * Gets locale string.
     *
     * @param key      key
     * @param language language
     * @return locale string
     */
    public static String getLocaleString(String key, Language language) {
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
        String name = LocalizationUtils.getName(speaker.getName(), language);
        String company = LocalizationUtils.getName(speaker.getCompany(), language);

        if ((company != null) && !company.isEmpty()) {
            return String.format("%s (%s)", name, company);
        } else {
            return name;
        }
    }
}
