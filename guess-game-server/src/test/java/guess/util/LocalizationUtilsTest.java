package guess.util;

import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static junit.framework.TestCase.assertEquals;

@RunWith(Enclosed.class)
public class LocalizationUtilsTest {
    @RunWith(Parameterized.class)
    public static class GetStringTest {
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
                    {null, null, null, ""},
                    {null, null, Language.ENGLISH, ""},
                    {null, Language.ENGLISH, Language.ENGLISH, ""},

                    {STANDARD_LOCALE_ITEMS, Language.ENGLISH, Language.ENGLISH, "Text"},
                    {STANDARD_LOCALE_ITEMS, Language.RUSSIAN, Language.ENGLISH, "Текст"},
                    {STANDARD_LOCALE_ITEMS, null, Language.ENGLISH, "Text"},

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

        private final List<LocaleItem> localeItems;
        private final Language language;
        private final Language defaultLanguage;
        private final String expected;

        public GetStringTest(List<LocaleItem> localeItems, Language language, Language defaultLanguage, String expected) {
            this.localeItems = localeItems;
            this.language = language;
            this.defaultLanguage = defaultLanguage;
            this.expected = expected;
        }

        @Test
        public void getString() {
            assertEquals(expected, LocalizationUtils.getString(localeItems, language, defaultLanguage));
        }
    }

