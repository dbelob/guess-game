package guess.service;

import guess.dao.SpeakerDao;
import guess.domain.Language;
import guess.domain.source.Company;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("SpeakerServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class SpeakerServiceImplTest {
    private static Speaker speaker0;
    private static Speaker speaker1;
    private static Speaker speaker2;
    private static Speaker speaker3;

    @BeforeAll
    static void init() {
        Company company0 = new Company(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company0")));
        Company company1 = new Company(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company1")));
        Company company2 = new Company(2, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company2")));

        speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
        speaker0.setCompanies(List.of(company0));
        speaker0.setCompanyIds(List.of(0L));
        speaker0.setTwitter("twitter0");
        speaker0.setGitHub("github0");

        speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));
        speaker1.setCompanies(List.of(company1));
        speaker1.setCompanyIds(List.of(1L));
        speaker1.setTwitter("twitter1");
        speaker1.setGitHub("github1");
        speaker1.setJavaChampion(true);

        speaker2 = new Speaker();
        speaker2.setId(2);
        speaker2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name2")));
        speaker2.setCompanies(List.of(company2));
        speaker2.setCompanyIds(List.of(2L));
        speaker2.setTwitter("twitter2");
        speaker2.setGitHub("github2");
        speaker2.setMvp(true);

        speaker3 = new Speaker();
        speaker3.setId(3);
        speaker3.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "First3 Last3")));
        speaker3.setCompanies(List.of(company2));
        speaker3.setCompanyIds(List.of(2L));
        speaker3.setTwitter("twitter3");
        speaker3.setGitHub("github3");
    }

    @Test
    void getSpeakerById() {
        SpeakerDao speakerDao = Mockito.mock(SpeakerDao.class);
        SpeakerService speakerService = new SpeakerServiceImpl(speakerDao);

        Mockito.when(speakerDao.getSpeakerById(Mockito.anyLong())).thenReturn(speaker0);

        speakerService.getSpeakerById(0);
        Mockito.verify(speakerDao, VerificationModeFactory.times(1)).getSpeakerById(0);
        Mockito.verifyNoMoreInteractions(speakerDao);
    }

    @Test
    void getSpeakerByIds() {
        final List<Long> IDS = List.of(0L, 1L, 2L);

        SpeakerDao speakerDao = Mockito.mock(SpeakerDao.class);
        SpeakerService speakerService = new SpeakerServiceImpl(speakerDao);

        Mockito.when(speakerDao.getSpeakerByIds(Mockito.anyList())).thenReturn(List.of(speaker0, speaker1, speaker2));

        speakerService.getSpeakerByIds(IDS);
        Mockito.verify(speakerDao, VerificationModeFactory.times(1)).getSpeakerByIds(IDS);
        Mockito.verifyNoMoreInteractions(speakerDao);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakersByFirstLetter method tests")
    class GetSpeakersByFirstLetterTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, Collections.emptyList(), null, Collections.emptyList()),
                    arguments("n", Language.ENGLISH, List.of(speaker0), "", Collections.emptyList()),
                    arguments("n", Language.RUSSIAN, List.of(speaker0), "", Collections.emptyList()),
                    arguments("n", Language.ENGLISH, List.of(speaker0), " ", Collections.emptyList()),
                    arguments("n", Language.ENGLISH, List.of(speaker0), "N", List.of(speaker0))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakersByFirstLetter(String firstLetter, Language language, List<Speaker> speakers,
                                      String localizationString, List<Speaker> expected) {
            try (MockedStatic<LocalizationUtils> mockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
                mockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                        .thenAnswer(
                                (Answer<String>) invocation -> {
                                    Object[] args = invocation.getArguments();

                                    return Language.ENGLISH.equals(args[1]) ? localizationString : null;
                                }
                        );

                SpeakerDao speakerDaoMock = Mockito.mock(SpeakerDao.class);
                Mockito.when(speakerDaoMock.getSpeakers()).thenReturn(speakers);

                SpeakerService speakerService = new SpeakerServiceImpl(speakerDaoMock);

                assertEquals(expected, speakerService.getSpeakersByFirstLetter(firstLetter, language));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakersByFirstLetters method tests")
    class GetSpeakersByFirstLettersTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, Collections.emptyList(), Collections.emptyList()),
                    arguments("n", Language.ENGLISH, List.of(speaker0), List.of(speaker0)),
                    arguments("N", Language.ENGLISH, List.of(speaker0), List.of(speaker0)),
                    arguments("n", Language.ENGLISH, List.of(speaker0, speaker1), List.of(speaker0, speaker1)),
                    arguments("N", Language.ENGLISH, List.of(speaker0, speaker1), List.of(speaker0, speaker1)),
                    arguments("n", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker0, speaker1, speaker2)),
                    arguments("N", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker0, speaker1, speaker2)),
                    arguments("name", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker0, speaker1, speaker2)),
                    arguments("NAME", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker0, speaker1, speaker2)),
                    arguments("Name", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker0, speaker1, speaker2)),
                    arguments("name0", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker0)),
                    arguments("NAME0", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker0)),
                    arguments("Name0", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker0)),
                    arguments("name1", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker1)),
                    arguments("NAME1", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker1)),
                    arguments("Name1", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker1)),
                    arguments("name2", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker2)),
                    arguments("NAME2", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker2)),
                    arguments("Name2", Language.ENGLISH, List.of(speaker0, speaker1, speaker2), List.of(speaker2))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void getSpeakersByFirstLetters(String firstLetters, Language language, List<Speaker> speakers, List<Speaker> expected) {
            try (MockedStatic<LocalizationUtils> mockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
                mockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                        .thenAnswer(
                                (Answer<String>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    List<LocaleItem> localeItems = (List<LocaleItem>) args[0];

                                    return !localeItems.isEmpty() ? localeItems.get(0).getText() : "";
                                }
                        );

                SpeakerDao speakerDaoMock = Mockito.mock(SpeakerDao.class);
                Mockito.when(speakerDaoMock.getSpeakers()).thenReturn(speakers);

                SpeakerService speakerService = new SpeakerServiceImpl(speakerDaoMock);

                assertEquals(expected, speakerService.getSpeakersByFirstLetters(firstLetters, language));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakers method tests")
    class GetSpeakersTest {
        private Stream<Arguments> data() {
            List<Speaker> speakers = List.of(speaker0, speaker1, speaker2, speaker3);

            return Stream.of(
                    arguments(null, null, null, null, false, false, Collections.emptyList(), Collections.emptyList()),
                    arguments(null, null, null, null, false, false, speakers, Collections.emptyList()),
                    arguments("0", null, null, null, false, false, speakers, List.of(speaker0)),
                    arguments("7", null, null, null, false, false, speakers, Collections.emptyList()),
                    arguments(null, "0", null, null, false, false, speakers, List.of(speaker0)),
                    arguments(null, "7", null, null, false, false, speakers, Collections.emptyList()),
                    arguments(null, null, "0", null, false, false, speakers, List.of(speaker0)),
                    arguments(null, null, "7", null, false, false, speakers, Collections.emptyList()),
                    arguments(null, null, null, "0", false, false, speakers, List.of(speaker0)),
                    arguments(null, null, null, "7", false, false, speakers, Collections.emptyList()),
                    arguments(null, null, null, null, true, false, speakers, List.of(speaker1)),
                    arguments(null, null, null, null, false, true, speakers, List.of(speaker2)),
                    arguments(null, null, null, null, true, true, speakers, Collections.emptyList()),
                    arguments("0", "0", "0", "0", false, false, speakers, List.of(speaker0)),
                    arguments(null, null, "1", null, true, false, speakers, List.of(speaker1)),
                    arguments(null, null, null, "2", false, true, speakers, List.of(speaker2)),
                    arguments("name", null, null, null, false, false, speakers, List.of(speaker0, speaker1, speaker2)),
                    arguments("Last3 First3", null, null, null, false, false, speakers, List.of(speaker3))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakers(String name, String company, String twitter, String gitHub, boolean isJavaChampion, boolean isMvp,
                         List<Speaker> speakers, List<Speaker> expected) {
            SpeakerDao speakerDao = Mockito.mock(SpeakerDao.class);
            SpeakerService speakerService = new SpeakerServiceImpl(speakerDao);

            Mockito.when(speakerDao.getSpeakers()).thenReturn(speakers);

            assertEquals(expected, speakerService.getSpeakers(name, company, twitter, gitHub, isJavaChampion, isMvp));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakersByCompanyId method tests")
    class GetSpeakersByCompanyIdTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(0L, Collections.emptyList(), Collections.emptyList()),
                    arguments(0L, List.of(speaker0, speaker1, speaker2, speaker3), List.of(speaker0)),
                    arguments(1L, List.of(speaker0, speaker1, speaker2, speaker3), List.of(speaker1)),
                    arguments(2L, List.of(speaker0, speaker1, speaker2, speaker3), List.of(speaker2, speaker3)),
                    arguments(3L, List.of(speaker0, speaker1, speaker2, speaker3), Collections.emptyList())
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakersByCompanyId(long companyId, List<Speaker> speakers, List<Speaker> expected) {
            SpeakerDao speakerDao = Mockito.mock(SpeakerDao.class);
            SpeakerService speakerService = new SpeakerServiceImpl(speakerDao);

            Mockito.when(speakerDao.getSpeakers()).thenReturn(speakers);

            assertEquals(expected, speakerService.getSpeakersByCompanyId(companyId));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isSpeakerCompanyFound method tests")
    class IsSpeakerCompanyFoundTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(speaker0, "company0", true),
                    arguments(speaker0, "company", true),
                    arguments(speaker0, "0", true),
                    arguments(speaker0, "1", false)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void isSpeakerCompanyFound(Speaker speaker, String trimmedLowerCasedCompany, boolean expected) {
            assertEquals(expected, SpeakerServiceImpl.isSpeakerCompanyFound(speaker, trimmedLowerCasedCompany));
        }
    }
}
