package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.dao.QuestionDao;
import guess.domain.Conference;
import guess.domain.GuessMode;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Place;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("QuestionServiceImpl class tests")
class QuestionServiceImplTest {
    private static Event event0;
    private static Event event1;
    private static Event event2;
    private static Event event3;

    private static QuestionService questionService;

    @BeforeAll
    static void init() {
        event0 = new Event();
        event0.setId(0);

        event1 = new Event();
        event1.setId(1);

        event2 = new Event();
        event2.setId(2);

        event3 = new Event();
        event3.setId(3);

        EventType eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);
        eventType0.setEvents(List.of(event0, event3));
        event0.setEventTypeId(0);
        event0.setEventType(eventType0);
        event3.setEventTypeId(0);
        event3.setEventType(eventType0);

        EventType eventType1 = new EventType();
        eventType1.setId(1);
        eventType1.setEvents(List.of(event1));
        event1.setEventTypeId(1);
        event1.setEventType(eventType1);

        EventType eventType2 = new EventType();
        eventType2.setId(2);
        eventType2.setEvents(List.of(event2));
        event2.setEventTypeId(2);
        event2.setEventType(eventType2);

        QuestionDao questionDao = Mockito.mock(QuestionDao.class);
        Mockito.when(questionDao.getQuestionByIds(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Collections.emptyList());

        EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
        Mockito.when(eventTypeDao.getEventTypeById(0)).thenReturn(eventType0);
        Mockito.when(eventTypeDao.getEventTypeById(1)).thenReturn(eventType1);
        Mockito.when(eventTypeDao.getEventTypeById(2)).thenReturn(eventType2);

        EventDao eventDao = Mockito.mock(EventDao.class);
        Mockito.when(eventDao.getEventsByEventTypeId(0)).thenReturn(List.of(event0, event3));
        Mockito.when(eventDao.getEventsByEventTypeId(1)).thenReturn(List.of(event1));
        Mockito.when(eventDao.getEventsByEventTypeId(2)).thenReturn(List.of(event2));

        questionService = new QuestionServiceImpl(questionDao, eventTypeDao, eventDao);
    }

    @Test
    void getEvents() {
        final List<Event> ALL_EVENTS_OPTION_EVENTS = Collections.singletonList(
                new Event(
                        -1L,
                        null,
                        Collections.emptyList(),
                        new Event.EventDates(
                                null,
                                null
                        ),
                        new Event.EventLinks(
                                null,
                                null
                        ),
                        new Place(
                                -1L,
                                null,
                                null,
                                null
                        ),
                        null,
                        Collections.emptyList()
                )
        );

        assertEquals(
                Collections.emptyList(),
                questionService.getEvents(Collections.emptyList())
        );
        assertEquals(
                Collections.emptyList(),
                questionService.getEvents(Collections.singletonList(null))
        );
        assertEquals(
                List.of(event0, event3),
                questionService.getEvents(List.of(0L))
        );
        assertEquals(
                ALL_EVENTS_OPTION_EVENTS,
                questionService.getEvents(List.of(1L))
        );
        assertEquals(
                ALL_EVENTS_OPTION_EVENTS,
                questionService.getEvents(List.of(2L))
        );
        assertEquals(
                ALL_EVENTS_OPTION_EVENTS,
                questionService.getEvents(List.of(0L, 1L))
        );
        assertEquals(
                ALL_EVENTS_OPTION_EVENTS,
                questionService.getEvents(List.of(0L, 1L, 2L))
        );
    }

    @Test
    void getQuantities() {
        assertDoesNotThrow(() -> questionService.getQuantities(
                Collections.emptyList(),
                Collections.emptyList(),
                GuessMode.GUESS_PHOTO_BY_NAME_MODE));
    }
}
