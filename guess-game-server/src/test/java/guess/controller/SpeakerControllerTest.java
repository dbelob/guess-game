package guess.controller;

import guess.domain.Language;
import guess.domain.source.*;
import guess.dto.speaker.SpeakerBriefDto;
import guess.service.*;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SpeakerController class tests")
@WebMvcTest(SpeakerController.class)
class SpeakerControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private SpeakerService speakerService;

    @MockBean
    private TalkService talkService;

    @MockBean
    private EventService eventService;

    @MockBean
    private EventTypeService eventTypeService;

    @MockBean
    private LocaleService localeService;

    @Autowired
    private SpeakerController speakerController;

    @Test
    void getSpeakersByFirstLetter() throws Exception {
        final Language LANGUAGE = Language.ENGLISH;
        final String FIRST_LETTER = "a";

        MockHttpSession httpSession = new MockHttpSession();

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);

        given(localeService.getLanguage(httpSession)).willReturn(LANGUAGE);
        given(speakerService.getSpeakersByFirstLetter(FIRST_LETTER, LANGUAGE)).willReturn(new ArrayList<>(List.of(speaker0, speaker1)));

        mvc.perform(get("/api/speaker/first-letter-speakers")
                .contentType(MediaType.APPLICATION_JSON)
                .param("firstLetter", FIRST_LETTER)
                .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
        Mockito.verify(speakerService, VerificationModeFactory.times(1)).getSpeakersByFirstLetter(FIRST_LETTER, LANGUAGE);
    }

    @Test
    void getSpeakers() throws Exception {
        final String NAME = "name";
        final String COMPANY = "company";
        final String TWITTER = "twitter";
        final String GITHUB = "gitHub";
        final boolean JAVA_CHAMPION = true;
        final boolean MVP = false;

        MockHttpSession httpSession = new MockHttpSession();

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);

        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);
        given(speakerService.getSpeakers(NAME, COMPANY, TWITTER, GITHUB, JAVA_CHAMPION, MVP)).willReturn(new ArrayList<>(List.of(speaker0, speaker1)));

        mvc.perform(get("/api/speaker/speakers")
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", NAME)
                .param("company", COMPANY)
                .param("twitter", TWITTER)
                .param("gitHub", GITHUB)
                .param("javaChampion", Boolean.toString(JAVA_CHAMPION))
                .param("mvp", Boolean.toString(MVP))
                .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
        Mockito.verify(speakerService, VerificationModeFactory.times(1)).getSpeakers(NAME, COMPANY, TWITTER, GITHUB, JAVA_CHAMPION, MVP);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("convertToBriefDtoAndSort method tests")
    class ConvertToBriefDtoAndSortTest {
        private Stream<Arguments> data() {
            final Language LANGUAGE = Language.ENGLISH;

            Company company0 = new Company(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company0")));
            Company company1 = new Company(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Company1")));

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
            speaker0.setCompanies(List.of(company0));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));
            speaker1.setCompanies(List.of(company1));

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);
            speaker2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
            speaker2.setCompanies(List.of(company1));

            //TODO: fix (delete Collections.emptySet())
            SpeakerBriefDto speakerBriefDto0 = SpeakerBriefDto.convertToBriefDto(speaker0, LANGUAGE, Collections.emptySet());
            SpeakerBriefDto speakerBriefDto1 = SpeakerBriefDto.convertToBriefDto(speaker1, LANGUAGE, Collections.emptySet());
            SpeakerBriefDto speakerBriefDto2 = SpeakerBriefDto.convertToBriefDto(speaker2, LANGUAGE, Collections.emptySet());

            return Stream.of(
                    arguments(new ArrayList<>(List.of(speaker1, speaker0)), LANGUAGE, List.of(speakerBriefDto0, speakerBriefDto1)),
                    arguments(new ArrayList<>(List.of(speaker2, speaker0)), LANGUAGE, List.of(speakerBriefDto0, speakerBriefDto2))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void convertToBriefDtoAndSort(List<Speaker> speakers, Language language, List<SpeakerBriefDto> expected) {
            assertEquals(expected, speakerController.convertToBriefDtoAndSort(speakers, language));
        }
    }

    @Test
    void getSpeaker() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        Organizer organizer = new Organizer();
        organizer.setId(0);

        EventType eventType = new EventType();
        eventType.setId(0);
        eventType.setOrganizer(organizer);

        Event event = new Event();
        event.setId(0);
        event.setStartDate(LocalDate.of(2020, 10, 30));
        event.setEventType(eventType);

        Talk talk0 = new Talk();
        talk0.setId(0);
        talk0.setTalkDay(1L);

        Talk talk1 = new Talk();
        talk1.setId(1);
        talk1.setTalkDay(2L);

        Speaker speaker = new Speaker();
        speaker.setId(0);

        given(speakerService.getSpeakerById(0)).willReturn(speaker);
        given(talkService.getTalksBySpeaker(speaker)).willReturn(new ArrayList<>(List.of(talk0, talk1)));
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);
        given(eventService.getEventByTalk(talk0)).willReturn(event);
        given(eventService.getEventByTalk(talk1)).willReturn(event);
        given(eventTypeService.getEventTypeByEvent(event)).willReturn(eventType);

        mvc.perform(get("/api/speaker/speaker/0")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.speaker.id", is(0)))
                .andExpect(jsonPath("$.talks", hasSize(2)))
                .andExpect(jsonPath("$.talks[0].id", is(1)))
                .andExpect(jsonPath("$.talks[1].id", is(0)));
        Mockito.verify(speakerService, VerificationModeFactory.times(1)).getSpeakerById(0);
        Mockito.verify(talkService, VerificationModeFactory.times(1)).getTalksBySpeaker(speaker);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
        Mockito.verify(eventService, VerificationModeFactory.times(1)).getEventByTalk(talk0);
        Mockito.verify(eventService, VerificationModeFactory.times(1)).getEventByTalk(talk1);
    }
}
