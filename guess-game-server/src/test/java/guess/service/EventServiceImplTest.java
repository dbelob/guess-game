package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
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

@DisplayName("EventServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class EventServiceImplTest {
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
        //TODO: implement
    }

    @BeforeEach
    void setUp() {
//        Mockito.when(eventTypeDao.getEventTypes()).thenReturn(List.of(eventType0, eventType1, eventType2));
    }

    @Test
    void getEventById() {
        eventService.getEventById(0);
        Mockito.verify(eventDao, VerificationModeFactory.times(1)).getEventById(0);
        Mockito.verifyNoMoreInteractions(eventDao);
    }

    @Test
    void getEvents() {
        eventService.getEvents(false, false, null);
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
