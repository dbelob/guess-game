package guess.dao;

import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Place;
import guess.domain.source.SourceInformation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SourceDaoImplTest {
    private static Place place0;
    private static Place place1;
    private static Place place2;

    private static EventType eventType0;
    private static EventType eventType1;
    private static EventType eventType2;

    private static Event event0;
    private static Event event1;
    private static Event event2;
    private static Event event3;

    private static SourceDao sourceDao;

    @BeforeAll
    static void init() {
        place0 = new Place();
        place0.setId(0);

        place1 = new Place();
        place1.setId(1);

        place2 = new Place();
        place2.setId(2);

        eventType0 = new EventType();
        eventType0.setId(0);

        eventType1 = new EventType();
        eventType1.setId(1);

        eventType2 = new EventType();
        eventType2.setId(2);

        event0 = new Event();
        event0.setId(0);
        event0.setEventTypeId(eventType0.getId());
        event0.setEventType(eventType0);
        eventType0.setEvents(List.of(event0));

        event1 = new Event();
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

        SourceInformation sourceInformation = new SourceInformation(
                List.of(place0, place1, place2),
                List.of(eventType0, eventType1, eventType2),
                List.of(event0, event1, event2),
                Collections.emptyList(),
                Collections.emptyList());
        sourceDao = new SourceDaoImpl(sourceInformation);
    }

    @Test
    void getPlaces() {
        assertEquals(List.of(place0, place1, place2), sourceDao.getPlaces());
    }

    @Test
    void getEventTypes() {
        assertEquals(List.of(eventType0, eventType1, eventType2), sourceDao.getEventTypes());
    }

    @Test
    void getEventTypeById() {
        assertEquals(eventType0, sourceDao.getEventTypeById(0));
        assertEquals(eventType1, sourceDao.getEventTypeById(1));
        assertEquals(eventType2, sourceDao.getEventTypeById(2));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getEventTypeById(4));
    }

    @Test
    void getEventTypeByEvent() {
        assertEquals(eventType0, sourceDao.getEventTypeByEvent(event0));
        assertEquals(eventType1, sourceDao.getEventTypeByEvent(event1));
        assertEquals(eventType2, sourceDao.getEventTypeByEvent(event2));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getEventTypeByEvent(event3));
    }

//    @Test
//    void getItemsByEventTypeIds() {
//    }

    @Test
    void getEvents() {
        assertEquals(List.of(event0, event1, event2), sourceDao.getEvents());
    }

    @Test
    void getEventById() {
        assertEquals(event0, sourceDao.getEventById(0));
        assertEquals(event1, sourceDao.getEventById(1));
        assertEquals(event2, sourceDao.getEventById(2));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getEventById(4));
    }

    @Test
    void getEventsByEventTypeId() {
        assertEquals(List.of(event0), sourceDao.getEventsByEventTypeId(0));
        assertEquals(List.of(event1), sourceDao.getEventsByEventTypeId(1));
        assertEquals(List.of(event2), sourceDao.getEventsByEventTypeId(2));
        assertEquals(Collections.emptyList(), sourceDao.getEventsByEventTypeId(3));
    }

//    @Test
//    void getEventsFromDate() {
//    }

//    @Test
//    void getEventByTalk() {
//    }

//    @Test
//    void getSpeakers() {
//    }

//    @Test
//    void getSpeakerById() {
//    }

//    @Test
//    void getTalks() {
//    }

//    @Test
//    void getTalkById() {
//    }

//    @Test
//    void getTalksBySpeaker() {
//    }
}
