package guess.service;

import guess.dao.EventTypeDao;
import guess.dao.OlapDao;
import guess.dao.OlapDaoImpl;
import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.statistics.olap.*;
import guess.domain.statistics.olap.dimension.City;
import guess.dto.statistics.olap.OlapCityParametersDto;
import guess.dto.statistics.olap.OlapEventTypeParametersDto;
import guess.dto.statistics.olap.OlapParametersDto;
import guess.dto.statistics.olap.OlapSpeakerParametersDto;
import org.junit.jupiter.api.*;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private static Speaker speaker2;
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
        Organizer organizer0 = new Organizer();
        organizer0.setId(0L);

        Organizer organizer1 = new Organizer();
        organizer1.setId(1L);

        eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);
        eventType0.setOrganizerId(0L);
        eventType0.setOrganizer(organizer0);

        eventType1 = new EventType();
        eventType1.setId(1);
        eventType1.setOrganizerId(0L);
        eventType1.setOrganizer(organizer0);

        eventType2 = new EventType();
        eventType2.setId(2);
        eventType2.setOrganizerId(1L);
        eventType2.setOrganizer(organizer1);

        Place place0 = new Place(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City0")), Collections.emptyList(), null);
        Place place1 = new Place(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City1")), Collections.emptyList(), null);
        Place place2 = new Place(2, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City2")), Collections.emptyList(), null);

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
        event2.setPlace(place2);
        event2.setStartDate(LocalDate.of(2021, 5, 1));
        event2.setEndDate(LocalDate.of(2021, 5, 2));

        Event event3 = new Event();
        event3.setId(3);
        event3.setPlace(place2);
        event3.setStartDate(LocalDate.of(2021, 6, 10));
        event3.setEndDate(LocalDate.of(2021, 6, 12));

        company0 = new Company();
        company0.setId(0);

        company1 = new Company();
        company1.setId(1);

        speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setCompanyIds(List.of(0L));
        speaker0.setCompanies(List.of(company0));
        speaker0.setJavaChampion(true);

        speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setCompanyIds(List.of(1L));
        speaker1.setCompanies(List.of(company1));
        speaker1.setMvp(true);

        speaker2 = new Speaker();
        speaker2.setId(2);
        speaker2.setCompanyIds(List.of(1L));
        speaker2.setCompanies(List.of(company1));

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

    @Test
    void getMeasureTypes() {
        assertEquals(List.of(MeasureType.EVENTS_QUANTITY, MeasureType.DURATION, MeasureType.TALKS_QUANTITY,
                        MeasureType.SPEAKERS_QUANTITY, MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY),
                olapService.getMeasureTypes(CubeType.EVENT_TYPES));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getOlapEventTypeStatistics method with parameters tests")
    class GetOlapEventTypeStatisticsTest {
        private Stream<Arguments> data() {
            List<Integer> dimensionValues0 = List.of(2020, 2021);

            List<OlapEntityMetrics<EventType>> metricsList0 = Collections.emptyList();
            OlapEntityMetrics<Void> totals0 = new OlapEntityMetrics<>(null, List.of(0L, 0L), 0L);

            List<OlapEntityMetrics<EventType>> metricsList1 = List.of(
                    new OlapEntityMetrics<>(eventType0, List.of(1L, 0L), 1L)
            );
            OlapEntityMetrics<Void> totals1 = new OlapEntityMetrics<>(null, List.of(1L, 0L), 1L);

            List<OlapEntityMetrics<EventType>> metricsList2 = List.of(
                    new OlapEntityMetrics<>(eventType2, List.of(0L, 1L), 1L)
            );
            OlapEntityMetrics<Void> totals2 = new OlapEntityMetrics<>(null, List.of(0L, 1L), 1L);

            List<OlapEntityMetrics<EventType>> metricsList3 = List.of(
                    new OlapEntityMetrics<>(eventType0, List.of(1L, 0L), 1L)
            );
            OlapEntityMetrics<Void> totals3 = new OlapEntityMetrics<>(null, List.of(1L, 0L), 1L);

            OlapEventTypeParametersDto op0 = new OlapEventTypeParametersDto();
            op0.setCubeType(CubeType.EVENT_TYPES);

            OlapEventTypeParametersDto op1 = new OlapEventTypeParametersDto();
            op1.setCubeType(CubeType.SPEAKERS);
            op1.setMeasureType(MeasureType.EVENTS_QUANTITY);

            OlapEventTypeParametersDto op2 = new OlapEventTypeParametersDto();
            op2.setCubeType(CubeType.SPEAKERS);
            op2.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op2.setConferences(true);

            OlapEventTypeParametersDto op3 = new OlapEventTypeParametersDto();
            op3.setCubeType(CubeType.SPEAKERS);
            op3.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op3.setConferences(true);
            op3.setSpeakerId(42L);

            OlapEventTypeParametersDto op4 = new OlapEventTypeParametersDto();
            op4.setCubeType(CubeType.SPEAKERS);
            op4.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op4.setConferences(true);
            op4.setSpeakerId(0L);

            OlapEventTypeParametersDto op5 = new OlapEventTypeParametersDto();
            op5.setCubeType(CubeType.SPEAKERS);
            op5.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op5.setMeetups(true);
            op5.setSpeakerId(42L);

            OlapEventTypeParametersDto op6 = new OlapEventTypeParametersDto();
            op6.setCubeType(CubeType.SPEAKERS);
            op6.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op6.setMeetups(true);
            op6.setSpeakerId(0L);

            OlapEventTypeParametersDto op7 = new OlapEventTypeParametersDto();
            op7.setCubeType(CubeType.SPEAKERS);
            op7.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op7.setConferences(true);
            op7.setMeetups(true);
            op7.setOrganizerId(42L);
            op7.setSpeakerId(0L);

            OlapEventTypeParametersDto op8 = new OlapEventTypeParametersDto();
            op8.setCubeType(CubeType.SPEAKERS);
            op8.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op8.setConferences(true);
            op8.setMeetups(true);
            op8.setOrganizerId(0L);
            op8.setSpeakerId(0L);

            OlapEventTypeParametersDto op9 = new OlapEventTypeParametersDto();
            op9.setCubeType(CubeType.SPEAKERS);
            op9.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op9.setConferences(true);
            op9.setMeetups(true);
            op9.setEventTypeIds(List.of(42L));
            op9.setSpeakerId(0L);

            OlapEventTypeParametersDto op10 = new OlapEventTypeParametersDto();
            op10.setCubeType(CubeType.SPEAKERS);
            op10.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op10.setConferences(true);
            op10.setMeetups(true);
            op10.setEventTypeIds(List.of(0L));
            op10.setSpeakerId(0L);

            OlapEventTypeParametersDto op11 = new OlapEventTypeParametersDto();
            op11.setCubeType(CubeType.COMPANIES);
            op11.setMeasureType(MeasureType.EVENTS_QUANTITY);

            OlapEventTypeParametersDto op12 = new OlapEventTypeParametersDto();
            op12.setCubeType(CubeType.COMPANIES);
            op12.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op12.setConferences(true);

            OlapEventTypeParametersDto op13 = new OlapEventTypeParametersDto();
            op13.setCubeType(CubeType.COMPANIES);
            op13.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op13.setConferences(true);
            op13.setCompanyId(42L);

            OlapEventTypeParametersDto op14 = new OlapEventTypeParametersDto();
            op14.setCubeType(CubeType.COMPANIES);
            op14.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op14.setConferences(true);
            op14.setCompanyId(0L);

            OlapEventTypeParametersDto op15 = new OlapEventTypeParametersDto();
            op15.setCubeType(CubeType.COMPANIES);
            op15.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op15.setMeetups(true);
            op15.setCompanyId(42L);

            OlapEventTypeParametersDto op16 = new OlapEventTypeParametersDto();
            op16.setCubeType(CubeType.COMPANIES);
            op16.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op16.setMeetups(true);
            op16.setCompanyId(0L);

            OlapEventTypeParametersDto op17 = new OlapEventTypeParametersDto();
            op17.setCubeType(CubeType.COMPANIES);
            op17.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op17.setConferences(true);
            op17.setMeetups(true);
            op17.setOrganizerId(42L);
            op17.setCompanyId(0L);

            OlapEventTypeParametersDto op18 = new OlapEventTypeParametersDto();
            op18.setCubeType(CubeType.COMPANIES);
            op18.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op18.setConferences(true);
            op18.setMeetups(true);
            op18.setOrganizerId(0L);
            op18.setCompanyId(0L);

            OlapEventTypeParametersDto op19 = new OlapEventTypeParametersDto();
            op19.setCubeType(CubeType.COMPANIES);
            op19.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op19.setConferences(true);
            op19.setMeetups(true);
            op19.setEventTypeIds(List.of(42L));
            op19.setCompanyId(0L);

            OlapEventTypeParametersDto op20 = new OlapEventTypeParametersDto();
            op20.setCubeType(CubeType.COMPANIES);
            op20.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op20.setConferences(true);
            op20.setMeetups(true);
            op20.setEventTypeIds(List.of(0L));
            op20.setCompanyId(0L);

            OlapEntityStatistics<Integer, EventType> expected0 = new OlapEntityStatistics<>(dimensionValues0, metricsList0, totals0);
            OlapEntityStatistics<Integer, EventType> expected1 = new OlapEntityStatistics<>(dimensionValues0, metricsList1, totals1);
            OlapEntityStatistics<Integer, EventType> expected2 = new OlapEntityStatistics<>(dimensionValues0, metricsList2, totals2);
            OlapEntityStatistics<Integer, EventType> expected3 = new OlapEntityStatistics<>(dimensionValues0, metricsList3, totals3);

            return Stream.of(
                    arguments(op0, IllegalArgumentException.class, null),

                    arguments(op1, null, expected0),
                    arguments(op2, null, expected0),
                    arguments(op3, null, expected0),
                    arguments(op4, null, expected1),
                    arguments(op5, null, expected0),
                    arguments(op6, null, expected2),
                    arguments(op7, null, expected0),
                    arguments(op8, null, expected3),
                    arguments(op9, null, expected0),
                    arguments(op10, null, expected3),

                    arguments(op11, null, expected0),
                    arguments(op12, null, expected0),
                    arguments(op13, null, expected0),
                    arguments(op14, null, expected1),
                    arguments(op15, null, expected0),
                    arguments(op16, null, expected2),
                    arguments(op17, null, expected0),
                    arguments(op18, null, expected3),
                    arguments(op19, null, expected0),
                    arguments(op20, null, expected3)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getOlapEventTypeStatistics(OlapEventTypeParametersDto op, Class<? extends Throwable> expectedException,
                                        OlapEntityStatistics<Integer, EventType> expectedValue) {
            if (expectedException == null) {
                assertEquals(expectedValue, olapService.getOlapEventTypeStatistics(op));
            } else {
                assertThrows(expectedException, () -> olapService.getOlapEventTypeStatistics(op));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getOlapSpeakerStatistics method with parameters tests")
    class GetOlapSpeakerStatisticsTest {
        private Stream<Arguments> data() {
            List<Integer> dimensionValues0 = List.of(2020, 2021);

            List<OlapEntityMetrics<Speaker>> metricsList0 = Collections.emptyList();
            OlapEntityMetrics<Void> totals0 = new OlapEntityMetrics<>(null, List.of(0L, 0L), 0L);

            List<OlapEntityMetrics<Speaker>> metricsList1 = List.of(
                    new OlapEntityMetrics<>(speaker0, List.of(1L, 0L), 1L)
            );
            OlapEntityMetrics<Void> totals1 = new OlapEntityMetrics<>(null, List.of(1L, 0L), 1L);

            List<OlapEntityMetrics<Speaker>> metricsList2 = List.of(
                    new OlapEntityMetrics<>(speaker1, List.of(0L, 1L), 1L)
            );
            OlapEntityMetrics<Void> totals2 = new OlapEntityMetrics<>(null, List.of(0L, 1L), 1L);

            OlapSpeakerParametersDto op0 = new OlapSpeakerParametersDto();
            op0.setCubeType(CubeType.SPEAKERS);
            op0.setMeasureType(MeasureType.EVENTS_QUANTITY);

            OlapSpeakerParametersDto op1 = new OlapSpeakerParametersDto();
            op1.setCubeType(CubeType.SPEAKERS);
            op1.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op1.setEventTypeId(42L);

            OlapSpeakerParametersDto op2 = new OlapSpeakerParametersDto();
            op2.setCubeType(CubeType.SPEAKERS);
            op2.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op2.setEventTypeId(0L);

            OlapSpeakerParametersDto op3 = new OlapSpeakerParametersDto();
            op3.setCubeType(CubeType.SPEAKERS);
            op3.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op3.setCompanyId(42L);

            OlapSpeakerParametersDto op4 = new OlapSpeakerParametersDto();
            op4.setCubeType(CubeType.SPEAKERS);
            op4.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op4.setCompanyId(0L);

            OlapSpeakerParametersDto op5 = new OlapSpeakerParametersDto();
            op5.setCubeType(CubeType.SPEAKERS);
            op5.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op5.setCompanyId(0L);
            op5.setEventTypeId(42L);

            OlapSpeakerParametersDto op6 = new OlapSpeakerParametersDto();
            op6.setCubeType(CubeType.SPEAKERS);
            op6.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op6.setCompanyId(42L);
            op6.setEventTypeId(0L);

            OlapSpeakerParametersDto op7 = new OlapSpeakerParametersDto();
            op7.setCubeType(CubeType.SPEAKERS);
            op7.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op7.setCompanyId(0L);
            op7.setEventTypeId(0L);

            OlapSpeakerParametersDto op8 = new OlapSpeakerParametersDto();
            op8.setCubeType(CubeType.SPEAKERS);
            op8.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op8.setCompanyId(1L);
            op8.setEventTypeId(1L);

            OlapEntityStatistics<Integer, Speaker> expected0 = new OlapEntityStatistics<>(dimensionValues0, metricsList0, totals0);
            OlapEntityStatistics<Integer, Speaker> expected1 = new OlapEntityStatistics<>(dimensionValues0, metricsList1, totals1);
            OlapEntityStatistics<Integer, Speaker> expected2 = new OlapEntityStatistics<>(dimensionValues0, metricsList2, totals2);

            return Stream.of(
                    arguments(op0, expected0),
                    arguments(op1, expected0),
                    arguments(op2, expected0),
                    arguments(op3, expected0),
                    arguments(op4, expected0),
                    arguments(op5, expected0),
                    arguments(op6, expected0),
                    arguments(op7, expected1),
                    arguments(op8, expected2)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getOlapSpeakerStatistics(OlapSpeakerParametersDto op, OlapEntityStatistics<Integer, Speaker> expected) {
            assertEquals(expected, olapService.getOlapSpeakerStatistics(op));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getOlapCityStatistics method with parameters tests")
    class GetOlapCityStatisticsTest {
        private Stream<Arguments> data() {
            List<Integer> dimensionValues0 = List.of(2020, 2021);

            City city0 = new City(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City0")));
            City city1 = new City(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City1")));

            List<OlapEntityMetrics<City>> metricsList0 = Collections.emptyList();
            OlapEntityMetrics<Void> totals0 = new OlapEntityMetrics<>(null, List.of(0L, 0L), 0L);

            List<OlapEntityMetrics<City>> metricsList1 = List.of(
                    new OlapEntityMetrics<>(city0, List.of(1L, 0L), 1L)
            );
            OlapEntityMetrics<Void> totals1 = new OlapEntityMetrics<>(null, List.of(1L, 0L), 1L);

            List<OlapEntityMetrics<City>> metricsList2 = List.of(
                    new OlapEntityMetrics<>(city1, List.of(0L, 1L), 1L)
            );
            OlapEntityMetrics<Void> totals2 = new OlapEntityMetrics<>(null, List.of(0L, 1L), 1L);

            OlapCityParametersDto op0 = new OlapCityParametersDto();
            op0.setCubeType(CubeType.EVENT_TYPES);
            op0.setMeasureType(MeasureType.EVENTS_QUANTITY);

            OlapCityParametersDto op1 = new OlapCityParametersDto();
            op1.setCubeType(CubeType.EVENT_TYPES);
            op1.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op1.setEventTypeId(42L);

            OlapCityParametersDto op2 = new OlapCityParametersDto();
            op2.setCubeType(CubeType.EVENT_TYPES);
            op2.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op2.setEventTypeId(0L);

            OlapCityParametersDto op3 = new OlapCityParametersDto();
            op3.setCubeType(CubeType.EVENT_TYPES);
            op3.setMeasureType(MeasureType.EVENTS_QUANTITY);
            op3.setEventTypeId(1L);

            OlapEntityStatistics<Integer, City> expected0 = new OlapEntityStatistics<>(dimensionValues0, metricsList0, totals0);
            OlapEntityStatistics<Integer, City> expected1 = new OlapEntityStatistics<>(dimensionValues0, metricsList1, totals1);
            OlapEntityStatistics<Integer, City> expected2 = new OlapEntityStatistics<>(dimensionValues0, metricsList2, totals2);

            return Stream.of(
                    arguments(op0, expected0),
                    arguments(op1, expected0),
                    arguments(op2, expected1),
                    arguments(op3, expected2)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getOlapCityStatistics(OlapCityParametersDto op, OlapEntityStatistics<Integer, City> expected) {
            assertEquals(expected, olapService.getOlapCityStatistics(op));
        }
    }

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