    @RunWith(Parameterized.class)
    public static class GetResourceStringTest {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"allEventsOptionText", Language.ENGLISH, "All events of selected types"},
                    {"allEventsOptionText", Language.RUSSIAN, "Все события выбранных типов"}
            });
        }

        private final String key;
        private final Language language;
        private final String expected;

        public GetResourceStringTest(String key, Language language, String expected) {
            this.key = key;
            this.language = language;
            this.expected = expected;
        }

        @Test
        public void getResourceString() {
            assertEquals(expected, LocalizationUtils.getResourceString(key, language));
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

            final List<LocaleItem> EMPTY_COMPANY_LOCALE_ITEMS = Collections.emptyList();
            final List<LocaleItem> FULL_COMPANY_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Компания"));
            final List<LocaleItem> ENGLISH_COMPANY_LOCALE_ITEMS = Collections.singletonList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company"));

            return Arrays.asList(new Object[][]{
                    {new Speaker(0L, "0000.jpg", null, null, null, null, null, false, false, false), Language.ENGLISH, ""},
                    {new Speaker(0L, "0000.jpg", null, null, null, null, null, false, false, false), Language.RUSSIAN, ""},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, null, null, null, null, false, false, false), Language.ENGLISH, "Name"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, null, null, null, null, false, false, false), Language.RUSSIAN, "Имя"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, EMPTY_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.ENGLISH, "Name"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, EMPTY_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.RUSSIAN, "Имя"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, FULL_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.ENGLISH, "Name (Company)"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, FULL_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.RUSSIAN, "Имя (Компания)"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, ENGLISH_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.RUSSIAN, "Имя (Company)"},
                    {new Speaker(0L, "0000.jpg", ENGLISH_NAME_LOCALE_ITEMS, FULL_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.RUSSIAN, "Name (Компания)"},
                    {new Speaker(0L, "0000.jpg", ENGLISH_NAME_LOCALE_ITEMS, ENGLISH_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.RUSSIAN, "Name (Company)"}
            });
        }

        private final Speaker speaker;
        private final Language language;
        private final String expected;

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

    @RunWith(Parameterized.class)
    public static class GetSpeakerNameWithLastNameFirstWithCompanyTest {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            final List<LocaleItem> FULL_NAME_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "FirstName LastName"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Имя Фамилия"));
            final List<LocaleItem> ENGLISH_NAME_LOCALE_ITEMS = Collections.singletonList(
                    new LocaleItem(Language.ENGLISH.getCode(), "FirstName LastName"));

            final List<LocaleItem> EMPTY_COMPANY_LOCALE_ITEMS = Collections.emptyList();
            final List<LocaleItem> FULL_COMPANY_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Компания"));
            final List<LocaleItem> ENGLISH_COMPANY_LOCALE_ITEMS = Collections.singletonList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company"));

            return Arrays.asList(new Object[][]{
                    {new Speaker(0L, "0000.jpg", null, null, null, null, null, false, false, false), Language.ENGLISH, ""},
                    {new Speaker(0L, "0000.jpg", null, null, null, null, null, false, false, false), Language.RUSSIAN, ""},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, null, null, null, null, false, false, false), Language.ENGLISH, "LastName FirstName"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, null, null, null, null, false, false, false), Language.RUSSIAN, "Фамилия Имя"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, EMPTY_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.ENGLISH, "LastName FirstName"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, EMPTY_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.RUSSIAN, "Фамилия Имя"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, FULL_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.ENGLISH, "LastName FirstName (Company)"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, FULL_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.RUSSIAN, "Фамилия Имя (Компания)"},
                    {new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, ENGLISH_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.RUSSIAN, "Фамилия Имя (Company)"},
                    {new Speaker(0L, "0000.jpg", ENGLISH_NAME_LOCALE_ITEMS, FULL_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.RUSSIAN, "LastName FirstName (Компания)"},
                    {new Speaker(0L, "0000.jpg", ENGLISH_NAME_LOCALE_ITEMS, ENGLISH_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false), Language.RUSSIAN, "LastName FirstName (Company)"}
            });
        }

        private final Speaker speaker;
        private final Language language;
        private final String expected;

        public GetSpeakerNameWithLastNameFirstWithCompanyTest(Speaker speaker, Language language, String expected) {
            this.speaker = speaker;
            this.language = language;
            this.expected = expected;
        }

        @Test
        public void getSpeakerNameWithLastNameFirstWithCompany() {
            assertEquals(expected, LocalizationUtils.getSpeakerNameWithLastNameFirstWithCompany(speaker, language));
        }
    }

    @RunWith(Parameterized.class)
    public static class GetSpeakerNameTest {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            final List<LocaleItem> FULL_NAME_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "FirstName LastName"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Имя Фамилия"));
            final List<LocaleItem> FULL_COMPANY_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Компания"));

            Speaker speaker0 = new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, FULL_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false);

            Set<Speaker> EMPTY_SPEAKER_DUPLICATES = Collections.emptySet();
            Set<Speaker> FULL_SPEAKER_DUPLICATES = Set.of(speaker0);

            return Arrays.asList(new Object[][]{
                    {speaker0, Language.ENGLISH, EMPTY_SPEAKER_DUPLICATES, "FirstName LastName"},
                    {speaker0, Language.ENGLISH, FULL_SPEAKER_DUPLICATES, "FirstName LastName (Company)"}
            });
        }

        private final Speaker speaker;
        private final Language language;
        private final Set<Speaker> speakerDuplicates;
        private final String expected;

        public GetSpeakerNameTest(Speaker speaker, Language language, Set<Speaker> speakerDuplicates, String expected) {
            this.speaker = speaker;
            this.language = language;
            this.speakerDuplicates = speakerDuplicates;
            this.expected = expected;
        }

        @Test
        public void getSpeakerName() {
            assertEquals(expected, LocalizationUtils.getSpeakerName(speaker, language, speakerDuplicates));
        }
    }

    @RunWith(Parameterized.class)
    public static class GetSpeakerNameWithLastNameFirstTest {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            final List<LocaleItem> FULL_NAME_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "FirstName LastName"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Имя Фамилия"));
            final List<LocaleItem> FULL_COMPANY_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Компания"));

            Speaker speaker0 = new Speaker(0L, "0000.jpg", FULL_NAME_LOCALE_ITEMS, FULL_COMPANY_LOCALE_ITEMS, null, null, null, false, false, false);

            Set<Speaker> EMPTY_SPEAKER_DUPLICATES = Collections.emptySet();
            Set<Speaker> FULL_SPEAKER_DUPLICATES = Set.of(speaker0);

            return Arrays.asList(new Object[][]{
                    {speaker0, Language.ENGLISH, EMPTY_SPEAKER_DUPLICATES, "LastName FirstName"},
                    {speaker0, Language.ENGLISH, FULL_SPEAKER_DUPLICATES, "LastName FirstName (Company)"}
            });
        }

        private final Speaker speaker;
        private final Language language;
        private final Set<Speaker> speakerDuplicates;
        private final String expected;

        public GetSpeakerNameWithLastNameFirstTest(Speaker speaker, Language language, Set<Speaker> speakerDuplicates, String expected) {
            this.speaker = speaker;
            this.language = language;
            this.speakerDuplicates = speakerDuplicates;
            this.expected = expected;
        }

        @Test
        public void getSpeakerNameWithLastNameFirst() {
            assertEquals(expected, LocalizationUtils.getSpeakerNameWithLastNameFirst(speaker, language, speakerDuplicates));
        }
    }
}
