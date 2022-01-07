package guess.controller;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.domain.source.LocaleItem;
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
}
