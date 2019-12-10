package guess.util;

import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(Enclosed.class)
public class LocalizationUtilsTest {
    @RunWith(Parameterized.class)
    public static class GetNameTest {
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

        public GetNameTest(List<LocaleItem> localeItems, Language language, Language defaultLanguage, String expected) {
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

    @RunWith(Parameterized.class)
    public static class GetSpeakerNameWithCompanyTest {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            final List<LocaleItem> FULL_NAME_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Name"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Имя"));
            final List<LocaleItem> ENGLISH_NAME_LOCALE_ITEMS = Collections.singletonList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Name"));
            final List<LocaleItem> FULL_COMPANY_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Компания"));
            final List<LocaleItem> ENGLISH_COMPANY_LOCALE_ITEMS = Collections.singletonList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company"));

            return Arrays.asList(new Object[][]{
                    {new Speaker(0L, "0000.jpg", null, null), Language.ENGLISH, ""},
                    {new Speaker(0L, "0000.jpg", null, null), Language.RUSSIAN, ""},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, null), Language.ENGLISH, "Name"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, null), Language.RUSSIAN, "Имя"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, FULL_COMPANY_LOCALE_ITEMS), Language.ENGLISH, "Name (Company)"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, FULL_COMPANY_LOCALE_ITEMS), Language.RUSSIAN, "Имя (Компания)"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, ENGLISH_COMPANY_LOCALE_ITEMS), Language.RUSSIAN, "Имя (Company)"},
                    {new Speaker(0L, "0000.jpg", ENGLISH_NAME_LOCALE_ITEMS, FULL_COMPANY_LOCALE_ITEMS), Language.RUSSIAN, "Name (Компания)"},
                    {new Speaker(0L, "0000.jpg", ENGLISH_NAME_LOCALE_ITEMS, ENGLISH_COMPANY_LOCALE_ITEMS), Language.RUSSIAN, "Name (Company)"}
            });
        }

        private Speaker speaker;
        private Language language;
        private String expected;

        public GetSpeakerNameWithCompanyTest(Speaker speaker, Language language, String expected) {
            this.speaker = speaker;
            this.language = language;
            this.expected = expected;
        }

        @Test
        public void getSpeakerNameWithCompany() {
            assertEquals(expected, LocalizationUtils.getSpeakerNameWithCompany(speaker, language));
        }
    }
}
