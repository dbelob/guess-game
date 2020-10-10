package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.domain.Conference;
import guess.domain.source.EventType;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("StatisticsServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class StatisticsServiceImplTest {
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

    @BeforeEach
    void setUp() {
        EventType eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);

        EventType eventType1 = new EventType();
        eventType1.setId(1);

        Mockito.when(eventTypeDao.getEventTypes()).thenReturn(List.of(eventType0, eventType1));
    }

    @Test
    void getConferences() {
        List<EventType> eventTypes = statisticsService.getConferences();

        assertEquals(1, eventTypes.size());

        EventType eventType = eventTypes.get(0);

        assertEquals(0, eventType.getId());
        assertEquals(Conference.JPOINT, eventType.getConference());
    }
}
