package guess.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Organizer;
import guess.service.EventService;
import guess.service.LocaleService;
import guess.service.OrganizerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("EventTypeController class tests")
@WebMvcTest(OrganizerController.class)
class OrganizerControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrganizerService organizerService;

    @MockBean
    private EventService eventService;

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

        given(organizerService.getOrganizers()).willReturn(List.of(organizer0, organizer1));
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/organizer/organizers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        Mockito.verify(organizerService, VerificationModeFactory.times(1)).getOrganizers();
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getDefaultOrganizer method tests")
    class GetDefaultOrganizerTest {
        private Stream<Arguments> data() {
            Organizer organizer0 = new Organizer();
            organizer0.setId(0);

            EventType eventType0 = new EventType();
            eventType0.setId(0);
            eventType0.setOrganizer(organizer0);

            Event event0 = new Event();
            event0.setId(0);
            event0.setEventType(eventType0);

            return Stream.of(
                    arguments((Event) null),
                    arguments(event0)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getDefaultEventOrganizer(Event defaultEvent) throws Exception {
            final boolean IS_CONFERENCES = Boolean.TRUE;
            final boolean IS_MEETUPS = Boolean.TRUE;

            MockHttpSession httpSession = new MockHttpSession();

            given(eventService.getDefaultEvent(IS_CONFERENCES, IS_MEETUPS)).willReturn(defaultEvent);

            if (defaultEvent != null) {
                given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

                MvcResult mvcResult = mvc.perform(get("/api/organizer/default-event-organizer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(httpSession))
                        .andExpect(status().isOk())
                        .andReturn();
                String body = mvcResult.getResponse().getContentAsString();

                assertFalse(body.isBlank());

                Mockito.verify(eventService, VerificationModeFactory.times(1)).getDefaultEvent(IS_CONFERENCES, IS_MEETUPS);
                Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
            } else {
                MvcResult mvcResult = mvc.perform(get("/api/organizer/default-event-organizer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(httpSession))
                        .andExpect(status().isOk())
                        .andReturn();
                String body = mvcResult.getResponse().getContentAsString();

                assertTrue(body.isBlank());

                Mockito.verify(eventService, VerificationModeFactory.times(1)).getDefaultEvent(IS_CONFERENCES, IS_MEETUPS);
            }
        }
    }
}
