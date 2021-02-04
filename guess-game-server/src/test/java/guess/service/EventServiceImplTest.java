package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.domain.Conference;
import guess.domain.auxiliary.EventDateMinTrackTime;
import guess.domain.auxiliary.EventMinTrackTimeEndDayTime;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Talk;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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

    private static final LocalDate EVENT_START_DATE4;

    private static final LocalDate EVENT_END_DATE5;

    private static final LocalDate EVENT_START_DATE6;
    private static final LocalDate EVENT_END_DATE6;

    private static final LocalTime TALK_TRACK_TIME1;
    private static final LocalTime TALK_TRACK_TIME2;

    private static EventType eventType0;
    private static EventType eventType1;
    private static EventType eventType2;
    private static Event event0;
    private static Event event1;
    private static Event event2;
    private static Event event3;
    private static Event event4;
    private static Event event5;
    private static Event event6;

    static {
        NOW_DATE = LocalDate.now();

        EVENT_START_DATE0 = NOW_DATE.minus(3, ChronoUnit.DAYS);
        EVENT_END_DATE0 = EVENT_START_DATE0.plus(1, ChronoUnit.DAYS);

        EVENT_START_DATE1 = NOW_DATE.plus(3, ChronoUnit.DAYS);
        EVENT_END_DATE1 = EVENT_START_DATE1;

        EVENT_START_DATE2 = NOW_DATE.plus(7, ChronoUnit.DAYS);
        EVENT_END_DATE2 = EVENT_START_DATE2;

        EVENT_START_DATE4 = NOW_DATE.plus(8, ChronoUnit.DAYS);

        EVENT_END_DATE5 = EVENT_START_DATE4;

        EVENT_START_DATE6 = NOW_DATE.plus(10, ChronoUnit.DAYS);
        EVENT_END_DATE6 = EVENT_START_DATE6.minus(1, ChronoUnit.DAYS);

        TALK_TRACK_TIME1 = LocalTime.of(9, 0);
        TALK_TRACK_TIME2 = LocalTime.of(11, 30);
    }

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

        EventType eventType3 = new EventType();
        eventType3.setId(3);
        eventType3.setConference(Conference.HEISENBUG);

        EventType eventType4 = new EventType();
        eventType4.setId(4);
        eventType4.setConference(Conference.DOT_NEXT);

        EventType eventType5 = new EventType();
        eventType5.setId(2);
        eventType5.setConference(Conference.HOLY_JS);

        EventType eventType6 = new EventType();
        eventType6.setId(6);
        eventType6.setConference(Conference.CPP_RUSSIA);

        Talk talk0 = new Talk();
        talk0.setId(0);

        Talk talk1 = new Talk();
        talk1.setId(1);
        talk1.setTalkDay(1L);
        talk1.setTrackTime(TALK_TRACK_TIME1);

        Talk talk2 = new Talk();
        talk2.setId(2);
        talk2.setTalkDay(1L);
        talk2.setTrackTime(TALK_TRACK_TIME2);

        event0 = new Event();
        event0.setId(0);
        event0.setEventType(eventType0);
        event0.setStartDate(EVENT_START_DATE0);
        event0.setEndDate(EVENT_END_DATE0);
        event0.setTalks(List.of(talk0));

        event1 = new Event();
        event1.setId(1);
        event1.setEventType(eventType1);
        event1.setStartDate(EVENT_START_DATE1);
        event1.setEndDate(EVENT_END_DATE1);
        event1.setTalks(List.of(talk1));

        event2 = new Event();
        event2.setId(2);
        event2.setEventType(eventType2);
        event2.setStartDate(EVENT_START_DATE2);
        event2.setEndDate(EVENT_END_DATE2);
        event2.setTalks(List.of(talk2));

        event3 = new Event();
        event3.setId(3);
        event3.setEventType(eventType3);
        event3.setTalks(List.of(talk2));

        event4 = new Event();
        event4.setId(4);
        event4.setEventType(eventType4);
        event4.setStartDate(EVENT_START_DATE4);
        event4.setTalks(List.of(talk2));

        event5 = new Event();
        event5.setId(5);
        event5.setEventType(eventType5);
        event5.setEndDate(EVENT_END_DATE5);
        event5.setTalks(List.of(talk2));

        event6 = new Event();
        event6.setId(6);
        event6.setEventType(eventType6);
        event6.setStartDate(EVENT_START_DATE6);
        event6.setEndDate(EVENT_END_DATE6);
        event6.setTalks(List.of(talk2));

        eventType0.setEvents(List.of(event0));
        eventType1.setEvents(List.of(event1));
        eventType2.setEvents(List.of(event2));
    }

    @Test
    void getEventById() {
        EventDao eventDao = Mockito.mock(EventDao.class);
        EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
        EventService eventService = new EventServiceImpl(eventDao, eventTypeDao);

        eventService.getEventById(0);
        Mockito.verify(eventDao, VerificationModeFactory.times(1)).getEventById(0);
        Mockito.verifyNoMoreInteractions(eventDao);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getEvents method tests")
    class GetEventsTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(false, false, null, Collections.emptyList()),
                    arguments(false, true, null, List.of(event1)),
                    arguments(true, false, null, List.of(event0, event2)),
                    arguments(true, true, null, List.of(event0, event1, event2)),

                    arguments(false, false, 0L, Collections.emptyList()),
                    arguments(false, true, 0L, Collections.emptyList()),
                    arguments(true, false, 0L, List.of(event0)),
                    arguments(true, true, 0L, List.of(event0)),

                    arguments(false, false, 1L, Collections.emptyList()),
                    arguments(false, true, 1L, List.of(event1)),
                    arguments(true, false, 1L, Collections.emptyList()),
                    arguments(true, true, 1L, List.of(event1)),

                    arguments(false, false, 2L, Collections.emptyList()),
                    arguments(false, true, 2L, Collections.emptyList()),
                    arguments(true, false, 2L, List.of(event2)),
                    arguments(true, true, 2L, List.of(event2)),

                    arguments(false, false, 3L, Collections.emptyList()),
                    arguments(false, true, 3L, Collections.emptyList()),
                    arguments(true, false, 3L, Collections.emptyList()),
                    arguments(true, true, 3L, Collections.emptyList())
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getEvents(boolean isConferences, boolean isMeetups, Long eventTypeId, List<Event> expected) {
            EventDao eventDao = Mockito.mock(EventDao.class);
            EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
            EventService eventService = new EventServiceImpl(eventDao, eventTypeDao);

            Mockito.when(eventTypeDao.getEventTypes()).thenReturn(List.of(eventType0, eventType1, eventType2));

            assertEquals(expected, eventService.getEvents(isConferences, isMeetups, eventTypeId));
        }
    }

    @Test
    void getDefaultEvent() {
        EventServiceImpl eventService = Mockito.mock(EventServiceImpl.class);
        final boolean IS_CONFERENCES = Boolean.TRUE;
        final boolean IS_MEETUPS = Boolean.FALSE;

        Mockito.doCallRealMethod().when(eventService).getDefaultEvent(IS_CONFERENCES, IS_MEETUPS);

        eventService.getDefaultEvent(IS_CONFERENCES, IS_MEETUPS);
        Mockito.verify(eventService, VerificationModeFactory.times(1)).getDefaultEvent(IS_CONFERENCES, IS_MEETUPS);
        Mockito.verify(eventService, VerificationModeFactory.times(1)).getDefaultEvent(Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any(LocalDateTime.class));
        Mockito.verifyNoMoreInteractions(eventService);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getDefaultEvent method tests")
    class GetDefaultEventTest {
        private Stream<Arguments> data() {
            LocalDateTime dateTime = LocalDateTime.of(2020, 10, 23, 9, 0);
            LocalDate date = dateTime.toLocalDate();

            EventDateMinTrackTime eventDateMinTrackTime0 = new EventDateMinTrackTime(
                    event0,
                    date.minus(1, ChronoUnit.DAYS),
                    LocalTime.of(9, 0)
            );

            EventDateMinTrackTime eventDateMinTrackTime1 = new EventDateMinTrackTime(
                    event0,
                    date.plus(1, ChronoUnit.DAYS),
                    LocalTime.of(9, 0)
            );

            EventDateMinTrackTime eventDateMinTrackTime2 = new EventDateMinTrackTime(
                    event2,
                    date,
                    LocalTime.of(9, 0)
            );

            EventDateMinTrackTime eventDateMinTrackTime3 = new EventDateMinTrackTime(
                    event0,
                    date,
                    LocalTime.of(10, 0)
            );

            return Stream.of(
                    arguments(true, false, dateTime, Collections.emptyList(), null, null),
                    arguments(true, false, dateTime, List.of(event1), null, null),
                    arguments(true, false, dateTime, List.of(event0), Collections.emptyList(), null),
                    arguments(true, false, dateTime, List.of(event0), List.of(eventDateMinTrackTime0), null),
                    arguments(true, false, dateTime, List.of(event0), List.of(eventDateMinTrackTime0, eventDateMinTrackTime1), event0),
                    arguments(true, false, dateTime, List.of(event0), List.of(eventDateMinTrackTime1, eventDateMinTrackTime2, eventDateMinTrackTime3), event2),
                    arguments(true, false, dateTime, List.of(event0), List.of(eventDateMinTrackTime1, eventDateMinTrackTime3), event0),

                    arguments(false, true, dateTime, Collections.emptyList(), null, null),
                    arguments(false, true, dateTime, List.of(event0), null, null),
                    arguments(false, true, dateTime, List.of(event1), Collections.emptyList(), null),
                    arguments(false, true, dateTime, List.of(event1), List.of(eventDateMinTrackTime0), null),
                    arguments(false, true, dateTime, List.of(event1), List.of(eventDateMinTrackTime0, eventDateMinTrackTime1), event0),
                    arguments(false, true, dateTime, List.of(event1), List.of(eventDateMinTrackTime1, eventDateMinTrackTime2, eventDateMinTrackTime3), event2),
                    arguments(false, true, dateTime, List.of(event1), List.of(eventDateMinTrackTime1, eventDateMinTrackTime3), event0),

                    arguments(false, false, dateTime, Collections.emptyList(), null, null),

                    arguments(false, true, dateTime, Collections.emptyList(), null, null),
                    arguments(false, true, dateTime, List.of(event0, event1), Collections.emptyList(), null),
                    arguments(false, true, dateTime, List.of(event0, event1), List.of(eventDateMinTrackTime0), null),
                    arguments(false, true, dateTime, List.of(event0, event1), List.of(eventDateMinTrackTime0, eventDateMinTrackTime1), event0),
                    arguments(false, true, dateTime, List.of(event0, event1), List.of(eventDateMinTrackTime1, eventDateMinTrackTime2, eventDateMinTrackTime3), event2),
                    arguments(false, true, dateTime, List.of(event0, event1), List.of(eventDateMinTrackTime1, eventDateMinTrackTime3), event0)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getDefaultEvent(boolean isConferences, boolean isMeetups, LocalDateTime dateTime, List<Event> events,
                             List<EventDateMinTrackTime> eventDateMinTrackTimeList, Event expected) {
            EventDao eventDao = Mockito.mock(EventDao.class);
            EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
            EventServiceImpl eventService = Mockito.mock(EventServiceImpl.class, Mockito.withSettings().useConstructor(eventDao, eventTypeDao));

            Mockito.when(eventDao.getEventsFromDate(Mockito.any(LocalDate.class))).thenReturn(events);

            Mockito.doCallRealMethod().when(eventService).getDefaultEvent(Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any());
            Mockito.when(eventService.getEventDateMinTrackTimeList(Mockito.any())).thenReturn(eventDateMinTrackTimeList);

            assertEquals(expected, eventService.getDefaultEvent(isConferences, isMeetups, dateTime));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getEventDateMinTrackTimeList method tests")
    class GetEventDateMinTrackTimeListTest {
        private Stream<Arguments> data() {
            EventDateMinTrackTime eventDateMinTrackTime0 = new EventDateMinTrackTime(
                    event0,
                    EVENT_START_DATE0,
                    LocalTime.of(0, 0)
            );

            EventDateMinTrackTime eventDateMinTrackTime1 = new EventDateMinTrackTime(
                    event0,
                    EVENT_START_DATE0.plus(1, ChronoUnit.DAYS),
                    LocalTime.of(0, 0)
            );

            EventDateMinTrackTime eventDateMinTrackTime2 = new EventDateMinTrackTime(
                    event1,
                    EVENT_START_DATE1,
                    TALK_TRACK_TIME1
            );

            EventDateMinTrackTime eventDateMinTrackTime3 = new EventDateMinTrackTime(
                    event2,
                    EVENT_START_DATE2,
                    TALK_TRACK_TIME2
            );

            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyList()),
                    arguments(List.of(event0), List.of(eventDateMinTrackTime0, eventDateMinTrackTime1)),
                    arguments(List.of(event0, event1), List.of(eventDateMinTrackTime0, eventDateMinTrackTime1, eventDateMinTrackTime2)),
                    arguments(List.of(event0, event1, event2), List.of(eventDateMinTrackTime0, eventDateMinTrackTime1, eventDateMinTrackTime2, eventDateMinTrackTime3)),
                    arguments(List.of(event3, event4, event5, event6), Collections.emptyList()),
                    arguments(List.of(event0, event1, event2, event3, event4, event5, event6), List.of(eventDateMinTrackTime0, eventDateMinTrackTime1, eventDateMinTrackTime2, eventDateMinTrackTime3))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getConferenceDateMinTrackTimeList(List<Event> events, List<EventDateMinTrackTime> expected) {
            EventDao eventDao = Mockito.mock(EventDao.class);
            EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
            EventServiceImpl eventService = new EventServiceImpl(eventDao, eventTypeDao);

            assertEquals(expected, eventService.getEventDateMinTrackTimeList(events));
        }
    }

    @Test
    void getEventMinTrackTimeEndDayTimeList() {
        Event event0 = new Event();
        event0.setId(0);
        event0.setTimeZoneId(ZoneId.of("Europe/Moscow"));

        Event event1 = new Event();
        event1.setId(1);
        event1.setTimeZoneId(ZoneId.of("Asia/Novosibirsk"));

        EventDateMinTrackTime eventDateMinTrackTime0 = new EventDateMinTrackTime(
                event0,
                LocalDate.of(2021, 2, 4),
                LocalTime.of(15, 0)
        );

        EventDateMinTrackTime eventDateMinTrackTime1 = new EventDateMinTrackTime(
                event1,
                LocalDate.of(2021, 2, 4),
                LocalTime.of(15, 0)
        );

        EventDateMinTrackTime eventDateMinTrackTime2 = new EventDateMinTrackTime(
                event0,
                LocalDate.of(2021, 2, 4),
                LocalTime.of(0, 0)
        );

        EventMinTrackTimeEndDayTime eventMinTrackTimeEndDayTime0 = new EventMinTrackTimeEndDayTime(
                event0,
                LocalDateTime.of(2021, 2, 4, 12, 0),
                LocalDateTime.of(2021, 2, 4, 21, 0)
        );

        EventMinTrackTimeEndDayTime eventMinTrackTimeEndDayTime1 = new EventMinTrackTimeEndDayTime(
                event1,
                LocalDateTime.of(2021, 2, 4, 8, 0),
                LocalDateTime.of(2021, 2, 4, 17, 0)
        );

        EventMinTrackTimeEndDayTime eventMinTrackTimeEndDayTime2 = new EventMinTrackTimeEndDayTime(
                event0,
                LocalDateTime.of(2021, 2, 3, 21, 0),
                LocalDateTime.of(2021, 2, 4, 21, 0)
        );

        EventDao eventDao = Mockito.mock(EventDao.class);
        EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
        EventServiceImpl eventService = new EventServiceImpl(eventDao, eventTypeDao);

        assertEquals(
                List.of(eventMinTrackTimeEndDayTime0, eventMinTrackTimeEndDayTime1, eventMinTrackTimeEndDayTime2),
                eventService.getEventMinTrackTimeEndDayTimeList(List.of(eventDateMinTrackTime0, eventDateMinTrackTime1, eventDateMinTrackTime2)));
    }

    @Test
    void getEventByTalk() {
        Talk talk0 = new Talk();
        talk0.setId(0);

        EventDao eventDao = Mockito.mock(EventDao.class);
        EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
        EventService eventService = new EventServiceImpl(eventDao, eventTypeDao);

        eventService.getEventByTalk(talk0);
        Mockito.verify(eventDao, VerificationModeFactory.times(1)).getEventByTalk(talk0);
        Mockito.verifyNoMoreInteractions(eventDao);
    }
}
