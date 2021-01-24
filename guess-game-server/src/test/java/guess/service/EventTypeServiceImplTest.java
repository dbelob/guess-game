package guess.service;

import guess.dao.EventTypeDao;
import guess.domain.Conference;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Organizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("EventTypeServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class EventTypeServiceImplTest {
    @TestConfiguration
    static class EventTypeServiceImplTestConfiguration {
        @MockBean
        EventTypeDao eventTypeDao;

        @Bean
        EventTypeService eventTypeService() {
            return new EventTypeServiceImpl(eventTypeDao);
        }
    }

    @Autowired
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getEventTypes method with parameters tests")
    class GetEventTypesTest {
        private Stream<Arguments> data() {
            Organizer organizer0 = new Organizer();
            organizer0.setId(0);

            EventType eventType0 = new EventType();
            eventType0.setId(0);
            eventType0.setConference(Conference.JPOINT);
            eventType0.setOrganizer(organizer0);

            EventType eventType1 = new EventType();
            eventType1.setId(1);
            eventType1.setOrganizer(organizer0);

            List<EventType> eventTypes = List.of(eventType0, eventType1);

            return Stream.of(
                    arguments(false, false, null, eventTypes, Collections.emptyList()),
                    arguments(true, false, null, eventTypes, List.of(eventType0)),
                    arguments(false, true, null, eventTypes, List.of(eventType1)),
                    arguments(true, true, null, eventTypes, List.of(eventType0, eventType1)),
                    arguments(false, false, 0L, eventTypes, Collections.emptyList()),
                    arguments(true, false, 0L, eventTypes, List.of(eventType0)),
                    arguments(false, true, 0L, eventTypes, List.of(eventType1)),
                    arguments(true, true, 0L, eventTypes, List.of(eventType0, eventType1)),
                    arguments(false, false, 1L, eventTypes, Collections.emptyList()),
                    arguments(true, false, 1L, eventTypes, Collections.emptyList()),
                    arguments(false, true, 1L, eventTypes, Collections.emptyList()),
                    arguments(true, true, 1L, eventTypes, Collections.emptyList())
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getEventTypes(boolean isConferences, boolean isMeetups, Long organizerId, List<EventType> eventTypes,
                           List<EventType> expected) {
            Mockito.when(eventTypeDao.getEventTypes()).thenReturn(eventTypes);

            assertEquals(expected, eventTypeService.getEventTypes(isConferences, isMeetups, organizerId));
            Mockito.verify(eventTypeDao, VerificationModeFactory.times(1)).getEventTypes();
            Mockito.verifyNoMoreInteractions(eventTypeDao);

            Mockito.reset(eventTypeDao);
        }
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
