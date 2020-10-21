package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.domain.Conference;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Talk;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
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

@DisplayName("EventServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class EventServiceImplTest {
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
    static class EventServiceImplTestConfiguration {
        @MockBean
        EventDao eventDao;

        @MockBean
        EventTypeDao eventTypeDao;

        @Bean
        EventService eventService() {
            return new EventServiceImpl(eventDao, eventTypeDao);
        }
    }

    @Autowired
    EventDao eventDao;

    @Autowired
    EventTypeDao eventTypeDao;

    @Autowired
    EventService eventService;

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

        event0 = new Event();
        event0.setEventType(eventType0);
        event0.setStartDate(EVENT_START_DATE0);
        event0.setEndDate(EVENT_END_DATE0);

        event1 = new Event();
        event1.setEventType(eventType1);
        event1.setStartDate(EVENT_START_DATE1);
        event1.setEndDate(EVENT_END_DATE1);

        event2 = new Event();
        event2.setEventType(eventType2);
        event2.setStartDate(EVENT_START_DATE2);
        event2.setEndDate(EVENT_END_DATE2);

        eventType0.setEvents(List.of(event0));
        eventType1.setEvents(List.of(event1));
        eventType2.setEvents(List.of(event2));
    }

    @BeforeEach
    void setUp() {
        Mockito.when(eventTypeDao.getEventTypes()).thenReturn(List.of(eventType0, eventType1, eventType2));
    }

    @Test
    void getEventById() {
        eventService.getEventById(0);
        Mockito.verify(eventDao, VerificationModeFactory.times(1)).getEventById(0);
        Mockito.verifyNoMoreInteractions(eventDao);
    }

    @Test
    void getEvents() {
        assertEquals(Collections.emptyList(), eventService.getEvents(false, false, null));
        assertEquals(List.of(event1), eventService.getEvents(false, true, null));
        assertEquals(List.of(event0, event2), eventService.getEvents(true, false, null));
        assertEquals(List.of(event0, event1, event2), eventService.getEvents(true, true, null));

        assertEquals(Collections.emptyList(), eventService.getEvents(false, false, 0L));
        assertEquals(Collections.emptyList(), eventService.getEvents(false, true, 0L));
        assertEquals(List.of(event0), eventService.getEvents(true, false, 0L));
        assertEquals(List.of(event0), eventService.getEvents(true, true, 0L));

        assertEquals(Collections.emptyList(), eventService.getEvents(false, false, 1L));
        assertEquals(List.of(event1), eventService.getEvents(false, true, 1L));
        assertEquals(Collections.emptyList(), eventService.getEvents(true, false, 1L));
        assertEquals(List.of(event1), eventService.getEvents(true, true, 1L));

        assertEquals(Collections.emptyList(), eventService.getEvents(false, false, 2L));
        assertEquals(Collections.emptyList(), eventService.getEvents(false, true, 2L));
        assertEquals(List.of(event2), eventService.getEvents(true, false, 2L));
        assertEquals(List.of(event2), eventService.getEvents(true, true, 2L));

        assertEquals(Collections.emptyList(), eventService.getEvents(false, false, 3L));
        assertEquals(Collections.emptyList(), eventService.getEvents(false, true, 3L));
        assertEquals(Collections.emptyList(), eventService.getEvents(true, false, 3L));
        assertEquals(Collections.emptyList(), eventService.getEvents(true, true, 3L));
    }

    @Test
    void getEventByTalk() {
        Talk talk0 = new Talk();
        talk0.setId(0);

        eventService.getEventByTalk(talk0);
        Mockito.verify(eventDao, VerificationModeFactory.times(1)).getEventByTalk(talk0);
        Mockito.verifyNoMoreInteractions(eventDao);
    }
}
