package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.dao.TalkDao;
import guess.domain.Language;
import guess.domain.source.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("TalkServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class TalkServiceImplTest {
    private static EventType eventType0;
    private static EventType eventType1;
    private static EventType eventType2;
    private static Event event0;
    private static Event event1;
    private static Event event2;
    private static Talk talk0;
    private static Talk talk1;
    private static Talk talk2;

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        TalkDao talkDao() {
            TalkDao talkDao = Mockito.mock(TalkDao.class);

            Mockito.when(talkDao.getTalks()).thenReturn(List.of(talk0, talk1, talk2));

            return talkDao;
        }

        @Bean
        EventDao eventDao() {
            EventDao eventDao = Mockito.mock(EventDao.class);

            Mockito.when(eventDao.getEventById(0L)).thenReturn(event0);
            Mockito.when(eventDao.getEventById(1L)).thenReturn(event1);
            Mockito.when(eventDao.getEventById(2L)).thenReturn(event2);

            return eventDao;
        }

        @Bean
        EventTypeDao eventTypeDao() {
            EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);

            Mockito.when(eventTypeDao.getEventTypeById(0)).thenReturn(eventType0);
            Mockito.when(eventTypeDao.getEventTypeById(1)).thenReturn(eventType1);
            Mockito.when(eventTypeDao.getEventTypeById(2)).thenReturn(eventType2);

            return eventTypeDao;
        }

        @Bean
        TalkService talkService() {
            return new TalkServiceImpl(talkDao(), eventDao(), eventTypeDao());
        }
    }

    @Autowired
    private TalkDao talkDao;

    @Autowired
    private TalkService talkService;

    @BeforeAll
    static void init() {
        eventType0 = new EventType();
        eventType0.setId(0);

        eventType1 = new EventType();
        eventType1.setId(1);

        eventType2 = new EventType();
        eventType2.setId(2);

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Speaker0")));

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Speaker1")));

        talk0 = new Talk();
        talk0.setId(0);
        talk0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
        talk0.setSpeakers(List.of(speaker0));

        talk1 = new Talk();
        talk1.setId(1);
        talk1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));
        talk1.setSpeakers(List.of(speaker1));

        talk2 = new Talk();
        talk2.setId(2);
        talk2.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name2")));
        talk2.setSpeakers(List.of(speaker0, speaker1));

        event0 = new Event();
        event0.setId(0);
        event0.setEventType(eventType0);
        event0.setStartDate(LocalDate.of(2020, 10, 27));
        event0.setEndDate(LocalDate.of(2020, 10, 27));
        event0.setTalks(List.of(talk0));

        event1 = new Event();
        event1.setId(1);
        event1.setEventType(eventType1);
        event1.setStartDate(LocalDate.of(2020, 10, 28));
        event1.setEndDate(LocalDate.of(2020, 10, 28));
        event1.setTalks(List.of(talk1));

        event2 = new Event();
        event2.setId(2);
        event2.setEventType(eventType2);
        event2.setStartDate(LocalDate.of(2020, 10, 29));
        event2.setEndDate(LocalDate.of(2020, 10, 29));
        event2.setTalks(List.of(talk2));

        eventType0.setEvents(List.of(event0));
        eventType1.setEvents(List.of(event1));
        eventType2.setEvents(List.of(event2));
    }

    @Test
    void getTalkById() {
        talkService.getTalkById(0);
        Mockito.verify(talkDao, VerificationModeFactory.times(1)).getTalkById(0);
        Mockito.verifyNoMoreInteractions(talkDao);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getTalks method tests")
    class GetTalksTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, null, null, Collections.emptyList()),
                    arguments(0L, null, null, null, List.of(talk0)),
                    arguments(0L, 0L, null, null, List.of(talk0)),
                    arguments(null, null, "name", null, List.of(talk0, talk1, talk2)),
                    arguments(null, null, "0", null, List.of(talk0)),
                    arguments(null, null, "1", null, List.of(talk1)),
                    arguments(null, null, "2", null, List.of(talk2)),
                    arguments(null, null, "7", null, Collections.emptyList()),
                    arguments(null, null, null, "speaker", List.of(talk0, talk1, talk2)),
                    arguments(null, null, null, "0", List.of(talk0, talk2)),
                    arguments(null, null, null, "1", List.of(talk1, talk2)),
                    arguments(null, null, null, "7", Collections.emptyList()),
                    arguments(null, null, "2", "0", List.of(talk2)),
                    arguments(null, null, "0", "0", List.of(talk0)),
                    arguments(null, null, "1", "0", Collections.emptyList()),
                    arguments(null, 0L, null, null, List.of(talk0, talk1, talk2)),
                    arguments(null, 0L, null, "0", List.of(talk0, talk2)),
                    arguments(null, 0L, "0", null, List.of(talk0)),
                    arguments(null, 0L, "0", "0", List.of(talk0)),
                    arguments(0L, null, null, "0", List.of(talk0)),
                    arguments(0L, null, "0", null, List.of(talk0)),
                    arguments(0L, null, "0", "0", List.of(talk0)),
                    arguments(0L, 0L, null, null, List.of(talk0)),
                    arguments(0L, 0L, null, "0", List.of(talk0)),
                    arguments(0L, 0L, "0", null, List.of(talk0)),
                    arguments(0L, 0L, "0", "0", List.of(talk0))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getTalks(Long eventTypeId, Long eventId, String talkName, String speakerName, List<Talk> expected) {
            assertEquals(expected, talkService.getTalks(eventTypeId, eventId, talkName, speakerName));
        }
    }

    @Test
    void getTalksBySpeaker() {
        Speaker speaker = new Speaker();
        speaker.setId(0);

        talkService.getTalksBySpeaker(speaker);
        Mockito.verify(talkDao, VerificationModeFactory.times(1)).getTalksBySpeaker(speaker);
        Mockito.verifyNoMoreInteractions(talkDao);
    }
}
