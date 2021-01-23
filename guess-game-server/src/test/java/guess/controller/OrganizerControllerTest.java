package guess.controller;

import guess.domain.Language;
import guess.domain.source.Organizer;
import guess.service.LocaleService;
import guess.service.OrganizerService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("EventTypeController class tests")
@WebMvcTest(OrganizerController.class)
class OrganizerControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrganizerService organizerService;

    @MockBean
    private LocaleService localeService;

    @Autowired
    private OrganizerController organizerController;

    @Test
    void getOrganizers() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        Organizer organizer0 = new Organizer();
        organizer0.setId(0);

        Organizer organizer1 = new Organizer();
        organizer1.setId(1);

        given(organizerService.getOrganizers()).willReturn(new ArrayList<>(List.of(organizer0, organizer1)));
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/organizer/organizers")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        Mockito.verify(organizerService, VerificationModeFactory.times(1)).getOrganizers();
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }
}
