package guess.service;

import guess.dao.CompanyDao;
import guess.dao.SpeakerDao;
import guess.domain.Language;
import guess.domain.source.Company;
import guess.domain.source.Speaker;
import guess.domain.statistics.company.CompanySearchResult;
import guess.util.LocalizationUtils;
import guess.util.SearchUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("CompanyServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class CompanyServiceImplTest {
    private static final Company company0 = new Company(0, Collections.emptyList());
    private static final Company company1 = new Company(1, Collections.emptyList());
    private static final Company company2 = new Company(2, Collections.emptyList());

    private static final Speaker speaker0 = new Speaker(0, new Speaker.SpeakerPhoto(null, null),
            Collections.emptyList(), List.of(company0), null, new Speaker.SpeakerSocials(null, null, null),
            new Speaker.SpeakerDegrees(true, false, false));
    private static final Speaker speaker1 = new Speaker(1, new Speaker.SpeakerPhoto(null, null),
            Collections.emptyList(), List.of(company1), null, new Speaker.SpeakerSocials(null, null, null),
            new Speaker.SpeakerDegrees(false, true, false));

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        CompanyDao companyDao() {
            CompanyDao companyDao = Mockito.mock(CompanyDao.class);

            Mockito.when(companyDao.getCompanies()).thenReturn(List.of(company0, company1, company2));
            Mockito.when(companyDao.getCompanyById(Mockito.anyLong())).thenReturn(company0);
            Mockito.when(companyDao.getCompaniesByIds(Mockito.anyList())).thenReturn(List.of(company0, company1, company2));

            return companyDao;
        }

        @Bean
        SpeakerDao speakerDao() {
            SpeakerDao speakerDao = Mockito.mock(SpeakerDao.class);

            Mockito.when(speakerDao.getSpeakers()).thenReturn(List.of(speaker0, speaker1));

            return speakerDao;
        }

        @Bean
        CompanyService companyService() {
            return new CompanyServiceImpl(companyDao(), speakerDao());
        }
    }

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private CompanyService companyService;

    @Test
    void getCompanies() {
        companyService.getCompanies();
        Mockito.verify(companyDao, VerificationModeFactory.times(1)).getCompanies();
        Mockito.verifyNoMoreInteractions(companyDao);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getCompanies method tests")
    class GetCompaniesTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, Collections.emptyList(), false, Collections.emptyList()),
                    arguments("name", List.of(company0, company1), false, Collections.emptyList()),
                    arguments("name", List.of(company0, company1), true, List.of(company0, company1))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getCompanies(String name, List<Company> companies, boolean isSubstringFound, List<Company> expected) {
            try (MockedStatic<SearchUtils> mockedStatic = Mockito.mockStatic(SearchUtils.class)) {
                mockedStatic.when(() -> SearchUtils.trimAndLowerCase(Mockito.anyString()))
                        .thenCallRealMethod();
                mockedStatic.when(() -> SearchUtils.isStringSet(Mockito.anyString()))
                        .thenCallRealMethod();
                mockedStatic.when(() -> SearchUtils.isSubstringFound(Mockito.anyString(), Mockito.anyList()))
                        .thenReturn(isSubstringFound);

                CompanyDao companyDaoMock = Mockito.mock(CompanyDao.class);
                Mockito.when(companyDaoMock.getCompanies()).thenReturn(companies);

                SpeakerDao speakerDao = Mockito.mock(SpeakerDao.class);

                CompanyService companyService = new CompanyServiceImpl(companyDaoMock, speakerDao);

                assertEquals(expected, companyService.getCompanies(name));
            }
        }
    }

    @Test
    void getCompanyById() {
        final long ID = 0;

        companyService.getCompanyById(ID);
        Mockito.verify(companyDao, VerificationModeFactory.times(1)).getCompanyById(ID);
        Mockito.verifyNoMoreInteractions(companyDao);
    }

    @Test
    void getCompaniesByIds() {
        final List<Long> IDS = List.of(0L, 1L, 2L);

        companyService.getCompaniesByIds(IDS);
        Mockito.verify(companyDao, VerificationModeFactory.times(1)).getCompaniesByIds(IDS);
        Mockito.verifyNoMoreInteractions(companyDao);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getCompaniesByFirstLetter method tests")
    class GetCompaniesByFirstLetterTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(false, null, null, Collections.emptyList(), null, Collections.emptyList()),
                    arguments(false, "n", Language.ENGLISH, List.of(company0), "", Collections.emptyList()),
                    arguments(false, "n", Language.RUSSIAN, List.of(company0), "", Collections.emptyList()),
                    arguments(false, "n", Language.ENGLISH, List.of(company0), " ", Collections.emptyList()),
                    arguments(false, "n", Language.ENGLISH, List.of(company0), "N", List.of(company0)),
                    arguments(true, null, Language.ENGLISH, List.of(company0), null, Collections.emptyList()),
                    arguments(true, null, Language.ENGLISH, List.of(company0), "N", Collections.emptyList()),
                    arguments(true, null, Language.ENGLISH, List.of(company0), "0", List.of(company0))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getCompaniesByFirstLetter(boolean isDigit, String firstLetter, Language language, List<Company> companies,
                                       String localizationString, List<Company> expected) {
            try (MockedStatic<LocalizationUtils> mockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
                mockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                        .thenAnswer(
                                (Answer<String>) invocation -> {
                                    Object[] args = invocation.getArguments();

                                    return Language.ENGLISH.equals(args[1]) ? localizationString : null;
                                }
                        );

                CompanyDao companyDaoMock = Mockito.mock(CompanyDao.class);
                Mockito.when(companyDaoMock.getCompanies()).thenReturn(companies);

                SpeakerDao speakerDao = Mockito.mock(SpeakerDao.class);

                CompanyService companyService = new CompanyServiceImpl(companyDaoMock, speakerDao);

                assertEquals(expected, companyService.getCompaniesByFirstLetter(isDigit, firstLetter, language));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getCompaniesByFirstLetters method tests")
    class GetCompaniesByFirstLettersTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, Collections.emptyList(), null, Collections.emptyList()),
                    arguments("", null, Collections.emptyList(), null, Collections.emptyList()),
                    arguments("", Language.ENGLISH, List.of(company0), "", List.of(company0)),
                    arguments("", Language.ENGLISH, List.of(company0), "N", List.of(company0)),
                    arguments("na", Language.ENGLISH, List.of(company0), "Name", List.of(company0)),
                    arguments("na", Language.ENGLISH, List.of(company0, company1), "Name", List.of(company0, company1)),
                    arguments("an", Language.ENGLISH, List.of(company0), "Name", Collections.emptyList()),
                    arguments("an", Language.ENGLISH, List.of(company0, company1), "Name", Collections.emptyList())
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getCompaniesByFirstLetters(String firstLetters, Language language, List<Company> companies,
                                        String localizationString, List<Company> expected) {
            try (MockedStatic<LocalizationUtils> mockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
                mockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                        .thenReturn(localizationString);

                CompanyDao companyDaoMock = Mockito.mock(CompanyDao.class);
                Mockito.when(companyDaoMock.getCompanies()).thenReturn(companies);

                SpeakerDao speakerDao = Mockito.mock(SpeakerDao.class);

                CompanyService companyService = new CompanyServiceImpl(companyDaoMock, speakerDao);

                assertEquals(expected, companyService.getCompaniesByFirstLetters(firstLetters, language));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getCompanySearchResults method tests")
    class GetCompanySearchResultsTest {
        private Stream<Arguments> data() {
            CompanySearchResult companySearchResult0 = new CompanySearchResult(company0, 1, 1, 0);
            CompanySearchResult companySearchResult1 = new CompanySearchResult(company1, 1, 0, 1);
            CompanySearchResult companySearchResult2 = new CompanySearchResult(company2, 0, 0, 0);

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),
                    arguments(Collections.emptyList(), List.of(speaker0, speaker1), Collections.emptyList()),
                    arguments(List.of(company0), List.of(speaker0, speaker1), List.of(companySearchResult0)),
                    arguments(List.of(company1), List.of(speaker0, speaker1), List.of(companySearchResult1)),
                    arguments(List.of(company2), List.of(speaker0, speaker1), List.of(companySearchResult2)),
                    arguments(List.of(company0, company1, company2), List.of(speaker0, speaker1),
                            List.of(companySearchResult0, companySearchResult1, companySearchResult2))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getCompanySearchResults(List<Company> companies, List<Speaker> speakers, List<CompanySearchResult> expected) {
            CompanyDao companyDaoMock = Mockito.mock(CompanyDao.class);

            SpeakerDao speakerDao = Mockito.mock(SpeakerDao.class);
            Mockito.when(speakerDao.getSpeakers()).thenReturn(speakers);

            CompanyService companyService = new CompanyServiceImpl(companyDaoMock, speakerDao);

            assertEquals(expected, companyService.getCompanySearchResults(companies));
        }
    }
}
