package guess.controller;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.domain.source.LocaleItem;
import guess.service.CompanyService;
import guess.service.LocaleService;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private LocaleService localeService;

    @Test
    void getCompanyNames() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        Company company0 = new Company(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company0")));
        Company company1 = new Company(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company1")));
        Company company2 = new Company(2, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company2")));

        given(companyService.getCompanies()).willReturn(new ArrayList<>(List.of(company2, company1, company0)));
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/company/company-names")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Company0")))
                .andExpect(jsonPath("$[1]", is("Company1")))
                .andExpect(jsonPath("$[2]", is("Company2")));
        Mockito.verify(companyService, VerificationModeFactory.times(1)).getCompanies();
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }
}
