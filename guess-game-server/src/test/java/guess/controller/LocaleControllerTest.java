package guess.controller;

import guess.domain.Language;
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

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("LocaleController class tests")
@WebMvcTest(LocaleController.class)
class LocaleControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private LocaleService localeService;

    @Test
    void getLanguage() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/locale/language")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("ENGLISH")));
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
        Mockito.reset(localeService);
    }

    @Test
    void setLanguage() throws Exception {
        Language language = Language.ENGLISH;
        MockHttpSession httpSession = new MockHttpSession();

        mvc.perform(put("/api/locale/language")
                .contentType(MediaType.APPLICATION_JSON)
                .content(language.toString())
                .session(httpSession))
                .andExpect(status().isOk());
        Mockito.verify(localeService, VerificationModeFactory.times(1)).setLanguage(language, httpSession);
        Mockito.reset(localeService);
    }
}
