package guess.controller;

import guess.domain.Language;
import guess.domain.source.*;
import guess.service.EventService;
import guess.service.EventTypeService;
import guess.service.LocaleService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("EventController class tests")
@WebMvcTest(EventController.class)
class EventControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private EventTypeService eventTypeService;

    @MockBean
    private LocaleService localeService;

    @Autowired
    private EventController eventController;

    @Test
    void getEvents() throws Exception {
        final boolean CONFERENCES = true;
        final boolean MEETUPS = true;
        final Long EVENT_TYPE_ID = 0L;

        MockHttpSession httpSession = new MockHttpSession();

        Event event0 = new Event();
        event0.setId(0);
        event0.setStartDate(LocalDate.of(2020, 10, 30));
        event0.setEndDate(LocalDate.of(2020, 10, 30));

        Event event1 = new Event();
        event1.setId(1);
        event1.setStartDate(LocalDate.of(2020, 10, 29));
        event1.setEndDate(LocalDate.of(2020, 10, 29));

        Event event2 = new Event();
        event2.setId(2);
        event2.setStartDate(LocalDate.of(2020, 10, 31));
        event2.setEndDate(LocalDate.of(2020, 10, 31));

        given(eventService.getEvents(CONFERENCES, MEETUPS, EVENT_TYPE_ID)).willReturn(new ArrayList<>(List.of(event0, event1, event2)));
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/event/events")
                .contentType(MediaType.APPLICATION_JSON)
                .param("conferences", Boolean.toString(CONFERENCES))
                .param("meetups", Boolean.toString(MEETUPS))
                .param("eventTypeId", "0")
                .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[1].id", is(0)))
                .andExpect(jsonPath("$[2].id", is(1)));
        Mockito.verify(eventService, VerificationModeFactory.times(1)).getEvents(CONFERENCES, MEETUPS, EVENT_TYPE_ID);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getDefaultEvent method tests")
    class GetDefaultEventTest {
        private Stream<Arguments> data() {
            Event event = new Event();
            event.setId(0);

            return Stream.of(
                    arguments(new Object[]{null}),
                    arguments(event)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getDefaultEvent(Event defaultEvent) throws Exception {
            MockHttpSession httpSession = new MockHttpSession();

            given(eventService.getDefaultEvent()).willReturn(defaultEvent);
            given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

            mvc.perform(get("/api/event/default-event")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(httpSession))
                    .andExpect(status().isOk());
            Mockito.verify(eventService, VerificationModeFactory.times(1)).getDefaultEvent();
            Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
            Mockito.reset(eventService, localeService);
        }
    }

    @Test
    void getEvent() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        EventType eventType = new EventType();
        eventType.setId(0);

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));

        Talk talk0 = new Talk();
        talk0.setId(0);
        talk0.setTalkDay(1L);
        talk0.setSpeakers(List.of(speaker1));
        talk0.setTalkDay(2L);

        Talk talk1 = new Talk();
        talk1.setId(1);
        talk1.setTalkDay(2L);
        talk1.setSpeakers(List.of(speaker0));
        talk1.setTalkDay(1L);

        Event event = new Event();
        event.setId(0);
        event.setStartDate(LocalDate.of(2020, 10, 30));
        event.setEndDate(LocalDate.of(2020, 10, 30));
        event.setTalks(List.of(talk0, talk1));

        given(eventService.getEventById(0)).willReturn(event);
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);
        given(eventService.getEventByTalk(talk0)).willReturn(event);
        given(eventService.getEventByTalk(talk1)).willReturn(event);
        given(eventTypeService.getEventTypeByEvent(event)).willReturn(eventType);

        mvc.perform(get("/api/event/event/0")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event.id", is(0)))
                .andExpect(jsonPath("$.speakers", hasSize(2)))
                .andExpect(jsonPath("$.speakers[0].id", is(0)))
                .andExpect(jsonPath("$.speakers[1].id", is(1)))
                .andExpect(jsonPath("$.talks", hasSize(2)))
                .andExpect(jsonPath("$.talks[0].id", is(1)))
                .andExpect(jsonPath("$.talks[1].id", is(0)));
        Mockito.verify(eventService, VerificationModeFactory.times(1)).getEventById(0);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
        Mockito.verify(eventService, VerificationModeFactory.times(1)).getEventByTalk(talk0);
        Mockito.verify(eventService, VerificationModeFactory.times(1)).getEventByTalk(talk1);
        Mockito.verify(eventTypeService, VerificationModeFactory.times(3)).getEventTypeByEvent(event);
    }
}
