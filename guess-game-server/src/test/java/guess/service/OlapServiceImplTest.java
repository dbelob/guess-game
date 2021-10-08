package guess.service;

import guess.dao.EventTypeDao;
import guess.dao.OlapDao;
import guess.dao.OlapDaoImpl;
import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.statistics.olap.*;
import guess.dto.statistics.olap.OlapParametersDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("OlapServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class OlapServiceImplTest {
    private static EventType eventType0;
    private static EventType eventType1;
    private static EventType eventType2;
    private static Event event0;
    private static Event event1;
    private static Talk talk0;
    private static Talk talk1;
    private static Speaker speaker0;
    private static Speaker speaker1;
    private static Company company0;
    private static Company company1;

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        EventTypeDao eventTypeDao() {
            EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
            Mockito.when(eventTypeDao.getEventTypes()).thenReturn(List.of(eventType0, eventType1, eventType2));

            return eventTypeDao;
        }

        @Bean
        OlapDao olapDao() {
            return new OlapDaoImpl(eventTypeDao());
        }

        @Bean
        OlapService olapService() {
            return new OlapServiceImpl(olapDao());
        }
    }

    @Autowired
    private EventTypeDao eventTypeDao;

    @Autowired
    private OlapDao olapDao;

    @Autowired
    private OlapService olapService;

    @BeforeAll
    static void init() {
        eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);

        eventType1 = new EventType();
        eventType1.setId(1);

        eventType2 = new EventType();
        eventType2.setId(2);

        Place place0 = new Place(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City0")), Collections.emptyList(), null);
        Place place1 = new Place(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City1")), Collections.emptyList(), null);

        event0 = new Event();
        event0.setId(0);
        event0.setEventTypeId(eventType0.getId());
        event0.setEventType(eventType0);
        eventType0.setEvents(List.of(event0));
        event0.setPlace(place0);
        event0.setStartDate(LocalDate.of(2020, 9, 5));
        event0.setEndDate(LocalDate.of(2020, 9, 6));

        event1 = new Event();
        event1.setId(1);
        event1.setEventTypeId(eventType1.getId());
        event1.setEventType(eventType1);
        eventType1.setEvents(List.of(event1));
        event1.setPlace(place1);
        event1.setStartDate(LocalDate.of(2021, 10, 7));
        event1.setEndDate(LocalDate.of(2021, 10, 9));

        Event event2 = new Event();
        event2.setId(2);
        event2.setEventTypeId(eventType2.getId());
        event2.setEventType(eventType2);
        eventType2.setEvents(List.of(event2));
        event2.setPlace(place1);
        event2.setStartDate(LocalDate.of(2021, 5, 1));
        event2.setEndDate(LocalDate.of(2021, 5, 2));

        Event event3 = new Event();
        event3.setId(3);
        event3.setPlace(place1);
        event3.setStartDate(LocalDate.of(2021, 6, 10));
        event3.setEndDate(LocalDate.of(2021, 6, 12));

        company0 = new Company();
        company0.setId(0);

        company1 = new Company();
        company1.setId(1);

        speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setCompanies(List.of(company0));
        speaker0.setJavaChampion(true);

        speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setCompanies(List.of(company1));
        speaker1.setMvp(true);

        talk0 = new Talk();
        talk0.setId(0);
        talk0.setSpeakerIds(List.of(0L));
        talk0.setSpeakers(List.of(speaker0));
        event0.setTalkIds(List.of(0L));
        event0.setTalks(List.of(talk0));

        talk1 = new Talk();
        talk1.setId(1);
        talk1.setSpeakerIds(List.of(1L));
        talk1.setSpeakers(List.of(speaker1));
        event1.setTalkIds(List.of(1L));
        event1.setTalks(List.of(talk1));

        Talk talk2 = new Talk();
        talk2.setId(2);
        talk2.setSpeakerIds(List.of(0L, 1L));
        talk2.setSpeakers(List.of(speaker0, speaker1));
        event2.setTalkIds(List.of(2L));
        event2.setTalks(List.of(talk2));
    }

//    @BeforeEach
//    void setUp() {
//        EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
//        Mockito.when(eventTypeDao.getEventTypes()).thenReturn(List.of(eventType0, eventType1, eventType2));
//
//        OlapDao olapDao = new OlapDaoImpl(eventTypeDao);
//
//        Mockito.when(olapDao.getCube(CubeType.EVENT_TYPES)).thenReturn(olapDao.getCube(CubeType.EVENT_TYPES));
//    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getOlapEntityStatistics method with parameters tests")
    class GetOlapEntityStatisticsTest {
        private Stream<Arguments> data() {
            OlapParametersDto op0 = new OlapParametersDto();
            op0.setCubeType(CubeType.EVENT_TYPES);
            op0.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op0.setConferences(true);
            op0.setMeetups(true);

            OlapParametersDto op1 = new OlapParametersDto();
            op1.setCubeType(CubeType.EVENT_TYPES);
            op1.setMeasureType(MeasureType.DURATION);
            op1.setConferences(true);
            op1.setMeetups(false);

            List<Integer> dimensionValues0 = List.of(2020, 2021);

            List<OlapEntityMetrics<EventType>> metricsList0 = List.of(
                    new OlapEntityMetrics<>(eventType0, List.of(1L, 0L), 1L),
                    new OlapEntityMetrics<>(eventType1, List.of(0L, 1L), 1L),
                    new OlapEntityMetrics<>(eventType2, List.of(0L, 1L), 1L)
            );
            OlapEntityMetrics<Void> totals0 = new OlapEntityMetrics<>(null, List.of(1L, 2L), 3L);

            List<OlapEntityMetrics<EventType>> metricsList1 = List.of(
                    new OlapEntityMetrics<>(eventType0, List.of(2L, 0L), 2L)
            );
            OlapEntityMetrics<Void> totals1 = new OlapEntityMetrics<>(null, List.of(2L, 0L), 2L);

            OlapEntityStatistics<Integer, EventType> expected0 = new OlapEntityStatistics<>(dimensionValues0, metricsList0, totals0);
            OlapEntityStatistics<Integer, EventType> expected1 = new OlapEntityStatistics<>(dimensionValues0, metricsList1, totals1);

            return Stream.of(
                    arguments(op0, expected0),
                    arguments(op1, expected1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getOlapEntityStatistics(OlapParametersDto op, OlapEntityStatistics<Integer, EventType> expected) {
            OlapServiceImpl olapServiceImpl = new OlapServiceImpl(olapDao);
            Predicate<EventType> eventTypePredicate = et ->
                    ((op.isConferences() && et.isEventTypeConference()) || (op.isMeetups() && !et.isEventTypeConference())) &&
                            ((op.getOrganizerId() == null) || (et.getOrganizer().getId() == op.getOrganizerId())) &&
                            ((op.getEventTypeIds() == null) || op.getEventTypeIds().isEmpty() || op.getEventTypeIds().contains(et.getId()));
            OlapEntityStatistics<Integer, EventType> actual = olapServiceImpl.getOlapEntityStatistics(
                    op.getCubeType(), op.getMeasureType(), DimensionType.EVENT_TYPE, eventTypePredicate,
                    DimensionType.EVENT_TYPE, eventTypePredicate);

            actual.getMetricsList().sort(Comparator.comparing(m -> m.getEntity().getId()));

            assertEquals(expected, actual);
        }
    }
}
