package guess.util;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("LocalizationUtils class tests")
public class LocalizationUtilsTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getString method tests")
    class GetStringTest {
        private Stream<Arguments> data() {
            final List<LocaleItem> STANDARD_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Text"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Текст"));
            final List<LocaleItem> EMPTY_LOCALE_ITEMS = Collections.emptyList();
            final List<LocaleItem> ONLY_ENGLISH_LOCALE_ITEMS = Collections.singletonList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Text"));
            final List<LocaleItem> ONLY_RUSSIAN_LOCALE_ITEMS = Collections.singletonList(
                    new LocaleItem(Language.RUSSIAN.getCode(), "Текст"));

            return Stream.of(
                    arguments(null, null, null, ""),
                    arguments(null, null, Language.ENGLISH, ""),
                    arguments(null, Language.ENGLISH, null, ""),
                    arguments(null, Language.ENGLISH, Language.ENGLISH, ""),

                    arguments(STANDARD_LOCALE_ITEMS, Language.ENGLISH, Language.ENGLISH, "Text"),
                    arguments(STANDARD_LOCALE_ITEMS, Language.RUSSIAN, Language.ENGLISH, "Текст"),
                    arguments(STANDARD_LOCALE_ITEMS, null, Language.ENGLISH, "Text"),
                    arguments(STANDARD_LOCALE_ITEMS, Language.ENGLISH, null, "Text"),
                    arguments(STANDARD_LOCALE_ITEMS, null, null, ""),

                    arguments(EMPTY_LOCALE_ITEMS, Language.ENGLISH, Language.ENGLISH, ""),
                    arguments(EMPTY_LOCALE_ITEMS, Language.RUSSIAN, Language.ENGLISH, ""),

                    arguments(ONLY_ENGLISH_LOCALE_ITEMS, Language.ENGLISH, Language.ENGLISH, "Text"),
                    arguments(ONLY_ENGLISH_LOCALE_ITEMS, Language.RUSSIAN, Language.ENGLISH, "Text"),
                    arguments(ONLY_ENGLISH_LOCALE_ITEMS, Language.ENGLISH, Language.RUSSIAN, "Text"),
                    arguments(ONLY_ENGLISH_LOCALE_ITEMS, Language.RUSSIAN, Language.RUSSIAN, ""),

                    arguments(ONLY_RUSSIAN_LOCALE_ITEMS, Language.ENGLISH, Language.ENGLISH, ""),
                    arguments(ONLY_RUSSIAN_LOCALE_ITEMS, Language.RUSSIAN, Language.ENGLISH, "Текст"),
                    arguments(ONLY_RUSSIAN_LOCALE_ITEMS, Language.ENGLISH, Language.RUSSIAN, "Текст"),
                    arguments(ONLY_RUSSIAN_LOCALE_ITEMS, Language.RUSSIAN, Language.RUSSIAN, "Текст")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getString(List<LocaleItem> localeItems, Language language, Language defaultLanguage, String expected) {
            assertEquals(expected, LocalizationUtils.getString(localeItems, language, defaultLanguage));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getResourceString method tests")
    class GetResourceStringTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments("allEventsOptionText", Language.ENGLISH, "All events of selected types"),
                    arguments("allEventsOptionText", Language.RUSSIAN, "Все события выбранных типов")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getResourceString(String key, Language language, String expected) {
            assertEquals(expected, LocalizationUtils.getResourceString(key, language));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakerCompanies method tests")
    class GetSpeakerCompaniesTest {
        private Stream<Arguments> data() {
            Company company0 = new Company(0, List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company0"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Компания0")));
            Company company1 = new Company(1, List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company1"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Компания1")
            ));

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setCompanies(Collections.emptyList());

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setCompanies(List.of(company0));

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);
            speaker2.setCompanies(List.of(company0, company1));

            return Stream.of(
                    arguments(speaker0, Language.ENGLISH, ""),
                    arguments(speaker1, Language.ENGLISH, "Company0"),
                    arguments(speaker1, Language.RUSSIAN, "Компания0"),
                    arguments(speaker2, Language.ENGLISH, "Company0, Company1"),
                    arguments(speaker2, Language.RUSSIAN, "Компания0, Компания1")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakerCompanies(Speaker speaker, Language language, String expected) {
            assertEquals(expected, LocalizationUtils.getSpeakerCompanies(speaker, language));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakerNameWithCompanies method tests")
    class GetSpeakerNameWithCompaniesTest {
        private Stream<Arguments> data() {
            final List<LocaleItem> FULL_NAME_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Name"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Имя"));
            final List<LocaleItem> ENGLISH_NAME_LOCALE_ITEMS = Collections.singletonList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Name"));

            Company company0 = new Company(0, List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company0"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Компания0")));
            Company company1 = new Company(1, List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company1")));

            final List<Company> EMPTY_COMPANY_LIST = Collections.emptyList();
            final List<Company> FULL_LANGUAGE_COMPANY_LIST = List.of(company0);
            final List<Company> ENGLISH_LANGUAGE_COMPANY_LIST = List.of(company1);
            final List<Company> SOME_COMPANY_LIST = List.of(company0, company1);
            final List<Company> SOME_COMPANY_REVERSE_LIST = List.of(company1, company0);

            return Stream.of(
                    arguments(new Speaker(
                                    0L,
                                    "0000.jpg",
                                    null,
                                    null,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.ENGLISH, ""),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    null,
                                    null,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, ""),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    null,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.ENGLISH, "Name"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    null,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "Имя"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    null,
                                    EMPTY_COMPANY_LIST,
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.ENGLISH, "Name"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    null,
                                    EMPTY_COMPANY_LIST,
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "Имя"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    null,
                                    FULL_LANGUAGE_COMPANY_LIST,
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.ENGLISH, "Name (Company0)"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    null,
                                    FULL_LANGUAGE_COMPANY_LIST,
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "Имя (Компания0)"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    null,
                                    ENGLISH_LANGUAGE_COMPANY_LIST,
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "Имя (Company1)"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    ENGLISH_NAME_LOCALE_ITEMS,
                                    null,
                                    FULL_LANGUAGE_COMPANY_LIST,
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "Name (Компания0)"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    ENGLISH_NAME_LOCALE_ITEMS,
                                    null,
                                    ENGLISH_LANGUAGE_COMPANY_LIST,
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "Name (Company1)"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    ENGLISH_NAME_LOCALE_ITEMS,
                                    null,
                                    SOME_COMPANY_LIST,
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.ENGLISH, "Name (Company0, Company1)"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    ENGLISH_NAME_LOCALE_ITEMS,
                                    null,
                                    SOME_COMPANY_REVERSE_LIST,
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.ENGLISH, "Name (Company0, Company1)")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakerNameWithCompanies(Speaker speaker, Language language, String expected) {
            assertEquals(expected, LocalizationUtils.getSpeakerNameWithCompanies(speaker, language));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakerNameWithLastNameFirstWithCompany method tests")
    class GetSpeakerNameWithLastNameFirstWithCompanyTest {
        private Stream<Arguments> data() {
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

            return Stream.of(
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    null,
                                    null,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.ENGLISH, ""),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    null,
                                    null,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, ""),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    null,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.ENGLISH, "LastName FirstName"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    null,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "Фамилия Имя"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    EMPTY_COMPANY_LOCALE_ITEMS,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.ENGLISH, "LastName FirstName"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    EMPTY_COMPANY_LOCALE_ITEMS,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "Фамилия Имя"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    FULL_COMPANY_LOCALE_ITEMS,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.ENGLISH, "LastName FirstName (Company)"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    FULL_COMPANY_LOCALE_ITEMS,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "Фамилия Имя (Компания)"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    FULL_NAME_LOCALE_ITEMS,
                                    ENGLISH_COMPANY_LOCALE_ITEMS,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "Фамилия Имя (Company)"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    ENGLISH_NAME_LOCALE_ITEMS,
                                    FULL_COMPANY_LOCALE_ITEMS,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "LastName FirstName (Компания)"),
                    arguments(new Speaker(0L,
                                    "0000.jpg",
                                    ENGLISH_NAME_LOCALE_ITEMS,
                                    ENGLISH_COMPANY_LOCALE_ITEMS,
                                    Collections.emptyList(),
                                    null,
                                    new Speaker.SpeakerSocials(
                                            null,
                                            null
                                    ),
                                    new Speaker.SpeakerDegrees(
                                            false,
                                            false,
                                            false
                                    )
                            ),
                            Language.RUSSIAN, "LastName FirstName (Company)")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakerNameWithLastNameFirstWithCompany(Speaker speaker, Language language, String expected) {
            assertEquals(expected, LocalizationUtils.getSpeakerNameWithLastNameFirstWithCompany(speaker, language));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakerName method tests")
    class GetSpeakerNameTest {
        private Stream<Arguments> data() {
            final List<LocaleItem> FULL_NAME_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "FirstName LastName"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Имя Фамилия"));

            Company company = new Company(0, List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Компания")));

            Speaker speaker0 = new Speaker(
                    0L,
                    "0000.jpg",
                    FULL_NAME_LOCALE_ITEMS,
                    null,
                    List.of(company),
                    null,
                    new Speaker.SpeakerSocials(
                            null,
                            null
                    ),
                    new Speaker.SpeakerDegrees(
                            false,
                            false,
                            false
                    )
            );

            Set<Speaker> EMPTY_SPEAKER_DUPLICATES = Collections.emptySet();
            Set<Speaker> FULL_SPEAKER_DUPLICATES = Set.of(speaker0);

            return Stream.of(
                    arguments(speaker0, Language.ENGLISH, EMPTY_SPEAKER_DUPLICATES, "FirstName LastName"),
                    arguments(speaker0, Language.ENGLISH, FULL_SPEAKER_DUPLICATES, "FirstName LastName (Company)")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakerName(Speaker speaker, Language language, Set<Speaker> speakerDuplicates, String expected) {
            assertEquals(expected, LocalizationUtils.getSpeakerName(speaker, language, speakerDuplicates));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakerNameWithLastNameFirst method tests")
    class GetSpeakerNameWithLastNameFirstTest {
        private Stream<Arguments> data() {
            final List<LocaleItem> FULL_NAME_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "FirstName LastName"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Имя Фамилия"));
            final List<LocaleItem> FULL_COMPANY_LOCALE_ITEMS = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), "Company"),
                    new LocaleItem(Language.RUSSIAN.getCode(), "Компания"));

            Speaker speaker0 = new Speaker(0L,
                    "0000.jpg",
                    FULL_NAME_LOCALE_ITEMS,
                    FULL_COMPANY_LOCALE_ITEMS,
                    Collections.emptyList(),
                    null,
                    new Speaker.SpeakerSocials(
                            null,
                            null
                    ),
                    new Speaker.SpeakerDegrees(
                            false,
                            false,
                            false
                    )
            );

            Set<Speaker> EMPTY_SPEAKER_DUPLICATES = Collections.emptySet();
            Set<Speaker> FULL_SPEAKER_DUPLICATES = Set.of(speaker0);

            return Stream.of(
                    arguments(speaker0, Language.ENGLISH, EMPTY_SPEAKER_DUPLICATES, "LastName FirstName"),
                    arguments(speaker0, Language.ENGLISH, FULL_SPEAKER_DUPLICATES, "LastName FirstName (Company)")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakerNameWithLastNameFirst(Speaker speaker, Language language, Set<Speaker> speakerDuplicates, String expected) {
            assertEquals(expected, LocalizationUtils.getSpeakerNameWithLastNameFirst(speaker, language, speakerDuplicates));
        }
    }
}
