package guess.controller;

import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.statistics.Metrics;
import guess.domain.statistics.company.CompanyMetrics;
import guess.domain.statistics.company.CompanyStatistics;
import guess.domain.statistics.event.EventMetrics;
import guess.domain.statistics.event.EventStatistics;
import guess.domain.statistics.eventtype.EventTypeMetrics;
import guess.domain.statistics.eventtype.EventTypeStatistics;
import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.speaker.SpeakerMetrics;
import guess.domain.statistics.speaker.SpeakerStatistics;
import guess.service.LocaleService;
import guess.service.OlapService;
import guess.service.StatisticsService;
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
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("StatisticsController class tests")
@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private StatisticsService statisticsService;

    @MockBean
    private OlapService olapService;

    @MockBean
    private LocaleService localeService;

    @Test
    void getEventTypeStatistics() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        boolean conferences = true;
        boolean meetups = false;

        Organizer organizer0 = new Organizer(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
        Organizer organizer1 = new Organizer(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));

        EventType eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setOrganizer(organizer1);

        EventType eventType1 = new EventType();
        eventType1.setId(1);
        eventType1.setOrganizer(organizer0);

        EventTypeMetrics eventTypeMetrics0 = new EventTypeMetrics(
                eventType0,
                LocalDate.of(2016, 11, 1), 4, 4, 1, 20,
                new Metrics(21, 3, 0)
        );

        EventTypeMetrics eventTypeMetrics1 = new EventTypeMetrics(
                eventType1,
                LocalDate.of(2018, 1, 1), 2, 10, 4, 40,
                new Metrics(61, 5, 1)
        );

        EventTypeMetrics eventTypeMetricsTotals = new EventTypeMetrics(
                new EventType(),
                LocalDate.of(2016, 11, 1), 4, 14, 5, 60,
                new Metrics(82, 7, 1)
        );

        EventTypeStatistics eventTypeStatistics = new EventTypeStatistics(
                List.of(eventTypeMetrics0, eventTypeMetrics1),
                eventTypeMetricsTotals);

        given(statisticsService.getEventTypeStatistics(conferences, meetups, null)).willReturn(eventTypeStatistics);
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/statistics/event-type-statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("conferences", Boolean.toString(conferences))
                        .param("meetups", Boolean.toString(meetups))
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventTypeMetricsList", hasSize(2)))
                .andExpect(jsonPath("$.eventTypeMetricsList[0].id", is(1)))
                .andExpect(jsonPath("$.eventTypeMetricsList[1].id", is(0)))
                .andExpect(jsonPath("$.totals.age", is(4)));
        Mockito.verify(statisticsService, VerificationModeFactory.times(1)).getEventTypeStatistics(conferences, meetups, null);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }

    @Test
    void getEventStatistics() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        Long organizerId = 0L;
        Long eventTypeId = 0L;

        Event event0 = new Event();
        event0.setId(0);

        Event event1 = new Event();
        event1.setId(1);

        EventMetrics eventMetrics0 = new EventMetrics(
                event0,
                LocalDate.of(2016, 11, 1), 4, 21, 20, 3, 0
        );

        EventMetrics eventMetrics1 = new EventMetrics(
                event1,
                LocalDate.of(2018, 1, 1), 10, 61, 40, 5, 1
        );

        EventMetrics eventMetricsTotals = new EventMetrics(
                new Event(),
                LocalDate.of(2016, 11, 1), 14, 81, 60, 5, 1
        );

        EventStatistics eventStatistics = new EventStatistics(
                List.of(eventMetrics0, eventMetrics1),
                eventMetricsTotals);

        given(statisticsService.getEventStatistics(organizerId, eventTypeId)).willReturn(eventStatistics);
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/statistics/event-statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("organizerId", Long.toString(organizerId))
                        .param("eventTypeId", Long.toString(eventTypeId))
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventMetricsList", hasSize(2)))
                .andExpect(jsonPath("$.eventMetricsList[0].id", is(0)))
                .andExpect(jsonPath("$.eventMetricsList[1].id", is(1)))
                .andExpect(jsonPath("$.totals.duration", is(14)));
        Mockito.verify(statisticsService, VerificationModeFactory.times(1)).getEventStatistics(organizerId, eventTypeId);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }

    @Test
    void getSpeakerStatistics() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        boolean conferences = true;
        boolean meetups = false;
        Long organizerId = null;
        Long eventTypeId = 0L;

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);

        SpeakerMetrics speakerMetrics0 = new SpeakerMetrics(
                speaker0, 21, 15, 10, 1, 0);

        SpeakerMetrics speakerMetrics1 = new SpeakerMetrics(
                speaker1, 61, 20, 15, 0, 1);

        SpeakerMetrics speakerMetricsTotals = new SpeakerMetrics(
                new Speaker(),
                82, 35, 25, 1, 1);

        SpeakerStatistics speakerStatistics = new SpeakerStatistics(
                List.of(speakerMetrics0, speakerMetrics1),
                speakerMetricsTotals);

        given(statisticsService.getSpeakerStatistics(conferences, meetups, organizerId, eventTypeId)).willReturn(speakerStatistics);
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/statistics/speaker-statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("conferences", Boolean.toString(conferences))
                        .param("meetups", Boolean.toString(meetups))
                        .param("eventTypeId", Long.toString(eventTypeId))
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.speakerMetricsList", hasSize(2)))
                .andExpect(jsonPath("$.speakerMetricsList[0].id", is(1)))
                .andExpect(jsonPath("$.speakerMetricsList[1].id", is(0)))
                .andExpect(jsonPath("$.totals.talksQuantity", is(82)));
        Mockito.verify(statisticsService, VerificationModeFactory.times(1)).getSpeakerStatistics(conferences, meetups, organizerId, eventTypeId);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }

    @Test
    void getCompanyStatistics() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        boolean conferences = true;
        boolean meetups = false;
        Long organizerId = null;
        Long eventTypeId = 0L;

        Company company0 = new Company();
        company0.setId(0);

        Company company1 = new Company();
        company1.setId(1);

        CompanyMetrics companyMetrics0 = new CompanyMetrics(
                company0, 20, 21, 15, 10, 1, 0);

        CompanyMetrics companyMetrics1 = new CompanyMetrics(
                company1, 42, 61, 20, 15, 0, 1);

        CompanyMetrics companyMetricsTotals = new CompanyMetrics(
                new Company(),
                60, 82, 35, 25, 1, 1);

        CompanyStatistics companyStatistics = new CompanyStatistics(
                List.of(companyMetrics0, companyMetrics1),
                companyMetricsTotals);

        given(statisticsService.getCompanyStatistics(conferences, meetups, organizerId, eventTypeId)).willReturn(companyStatistics);
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/statistics/company-statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("conferences", Boolean.toString(conferences))
                        .param("meetups", Boolean.toString(meetups))
                        .param("eventTypeId", Long.toString(eventTypeId))
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyMetricsList", hasSize(2)))
                .andExpect(jsonPath("$.companyMetricsList[0].id", is(1)))
                .andExpect(jsonPath("$.companyMetricsList[1].id", is(0)))
                .andExpect(jsonPath("$.totals.speakersQuantity", is(60)));
        Mockito.verify(statisticsService, VerificationModeFactory.times(1)).getCompanyStatistics(conferences, meetups, organizerId, eventTypeId);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);
    }

    @Test
    void getCubeTypes() throws Exception {
        mvc.perform(get("/api/statistics/cube-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("EVENT_TYPES")))
                .andExpect(jsonPath("$[1]", is("SPEAKERS")))
                .andExpect(jsonPath("$[2]", is("COMPANIES")));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getMeasureTypes method with parameters tests")
    class GetMeasureTypesTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(""),
                    arguments("EVENT_TYPES")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getMeasureTypes(String cubeType) throws Exception {
            if (cubeType.isEmpty()) {
                mvc.perform(get("/api/statistics/measure-types")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("cubeType", cubeType))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)));
                Mockito.verifyNoMoreInteractions(olapService);
            } else {
                mvc.perform(get("/api/statistics/measure-types")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("cubeType", cubeType))
                        .andExpect(status().isOk());
                Mockito.verify(olapService, VerificationModeFactory.times(1)).getMeasureTypes(CubeType.valueOf(cubeType));
                Mockito.verifyNoMoreInteractions(olapService);
            }
        }
    }
}
