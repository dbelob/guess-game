package guess.controller;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.LocaleItem;
import guess.domain.source.Talk;
import guess.service.EventService;
import guess.service.EventTypeService;
import guess.service.LocaleService;
import guess.service.TalkService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("TalkController class tests")
@WebMvcTest(TalkController.class)
class TalkControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private TalkService talkService;

    @MockBean
    private EventService eventService;

    @MockBean
    private EventTypeService eventTypeService;

    @MockBean
    private LocaleService localeService;

    @Test
    void getTalks() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        EventType eventType0 = new EventType();
        eventType0.setId(0);

        EventType eventType1 = new EventType();
        eventType1.setId(1);

        Event event0 = new Event();
        event0.setId(0);

        Event event1 = new Event();
        event1.setId(1);

        Talk talk0 = new Talk();
        talk0.setId(0);
        talk0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));

        Talk talk1 = new Talk();
        talk1.setId(1);
        talk1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));

        eventType0.setEvents(List.of(event0));
        eventType1.setEvents(List.of(event1));

        event0.setTalks(List.of(talk0));
        event1.setTalks(List.of(talk1));

        given(talkService.getTalks(0L, 1L, "Talk", "Speaker")).willReturn(List.of(talk1, talk0));
        given(eventService.getEventByTalk(talk0)).willReturn(event0);
        given(eventService.getEventByTalk(talk1)).willReturn(event1);
        given(eventTypeService.getEventTypeByEvent(event0)).willReturn(eventType0);
        given(eventTypeService.getEventTypeByEvent(event1)).willReturn(eventType1);
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/talk/talks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("eventTypeId", "0")
                .param("eventId", "1")
                .param("talkName", "Talk")
                .param("speakerName", "Speaker")
                .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[1].id", is(1)));
        Mockito.verify(talkService, VerificationModeFactory.times(1)).getTalks(0L, 1L, "Talk", "Speaker");
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }

    @Test
    void getTalk() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        Talk talk = new Talk();
        talk.setId(0);
        talk.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name")));

        given(talkService.getTalkById(0)).willReturn(talk);
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/talk/talk/0")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.talk.id", is(0)))
                .andExpect(jsonPath("$.talk.name", is("Name")));
        Mockito.verify(talkService, VerificationModeFactory.times(1)).getTalkById(0);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }
}
