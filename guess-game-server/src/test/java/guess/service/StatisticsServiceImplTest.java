package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.domain.Conference;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.domain.statistics.*;
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
    private static final LocalDate NOW_DATE;

    private static final LocalDate EVENT_START_DATE0;
    private static final LocalDate EVENT_END_DATE0;

    private static final LocalDate EVENT_START_DATE1;
    private static final LocalDate EVENT_END_DATE1;

    private static final LocalDate EVENT_START_DATE2;
    private static final LocalDate EVENT_END_DATE2;

    private static EventType eventType0;
    private static EventType eventType1;
    private static EventType eventType2;
    private static Event event0;
    private static Event event1;
    private static Event event2;

    static {
        NOW_DATE = LocalDate.now();

        EVENT_START_DATE0 = NOW_DATE.minus(3, ChronoUnit.DAYS);
        EVENT_END_DATE0 = EVENT_START_DATE0.plus(1, ChronoUnit.DAYS);

        EVENT_START_DATE1 = NOW_DATE.plus(3, ChronoUnit.DAYS);
        EVENT_END_DATE1 = EVENT_START_DATE1;

        EVENT_START_DATE2 = NOW_DATE.plus(7, ChronoUnit.DAYS);
        EVENT_END_DATE2 = EVENT_START_DATE2;
    }

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

        eventType2 = new EventType();
        eventType2.setId(2);
        eventType2.setConference(Conference.JOKER);

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setJavaChampion(true);

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setMvp(true);

        Speaker speaker2 = new Speaker();
        speaker2.setId(2);

        Talk talk0 = new Talk();
        talk0.setId(0);
        talk0.setSpeakers(List.of(speaker0));

        Talk talk1 = new Talk();
        talk1.setId(1);
        talk1.setSpeakers(List.of(speaker1));

        Talk talk2 = new Talk();
        talk2.setId(2);
        talk2.setSpeakers(List.of(speaker2));

        event0 = new Event();
        event0.setEventType(eventType0);
        event0.setStartDate(EVENT_START_DATE0);
        event0.setEndDate(EVENT_END_DATE0);
        event0.setTalks(List.of(talk0));

        event1 = new Event();
        event1.setEventType(eventType1);
        event1.setStartDate(EVENT_START_DATE1);
        event1.setEndDate(EVENT_END_DATE1);
        event1.setTalks(List.of(talk1));

        event2 = new Event();
        event2.setEventType(eventType2);
        event2.setStartDate(EVENT_START_DATE2);
        event2.setEndDate(EVENT_END_DATE2);
        event2.setTalks(List.of(talk2));

        eventType0.setEvents(List.of(event0));
        eventType1.setEvents(List.of(event1));
        eventType2.setEvents(List.of(event2));
    }

    @BeforeEach
    void setUp() {
        Mockito.when(eventTypeDao.getEventTypes()).thenReturn(List.of(eventType0, eventType1, eventType2));
        Mockito.when(eventDao.getEvents()).thenReturn(List.of(event0, event1, event2));
    }

    private EventTypeStatistics createEventTypeStatistics(List<EventTypeMetrics> eventTypeMetricsList,
                                                          EventType eventType, LocalDate startDate, long age, long duration,
                                                          long eventsQuantity, long speakersQuantity, long talksQuantity,
                                                          long javaChampionsQuantity, long mvpsQuantity) {
        return new EventTypeStatistics(
                eventTypeMetricsList,
                new EventTypeMetrics(
                        eventType,
                        startDate,
                        age,
                        duration,
                        eventsQuantity,
                        speakersQuantity,
                        new Metrics(talksQuantity, javaChampionsQuantity, mvpsQuantity)
                )
        );
    }

    @Test
    void getEventTypeStatistics() {
        EventTypeMetrics eventTypeMetrics0 = new EventTypeMetrics(
                eventType0,
                EVENT_START_DATE0,
                ChronoUnit.YEARS.between(EVENT_START_DATE0, NOW_DATE),
                2,
                1,
                1,
                new Metrics(1, 1, 0)
        );
        EventTypeMetrics eventTypeMetrics1 = new EventTypeMetrics(
                eventType1,
                NOW_DATE,
                ChronoUnit.YEARS.between(EVENT_START_DATE1, NOW_DATE),
                1,
                1,
                1,
                new Metrics(1, 0, 1)
        );
        EventTypeMetrics eventTypeMetrics2 = new EventTypeMetrics(
                eventType2,
                NOW_DATE,
                ChronoUnit.YEARS.between(EVENT_START_DATE2, NOW_DATE),
                1,
                1,
                1,
                new Metrics(1, 0, 0)
        );

        EventTypeStatistics actual0 = statisticsService.getEventTypeStatistics(false, false);
        EventTypeStatistics expected0 = createEventTypeStatistics(
                Collections.emptyList(),
                new EventType(),
                actual0.getTotals().getStartDate(),
                0,
                0,
                0,
                0,
                0, 0, 0
        );
        assertEquals(expected0, actual0);

        EventTypeStatistics actual1 = statisticsService.getEventTypeStatistics(false, true);
        EventTypeStatistics expected1 = createEventTypeStatistics(
                List.of(eventTypeMetrics1),
                new EventType(),
                actual1.getTotals().getStartDate(),
                ChronoUnit.YEARS.between(EVENT_START_DATE1, NOW_DATE),
                1,
                1,
                1,
                1, 0, 1
        );
        assertEquals(expected1, actual1);

        EventTypeStatistics expected2 = createEventTypeStatistics(
                List.of(eventTypeMetrics0, eventTypeMetrics2),
                new EventType(),
                EVENT_START_DATE0,
                ChronoUnit.YEARS.between(EVENT_START_DATE0, NOW_DATE),
                3,
                2,
                2,
                2, 1, 0
        );
        assertEquals(expected2, statisticsService.getEventTypeStatistics(true, false));

        EventTypeStatistics expected3 = createEventTypeStatistics(
                List.of(eventTypeMetrics0, eventTypeMetrics1, eventTypeMetrics2),
                new EventType(),
                EVENT_START_DATE0,
                ChronoUnit.YEARS.between(EVENT_START_DATE0, NOW_DATE),
                4,
                3,
                3,
                3, 1, 1
        );
        assertEquals(expected3, statisticsService.getEventTypeStatistics(true, true));
    }

    private EventStatistics createEventStatistics(List<EventMetrics> eventMetricsList, Event event, LocalDate startDate,
                                                  long duration, long talksQuantity, long speakersQuantity,
                                                  long javaChampionsQuantity, long mvpsQuantity) {
        return new EventStatistics(
                eventMetricsList,
                new EventMetrics(
                        event,
                        startDate,
                        duration,
                        talksQuantity,
                        speakersQuantity,
                        javaChampionsQuantity,
                        mvpsQuantity
                )
        );
    }

    @Test
    void getEventStatistics() {
        EventMetrics eventMetrics0 = new EventMetrics(
                event0,
                EVENT_START_DATE0,
                2,
                1,
                1,
                1,
                0);
        EventMetrics eventMetrics2 = new EventMetrics(
                event2,
                EVENT_START_DATE2,
                1,
                1,
                1,
                0,
                0);

        EventStatistics expected0 = createEventStatistics(
                List.of(eventMetrics0, eventMetrics2),
                new Event(),
                EVENT_START_DATE0,
                3,
                2,
                2,
                1,
                0
        );
        assertEquals(expected0, statisticsService.getEventStatistics(null));

        EventStatistics expected1 = createEventStatistics(
                List.of(eventMetrics0),
                new Event(),
                EVENT_START_DATE0,
                2,
                1,
                1,
                1,
                0
        );
        assertEquals(expected1, statisticsService.getEventStatistics(0L));

        EventStatistics actual2 = statisticsService.getEventStatistics(1L);
        EventStatistics expected2 = createEventStatistics(
                Collections.emptyList(),
                new Event(),
                actual2.getTotals().getStartDate(),
                0,
                0,
                0,
                0,
                0
        );
        assertEquals(expected2, actual2);

        EventStatistics actual3 = statisticsService.getEventStatistics(2L);
        EventStatistics expected3 = createEventStatistics(
                List.of(eventMetrics2),
                new Event(),
                actual3.getTotals().getStartDate(),
                1,
                1,
                1,
                0,
                0
        );
        assertEquals(expected3, actual3);

        EventStatistics actual4 = statisticsService.getEventStatistics(3L);
        EventStatistics expected4 = createEventStatistics(
                Collections.emptyList(),
                new Event(),
                actual4.getTotals().getStartDate(),
                0,
                0,
                0,
                0,
                0
        );
        assertEquals(expected4, actual4);
    }

    @Test
    void getConferences() {
        assertEquals(List.of(eventType0, eventType2), statisticsService.getConferences());
    }
}
