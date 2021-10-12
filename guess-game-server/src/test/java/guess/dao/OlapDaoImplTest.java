package guess.dao;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.statistics.olap.Cube;
import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.DimensionType;
import guess.domain.statistics.olap.MeasureType;
import guess.domain.statistics.olap.dimension.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class OlapDaoImplTest {
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

    private static EventTypeDao eventTypeDao;

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

        eventTypeDao = Mockito.mock(EventTypeDao.class);
        Mockito.when(eventTypeDao.getEventTypes()).thenReturn(List.of(eventType0, eventType1, eventType2));
    }

    @Test
    void getCube() {
        OlapDao olapDao = new OlapDaoImpl(eventTypeDao);

        assertNotNull(olapDao.getCube(CubeType.EVENT_TYPES));
        assertNotNull(olapDao.getCube(CubeType.SPEAKERS));
        assertNotNull(olapDao.getCube(CubeType.COMPANIES));
    }

    @Test
    void getMeasureTypes() {
        OlapDao olapDao = new OlapDaoImpl(eventTypeDao);

        assertEquals(
                List.of(MeasureType.EVENTS_QUANTITY, MeasureType.DURATION, MeasureType.TALKS_QUANTITY,
                        MeasureType.SPEAKERS_QUANTITY, MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY),
                olapDao.getMeasureTypes(CubeType.EVENT_TYPES));
        assertEquals(
                List.of(MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY, MeasureType.EVENT_TYPES_QUANTITY),
                olapDao.getMeasureTypes(CubeType.SPEAKERS));
        assertEquals(
                List.of(MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY, MeasureType.EVENT_TYPES_QUANTITY,
                        MeasureType.SPEAKERS_QUANTITY, MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY),
                olapDao.getMeasureTypes(CubeType.COMPANIES));
    }

    private static Cube createEventTypesCube() {
        return new Cube(
                new LinkedHashSet<>(Arrays.asList(DimensionType.EVENT_TYPE, DimensionType.CITY, DimensionType.YEAR)),
                new LinkedHashSet<>(Arrays.asList(MeasureType.EVENTS_QUANTITY, MeasureType.DURATION, MeasureType.TALKS_QUANTITY,
                        MeasureType.SPEAKERS_QUANTITY, MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY)));
    }

    private static Cube createSpeakersCube() {
        return new Cube(
                new LinkedHashSet<>(Arrays.asList(DimensionType.EVENT_TYPE, DimensionType.SPEAKER, DimensionType.YEAR)),
                new LinkedHashSet<>(Arrays.asList(MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY,
                        MeasureType.EVENT_TYPES_QUANTITY)));
    }

    private static Cube createCompaniesCube() {
        return new Cube(
                new LinkedHashSet<>(Arrays.asList(DimensionType.EVENT_TYPE, DimensionType.COMPANY, DimensionType.SPEAKER,
                        DimensionType.YEAR)),
                new LinkedHashSet<>(Arrays.asList(MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY,
                        MeasureType.EVENT_TYPES_QUANTITY, MeasureType.SPEAKERS_QUANTITY, MeasureType.JAVA_CHAMPIONS_QUANTITY,
                        MeasureType.MVPS_QUANTITY)));
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> convertList(List<Object> source) {
        return source.stream()
                .map(e -> (T) e)
                .collect(Collectors.toList());
    }

    @Test
    void fillDimensions() {
        OlapDaoImpl olapDaoImpl = new OlapDaoImpl(eventTypeDao);
        Cube eventTypesCube = createEventTypesCube();
        Cube speakersCube = createSpeakersCube();
        Cube companiesCube = createCompaniesCube();

        City city0 = new City(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City0")));
        City city1 = new City(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City1")));

        List<Integer> years = List.of(2020, 2021);

        assertEquals(Collections.emptyList(), convertList(eventTypesCube.getDimensionValues(DimensionType.EVENT_TYPE)));
        assertEquals(Collections.emptyList(), convertList(eventTypesCube.getDimensionValues(DimensionType.CITY)));
        assertEquals(Collections.emptyList(), convertList(eventTypesCube.getDimensionValues(DimensionType.YEAR)));

        assertEquals(Collections.emptyList(), convertList(speakersCube.getDimensionValues(DimensionType.EVENT_TYPE)));
        assertEquals(Collections.emptyList(), convertList(speakersCube.getDimensionValues(DimensionType.SPEAKER)));
        assertEquals(Collections.emptyList(), convertList(speakersCube.getDimensionValues(DimensionType.YEAR)));

        assertEquals(Collections.emptyList(), convertList(companiesCube.getDimensionValues(DimensionType.EVENT_TYPE)));
        assertEquals(Collections.emptyList(), convertList(companiesCube.getDimensionValues(DimensionType.COMPANY)));
        assertEquals(Collections.emptyList(), convertList(companiesCube.getDimensionValues(DimensionType.SPEAKER)));
        assertEquals(Collections.emptyList(), convertList(companiesCube.getDimensionValues(DimensionType.YEAR)));

        olapDaoImpl.fillDimensions(eventTypesCube, speakersCube, companiesCube);

        assertThat(convertList(eventTypesCube.getDimensionValues(DimensionType.EVENT_TYPE)), containsInAnyOrder(List.of(eventType0, eventType1, eventType2).toArray()));
        assertThat(convertList(eventTypesCube.getDimensionValues(DimensionType.CITY)), containsInAnyOrder(List.of(city0, city1).toArray()));
        assertThat(convertList(eventTypesCube.getDimensionValues(DimensionType.YEAR)), containsInAnyOrder(years.toArray()));

        assertThat(convertList(speakersCube.getDimensionValues(DimensionType.EVENT_TYPE)), containsInAnyOrder(List.of(eventType0, eventType1, eventType2).toArray()));
        assertThat(convertList(speakersCube.getDimensionValues(DimensionType.SPEAKER)), containsInAnyOrder(List.of(speaker0, speaker1).toArray()));
        assertThat(convertList(speakersCube.getDimensionValues(DimensionType.YEAR)), containsInAnyOrder(years.toArray()));

        assertThat(convertList(companiesCube.getDimensionValues(DimensionType.EVENT_TYPE)), containsInAnyOrder(List.of(eventType0, eventType1, eventType2).toArray()));
        assertThat(convertList(companiesCube.getDimensionValues(DimensionType.COMPANY)), containsInAnyOrder(List.of(company0, company1).toArray()));
        assertThat(convertList(companiesCube.getDimensionValues(DimensionType.SPEAKER)), containsInAnyOrder(List.of(speaker0, speaker1).toArray()));
        assertThat(convertList(companiesCube.getDimensionValues(DimensionType.YEAR)), containsInAnyOrder(years.toArray()));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("fillMeasures method with parameters tests")
    class FillMeasuresTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(
                            eventType0, new City(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City0"))),
                            2020, 2L, 1L, 1L),
                    arguments(
                            eventType1, new City(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City1"))),
                            2021, 3L, 1L, 1L
                    )
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void fillMeasures(EventType eventType, City city, int year, long expectedDuration, long expectedEvents, long expectedTalks) {
            OlapDaoImpl olapDaoImpl = new OlapDaoImpl(eventTypeDao);
            Cube eventTypesCube = createEventTypesCube();
            Cube speakersCube = createSpeakersCube();
            Cube companiesCube = createCompaniesCube();
            Set<Dimension<?>> eventTypeAndCityAndYearDimensions = Set.of(
                    new EventTypeDimension(eventType),
                    new CityDimension(city),
                    new YearDimension(year));

            olapDaoImpl.fillDimensions(eventTypesCube, speakersCube, companiesCube);

            assertEquals(0L, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.DURATION));
            assertEquals(0L, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.EVENTS_QUANTITY));
            assertEquals(0L, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.TALKS_QUANTITY));

            olapDaoImpl.fillMeasures(eventTypesCube, speakersCube, companiesCube);

            assertEquals(expectedDuration, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.DURATION));
            assertEquals(expectedEvents, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.EVENTS_QUANTITY));
            assertEquals(expectedTalks, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.TALKS_QUANTITY));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("iterateSpeakers method with parameters tests")
    class IterateSpeakersTest {
        private Stream<Arguments> data() {
            EventTypeDimension eventTypeDimension0 = new EventTypeDimension(eventType0);
            EventTypeDimension eventTypeDimension1 = new EventTypeDimension(eventType1);

            CityDimension cityDimension0 = new CityDimension(new City(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City0"))));
            CityDimension cityDimension1 = new CityDimension(new City(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "City1"))));

            YearDimension yearDimension0 = new YearDimension(2020);
            YearDimension yearDimension1 = new YearDimension(2021);

            return Stream.of(
                    arguments(
                            eventTypeDimension0, yearDimension0, Set.of(eventTypeDimension0, cityDimension0, yearDimension0),
                            event0, talk0, speaker0, 1L, 1L, 1L, 1L, 1L, 0L),
                    arguments(
                            eventTypeDimension1, yearDimension1, Set.of(eventTypeDimension1, cityDimension1, yearDimension1),
                            event1, talk1, speaker1, 1L, 1L, 1L, 1L, 0L, 1L)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void iterateSpeakers(EventTypeDimension eventTypeDimension, YearDimension yearDimension,
                             Set<Dimension<?>> eventTypeAndCityAndYearDimensions, Event event, Talk talk, Speaker speaker,
                             long expectedQuantitySpeakers, long expectedTalks, long expectedEvents,
                             long expectedEventTypes, long javaChampionsSpeakers, long mvpsSpeakers) {
            OlapDaoImpl olapDaoImpl = new OlapDaoImpl(eventTypeDao);
            Cube eventTypesCube = createEventTypesCube();
            Cube speakersCube = createSpeakersCube();
            Cube companiesCube = createCompaniesCube();
            OlapDaoImpl.Cubes cubes = new OlapDaoImpl.Cubes(eventTypesCube, speakersCube, companiesCube);
            SpeakerDimension speakerDimension = new SpeakerDimension(speaker);
            Set<Dimension<?>> eventTypeAndSpeakerAndYearDimensions = Set.of(eventTypeDimension, speakerDimension, yearDimension);

            olapDaoImpl.fillDimensions(eventTypesCube, speakersCube, companiesCube);

            assertEquals(0L, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.SPEAKERS_QUANTITY));
            assertEquals(0L, speakersCube.getMeasureValue(eventTypeAndSpeakerAndYearDimensions, MeasureType.TALKS_QUANTITY));
            assertEquals(0L, speakersCube.getMeasureValue(eventTypeAndSpeakerAndYearDimensions, MeasureType.EVENTS_QUANTITY));
            assertEquals(0L, speakersCube.getMeasureValue(eventTypeAndSpeakerAndYearDimensions, MeasureType.EVENT_TYPES_QUANTITY));
            assertEquals(0L, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.JAVA_CHAMPIONS_QUANTITY));
            assertEquals(0L, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.MVPS_QUANTITY));

            olapDaoImpl.iterateSpeakers(cubes, eventTypeDimension, yearDimension, eventTypeAndCityAndYearDimensions, event, talk);

            assertEquals(expectedQuantitySpeakers, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.SPEAKERS_QUANTITY));
            assertEquals(expectedTalks, speakersCube.getMeasureValue(eventTypeAndSpeakerAndYearDimensions, MeasureType.TALKS_QUANTITY));
            assertEquals(expectedEvents, speakersCube.getMeasureValue(eventTypeAndSpeakerAndYearDimensions, MeasureType.EVENTS_QUANTITY));
            assertEquals(expectedEventTypes, speakersCube.getMeasureValue(eventTypeAndSpeakerAndYearDimensions, MeasureType.EVENT_TYPES_QUANTITY));
            assertEquals(javaChampionsSpeakers, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.JAVA_CHAMPIONS_QUANTITY));
            assertEquals(mvpsSpeakers, eventTypesCube.getMeasureValue(eventTypeAndCityAndYearDimensions, MeasureType.MVPS_QUANTITY));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("iterateCompanies method with parameters tests")
    class IterateCompaniesTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(
                            new EventTypeDimension(eventType0), new YearDimension(2020), new SpeakerDimension(speaker0),
                            event0, talk0, company0, 1L, 1L, 1L, 1L, 1L, 0L),
                    arguments(
                            new EventTypeDimension(eventType1), new YearDimension(2021), new SpeakerDimension(speaker1),
                            event1, talk1, company1, 1L, 1L, 1L, 1L, 0L, 1L)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void iterateCompanies(EventTypeDimension eventTypeDimension, YearDimension yearDimension, SpeakerDimension speakerDimension,
                              Event event, Talk talk, Company company, long expectedQuantitySpeakers, long expectedTalks,
                              long expectedEvents, long expectedEventTypes, long javaChampionsSpeakers, long mvpsSpeakers) {
            OlapDaoImpl olapDaoImpl = new OlapDaoImpl(eventTypeDao);
            Cube eventTypesCube = createEventTypesCube();
            Cube speakersCube = createSpeakersCube();
            Cube companiesCube = createCompaniesCube();
            Set<Dimension<?>> eventTypeAndCompanyAndSpeakerAndYearDimensions = Set.of(
                    eventTypeDimension, new CompanyDimension(company), speakerDimension, yearDimension);

            olapDaoImpl.fillDimensions(eventTypesCube, speakersCube, companiesCube);

            assertEquals(0L, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.SPEAKERS_QUANTITY));
            assertEquals(0L, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.TALKS_QUANTITY));
            assertEquals(0L, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.EVENTS_QUANTITY));
            assertEquals(0L, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.EVENT_TYPES_QUANTITY));
            assertEquals(0L, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.JAVA_CHAMPIONS_QUANTITY));
            assertEquals(0L, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.MVPS_QUANTITY));

            olapDaoImpl.iterateCompanies(companiesCube, eventTypeDimension, yearDimension, speakerDimension, event, talk);

            assertEquals(expectedQuantitySpeakers, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.SPEAKERS_QUANTITY));
            assertEquals(expectedTalks, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.TALKS_QUANTITY));
            assertEquals(expectedEvents, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.EVENTS_QUANTITY));
            assertEquals(expectedEventTypes, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.EVENT_TYPES_QUANTITY));
            assertEquals(javaChampionsSpeakers, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.JAVA_CHAMPIONS_QUANTITY));
            assertEquals(mvpsSpeakers, companiesCube.getMeasureValue(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.MVPS_QUANTITY));
        }
    }
}
