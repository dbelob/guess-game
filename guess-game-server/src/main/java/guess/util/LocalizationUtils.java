package guess.util;

import guess.domain.Language;
import guess.domain.source.LocaleItem;

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
     * Gets name for language.
     *
     * @param localeItems     locale items
     * @param language        language
     * @param defaultLanguage default language
     * @return name
     */
    public static String getName(List<LocaleItem> localeItems, Language language, Language defaultLanguage) {
        Language finalLanguage = (language != null) ? language : defaultLanguage;

        Optional<LocaleItem> currentLanguageOptional = localeItems.stream()
                .filter(et -> et.getLanguage().equals(finalLanguage.getCode()))
                .findFirst();

        if (currentLanguageOptional.isPresent()) {
            return currentLanguageOptional.get().getText();
        } else {
            Optional<LocaleItem> defaultLanguageOptional = localeItems.stream()
                    .filter(et -> et.getLanguage().equals(defaultLanguage.getCode()))
                    .findFirst();

            if (defaultLanguageOptional.isPresent()) {
                return defaultLanguageOptional.get().getText();
            } else {
                return "";
            }
        }
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
}
