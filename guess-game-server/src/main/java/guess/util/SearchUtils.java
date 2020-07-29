package guess.util;

import guess.domain.source.LocaleItem;

import java.util.List;

/**
 * Search methods.
 */
public class SearchUtils {
    private SearchUtils() {
    }

    public static String trimAndLowerCase(String value) {
        return (value != null) ? value.trim().toLowerCase() : null;
    }

    public static boolean isStringSet(String string) {
        return ((string != null) && !string.isEmpty());
    }

    public static boolean isSubstringFound(String trimmedLowerCasedSubstring, List<LocaleItem> localeItems) {
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

    public static boolean isSubstringFound(String trimmedLowerCasedSubstring, String item) {
        if (!isStringSet(trimmedLowerCasedSubstring)) {
            return false;
        }

        String trimmedLowerCasedItem = trimAndLowerCase(item);

        if (!isStringSet(trimmedLowerCasedItem)) {
            return false;
        }

        return trimmedLowerCasedItem.contains(trimmedLowerCasedSubstring);
    }
}
