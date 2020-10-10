package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.domain.Conference;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.domain.statistics.EventTypeMetrics;
import guess.domain.statistics.EventTypeStatistics;
import guess.domain.statistics.Metrics;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("StatisticsServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class StatisticsServiceImplTest {
    private static final LocalDate EVENT_START_DATE0 = LocalDate.of(2020, 1, 1);
    private static final LocalDate EVENT_END_DATE0 = LocalDate.of(2020, 1, 2);

    private static final LocalDate EVENT_START_DATE1 = LocalDate.of(2020, 9, 3);
    private static final LocalDate EVENT_END_DATE1 = LocalDate.of(2020, 9, 3);

    private static EventType eventType0;
    private static EventType eventType1;

    @TestConfiguration
    static class TestContextConfiguration {
        @MockBean
        EventTypeDao eventTypeDao;

        @MockBean
        EventDao eventDao;

        @Bean
        StatisticsService statisticsService() {
            return new StatisticsServiceImpl(eventTypeDao, eventDao);
        }
    }

    @Autowired
    EventTypeDao eventTypeDao;

    @Autowired
    EventDao eventDao;

    @Autowired
    StatisticsService statisticsService;

    @BeforeAll
    static void init() {
        eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);

        eventType1 = new EventType();
        eventType1.setId(1);

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setJavaChampion(true);

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setMvp(true);

        Talk talk0 = new Talk();
        talk0.setId(0);
        talk0.setSpeakers(List.of(speaker0));

        Talk talk1 = new Talk();
        talk1.setId(1);
        talk1.setSpeakers(List.of(speaker1));

        Event event0 = new Event();
        event0.setEventType(eventType0);
        event0.setStartDate(EVENT_START_DATE0);
        event0.setEndDate(EVENT_END_DATE0);
        event0.setTalks(List.of(talk0));

        Event event1 = new Event();
        event1.setEventType(eventType1);
        event1.setStartDate(EVENT_START_DATE1);
        event1.setEndDate(EVENT_END_DATE1);
        event1.setTalks(List.of(talk1));

        eventType0.setEvents(List.of(event0));
        eventType1.setEvents(List.of(event1));
    }

    @BeforeEach
    void setUp() {
        Mockito.when(eventTypeDao.getEventTypes()).thenReturn(List.of(eventType0, eventType1));
    }

    @Test
    void getEventTypeStatistics() {
        LocalDate now = LocalDate.now();

        EventTypeMetrics eventTypeMetrics0 = new EventTypeMetrics(
                eventType0,
                EVENT_START_DATE0,
                ChronoUnit.YEARS.between(EVENT_START_DATE0, now),
                2,
                1,
                1,
                new Metrics(1, 1, 0)
        );
        EventTypeMetrics eventTypeMetrics1 = new EventTypeMetrics(
                eventType1,
                EVENT_START_DATE1,
                ChronoUnit.YEARS.between(EVENT_START_DATE1, now),
                1,
                1,
                1,
                new Metrics(1, 0, 1)
        );

        EventTypeStatistics expected1 = new EventTypeStatistics(
                List.of(eventTypeMetrics1),
                new EventTypeMetrics(
                        new EventType(),
                        EVENT_START_DATE1,
                        ChronoUnit.YEARS.between(EVENT_START_DATE1, now),
                        1,
                        1,
                        1,
                        new Metrics(1, 0, 1)
                )
        );
        EventTypeStatistics expected2 = new EventTypeStatistics(
                List.of(eventTypeMetrics0),
                new EventTypeMetrics(
                        new EventType(),
                        EVENT_START_DATE0,
                        ChronoUnit.YEARS.between(EVENT_START_DATE0, now),
                        2,
                        1,
                        1,
                        new Metrics(1, 1, 0)
                )
        );
        EventTypeStatistics expected3 = new EventTypeStatistics(
                List.of(eventTypeMetrics0, eventTypeMetrics1),
                new EventTypeMetrics(
                        new EventType(),
                        EVENT_START_DATE0,
                        ChronoUnit.YEARS.between(EVENT_START_DATE0, now),
                        3,
                        2,
                        2,
                        new Metrics(2, 1, 1)
                )
        );

        EventTypeStatistics actual0 = statisticsService.getEventTypeStatistics(false, false);
        EventTypeStatistics expected0 = new EventTypeStatistics(
                Collections.emptyList(),
                new EventTypeMetrics(
                        new EventType(),
                        actual0.getTotals().getStartDate(),
                        0,
                        0,
                        0,
                        0,
                        new Metrics(0, 0, 0)
                )
        );
        assertEquals(expected0, actual0);

        assertEquals(expected1, statisticsService.getEventTypeStatistics(false, true));
        assertEquals(expected2, statisticsService.getEventTypeStatistics(true, false));
        assertEquals(expected3, statisticsService.getEventTypeStatistics(true, true));
    }

    @Test
    void getConferences() {
        assertEquals(List.of(eventType0), statisticsService.getConferences());
    }
}
