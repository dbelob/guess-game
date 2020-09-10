package guess.service;

import guess.dao.EventTypeDao;
import guess.domain.Conference;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("EventTypeServiceImpl class tests")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EventTypeServiceImplTest.EventTypeServiceImplTestConfiguration.class})
class EventTypeServiceImplTest {
    @Configuration
    @Import(EventTypeServiceImpl.class)
    static class EventTypeServiceImplTestConfiguration {
    }

    @MockBean
    private EventTypeDao eventTypeDao;

    @Autowired
    private EventTypeService eventTypeService;

    @Test
    void getEventTypeById() {
        long id = 42;

        eventTypeService.getEventTypeById(id);
        Mockito.verify(eventTypeDao, VerificationModeFactory.times(1)).getEventTypeById(id);
        Mockito.verifyNoMoreInteractions(eventTypeDao);
        Mockito.reset(eventTypeDao);
    }

    @Test
    void getEventTypes() {
        eventTypeService.getEventTypes();
        Mockito.verify(eventTypeDao, VerificationModeFactory.times(1)).getEventTypes();
        Mockito.verifyNoMoreInteractions(eventTypeDao);
        Mockito.reset(eventTypeDao);
    }

    @Test
    void getEventTypesWithParameters() {
        EventType eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);

        EventType eventType1 = new EventType();
        eventType1.setId(1);

        Mockito.when(eventTypeDao.getEventTypes()).thenReturn(List.of(eventType0, eventType1));

        eventTypeService.getEventTypes(true, false);
        Mockito.verify(eventTypeDao, VerificationModeFactory.times(1)).getEventTypes();
        Mockito.verifyNoMoreInteractions(eventTypeDao);

        assertEquals(Collections.emptyList(), eventTypeService.getEventTypes(false, false));
        assertEquals(List.of(eventType0), eventTypeService.getEventTypes(true, false));
        assertEquals(List.of(eventType1), eventTypeService.getEventTypes(false, true));
        assertEquals(List.of(eventType0, eventType1), eventTypeService.getEventTypes(true, true));

        Mockito.reset(eventTypeDao);
    }

    @Test
    void getEventTypeByEvent() {
        EventType eventType = new EventType();
        eventType.setId(0);

        Event event = new Event();
        event.setEventType(eventType);
        event.setStartDate(LocalDate.of(2020, 9, 10));

        eventTypeService.getEventTypeByEvent(event);
        Mockito.verify(eventTypeDao, VerificationModeFactory.times(1)).getEventTypeByEvent(event);
        Mockito.verifyNoMoreInteractions(eventTypeDao);
        Mockito.reset(eventTypeDao);
    }
}
