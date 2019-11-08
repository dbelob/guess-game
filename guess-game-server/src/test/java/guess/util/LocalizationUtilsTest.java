package guess.util;

import guess.domain.Language;
import guess.domain.source.LocaleItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class LocalizationUtilsTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        final List<LocaleItem> STANDARD_LOCALE_ITEMS = Arrays.asList(
                new LocaleItem(Language.ENGLISH.getCode(), "Text"),
                new LocaleItem(Language.RUSSIAN.getCode(), "Текст"));
        final List<LocaleItem> EMPTY_LOCALE_ITEMS = Collections.emptyList();
        final List<LocaleItem> ONLY_ENGLISH_LOCALE_ITEMS = Collections.singletonList(
                new LocaleItem(Language.ENGLISH.getCode(), "Text"));
        final List<LocaleItem> ONLY_RUSSIAN_LOCALE_ITEMS = Collections.singletonList(
                new LocaleItem(Language.RUSSIAN.getCode(), "Текст"));

        return Arrays.asList(new Object[][]{
                {STANDARD_LOCALE_ITEMS, Language.ENGLISH, Language.ENGLISH, "Text"},
                {STANDARD_LOCALE_ITEMS, Language.RUSSIAN, Language.ENGLISH, "Текст"},

                {EMPTY_LOCALE_ITEMS, Language.ENGLISH, Language.ENGLISH, ""},
                {EMPTY_LOCALE_ITEMS, Language.RUSSIAN, Language.ENGLISH, ""},

                {ONLY_ENGLISH_LOCALE_ITEMS, Language.ENGLISH, Language.ENGLISH, "Text"},
                {ONLY_ENGLISH_LOCALE_ITEMS, Language.RUSSIAN, Language.ENGLISH, "Text"},
                {ONLY_ENGLISH_LOCALE_ITEMS, Language.ENGLISH, Language.RUSSIAN, "Text"},
                {ONLY_ENGLISH_LOCALE_ITEMS, Language.RUSSIAN, Language.RUSSIAN, ""},

                {ONLY_RUSSIAN_LOCALE_ITEMS, Language.ENGLISH, Language.ENGLISH, ""},
                {ONLY_RUSSIAN_LOCALE_ITEMS, Language.RUSSIAN, Language.ENGLISH, "Текст"},
                {ONLY_RUSSIAN_LOCALE_ITEMS, Language.ENGLISH, Language.RUSSIAN, "Текст"},
                {ONLY_RUSSIAN_LOCALE_ITEMS, Language.RUSSIAN, Language.RUSSIAN, "Текст"}
        });
    }

    private List<LocaleItem> localeItems;
    private Language language;
    private Language defaultLanguage;
    private String expected;

    public LocalizationUtilsTest(List<LocaleItem> localeItems, Language language, Language defaultLanguage, String expected) {
        this.localeItems = localeItems;
        this.language = language;
        this.defaultLanguage = defaultLanguage;
        this.expected = expected;
    }

    @Test
    public void getName() {
        assertEquals(expected, LocalizationUtils.getName(localeItems, language, defaultLanguage));
    }
}
