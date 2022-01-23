package guess.controller;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import guess.domain.statistics.company.CompanySearchResult;
import guess.dto.common.SelectedEntitiesDto;
import guess.service.CompanyService;
import guess.service.LocaleService;
import guess.service.SpeakerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CompanyController class tests")
@WebMvcTest(CompanyController.class)
class CompanyControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CompanyService companyService;

    @MockBean
    private SpeakerService speakerService;

    @MockBean
    private LocaleService localeService;

    @Test
    void getCompaniesByFirstLetters() throws Exception {
        final Language LANGUAGE = Language.ENGLISH;
        final String FIRST_LETTERS = "c";

        MockHttpSession httpSession = new MockHttpSession();

        Company company0 = new Company(0, List.of(new LocaleItem(LANGUAGE.getCode(), "Company0")));
        Company company1 = new Company(1, List.of(new LocaleItem(LANGUAGE.getCode(), "Company1")));
        Company company2 = new Company(2, List.of(new LocaleItem(LANGUAGE.getCode(), "Company2")));

        given(companyService.getCompaniesByFirstLetters(FIRST_LETTERS, LANGUAGE)).willReturn(List.of(company2, company1, company0));
        given(localeService.getLanguage(httpSession)).willReturn(LANGUAGE);

        mvc.perform(get("/api/company/first-letters-companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstLetters", FIRST_LETTERS)
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].name", is("Company0")))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].name", is("Company1")))
                .andExpect(jsonPath("$[2].id", is(2)))
                .andExpect(jsonPath("$[2].name", is("Company2")));
        Mockito.verify(companyService, VerificationModeFactory.times(1)).getCompaniesByFirstLetters(FIRST_LETTERS, LANGUAGE);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }

    @Test
    void getSelectedCompanies() throws Exception {
        final Language LANGUAGE = Language.ENGLISH;
        final List<Long> IDS = List.of(0L, 1L, 2L);

        MockHttpSession httpSession = new MockHttpSession();

        SelectedEntitiesDto selectedEntities = new SelectedEntitiesDto();
        selectedEntities.setIds(IDS);

        Company company0 = new Company(0, List.of(new LocaleItem(LANGUAGE.getCode(), "Company0")));
        Company company1 = new Company(1, List.of(new LocaleItem(LANGUAGE.getCode(), "Company1")));
        Company company2 = new Company(2, List.of(new LocaleItem(LANGUAGE.getCode(), "Company2")));

        given(companyService.getCompaniesByIds(IDS)).willReturn(List.of(company2, company1, company0));
        given(localeService.getLanguage(httpSession)).willReturn(LANGUAGE);

        mvc.perform(post("/api/company/selected-companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(selectedEntities))
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].name", is("Company0")))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].name", is("Company1")))
                .andExpect(jsonPath("$[2].id", is(2)))
                .andExpect(jsonPath("$[2].name", is("Company2")));
        Mockito.verify(companyService, VerificationModeFactory.times(1)).getCompaniesByIds(IDS);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }

    @Test
    void getCompanyNamesByFirstLetters() throws Exception {
        final Language LANGUAGE = Language.ENGLISH;
        final String FIRST_LETTERS = "c";

        MockHttpSession httpSession = new MockHttpSession();

        Company company0 = new Company(0, List.of(new LocaleItem(LANGUAGE.getCode(), "Company0")));
        Company company1 = new Company(1, List.of(new LocaleItem(LANGUAGE.getCode(), "Company1")));
        Company company2 = new Company(2, List.of(new LocaleItem(LANGUAGE.getCode(), "Company2")));

        given(companyService.getCompaniesByFirstLetters(FIRST_LETTERS, LANGUAGE)).willReturn(List.of(company2, company1, company0));
        given(localeService.getLanguage(httpSession)).willReturn(LANGUAGE);

        mvc.perform(get("/api/company/first-letters-company-names")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstLetters", FIRST_LETTERS)
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Company0")))
                .andExpect(jsonPath("$[1]", is("Company1")))
                .andExpect(jsonPath("$[2]", is("Company2")));
        Mockito.verify(companyService, VerificationModeFactory.times(1)).getCompaniesByFirstLetters(FIRST_LETTERS, LANGUAGE);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }

    @Test
    void getCompany() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        Company company0 = new Company(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company0")));

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
        speaker0.setCompanies(List.of(company0));

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));
        speaker1.setCompanies(List.of(company0));

        given(companyService.getCompanyById(0)).willReturn(company0);
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);
        given(speakerService.getSpeakersByCompanyId(0)).willReturn(List.of(speaker1, speaker0));

        mvc.perform(get("/api/company/company/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company.id", is(0)))
                .andExpect(jsonPath("$.speakers", hasSize(2)))
                .andExpect(jsonPath("$.speakers[0].id", is(0)))
                .andExpect(jsonPath("$.speakers[1].id", is(1)));
        Mockito.verify(companyService, VerificationModeFactory.times(1)).getCompanyById(0);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
        Mockito.verify(speakerService, VerificationModeFactory.times(1)).getSpeakersByCompanyId(0);
    }

    @Test
    void getCompaniesByFirstLetter() throws Exception {
        final boolean IS_DIGIT = false;
        final String FIRST_LETTER = "a";
        final Language LANGUAGE = Language.ENGLISH;

        MockHttpSession httpSession = new MockHttpSession();

        Company company0 = new Company();
        company0.setId(0);
        company0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));

        Company company1 = new Company();
        company1.setId(1);
        company1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));

        CompanySearchResult companySearchResult0 = new CompanySearchResult(company0, 1, 0, 0);
        CompanySearchResult companySearchResult1 = new CompanySearchResult(company1, 1, 0, 0);

        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);
        given(companyService.getCompaniesByFirstLetter(IS_DIGIT, FIRST_LETTER, LANGUAGE)).willReturn(List.of(company1, company0));
        given(companyService.getCompanySearchResults(Mockito.anyList())).willReturn(List.of(companySearchResult1, companySearchResult0));

        mvc.perform(get("/api/company/first-letter-companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("digit", Boolean.toString(IS_DIGIT))
                        .param("firstLetter", FIRST_LETTER)
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[1].id", is(1)));
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
        Mockito.verify(companyService, VerificationModeFactory.times(1)).getCompaniesByFirstLetter(IS_DIGIT, FIRST_LETTER, LANGUAGE);
        Mockito.verify(companyService, VerificationModeFactory.times(1)).getCompanySearchResults(Mockito.anyList());
    }
}
