package guess.dao;

import guess.domain.Conference;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("EventTypeDao interface tests")
class EventTypeDaoTest {
    private static Event event2;
    private static Event event3;

    private static EventTypeDao eventTypeDao;

    @BeforeAll
    static void init() {
        EventType eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);

        EventType eventType1 = new EventType();
        eventType1.setId(1);

        EventType eventType2 = new EventType();
        eventType2.setId(2);

        Event event0 = new Event();
        event0.setId(0);
        event0.setEventTypeId(eventType0.getId());
        event0.setEventType(eventType0);
        eventType0.setEvents(List.of(event0));

        Event event1 = new Event();
        event1.setId(1);
        event1.setEventTypeId(eventType1.getId());
        event1.setEventType(eventType1);
        eventType1.setEvents(List.of(event1));

        event2 = new Event();
        event2.setId(2);
        event2.setEventTypeId(eventType2.getId());
        event2.setEventType(eventType2);
        eventType2.setEvents(List.of(event2));

        event3 = new Event();
        event3.setId(3);

        eventTypeDao = Mockito.mock(EventTypeDao.class);
        Mockito.when(eventTypeDao.getEventTypeById(0)).thenReturn(eventType0);
        Mockito.when(eventTypeDao.getEventTypeById(1)).thenReturn(eventType1);
    }

    @Test
    void getItemsByEventTypeIds() {
        assertEquals(Collections.emptyList(), EventTypeDao.getItemsByEventTypeIds(
                Collections.emptyList(),
                value -> Collections.singletonList(event2),
                v -> Collections.singletonList(event3),
                eventTypeDao
        ));
        assertEquals(Collections.emptyList(), EventTypeDao.getItemsByEventTypeIds(
                Collections.singletonList(null),
                value -> Collections.singletonList(event2),
                v -> Collections.singletonList(event3),
                eventTypeDao
        ));
        assertEquals(Collections.singletonList(event2), EventTypeDao.getItemsByEventTypeIds(
                List.of(0L),
                value -> Collections.singletonList(event2),
                v -> Collections.singletonList(event3),
                eventTypeDao
        ));
        assertEquals(Collections.singletonList(event3), EventTypeDao.getItemsByEventTypeIds(
                List.of(1L),
                value -> Collections.singletonList(event2),
                v -> Collections.singletonList(event3),
                eventTypeDao
        ));
        assertEquals(Collections.singletonList(event3), EventTypeDao.getItemsByEventTypeIds(
                List.of(0L, 1L),
                value -> Collections.singletonList(event2),
                v -> Collections.singletonList(event3),
                eventTypeDao
        ));
    }
}
