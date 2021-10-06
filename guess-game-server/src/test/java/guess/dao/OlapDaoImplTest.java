package guess.dao;

import guess.domain.Conference;
import guess.domain.source.EventType;
import guess.domain.statistics.olap.Cube;
import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.DimensionType;
import guess.domain.statistics.olap.MeasureType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OlapDaoImplTest {
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

    @Test
    void fillDimensions() {
        OlapDaoImpl olapDaoImpl = new OlapDaoImpl(eventTypeDao);

        assertDoesNotThrow(() -> olapDaoImpl.fillDimensions(
                createEventTypesCube(),
                createSpeakersCube(),
                createCompaniesCube()));
    }

    @Test
    void fillMeasures() {
        OlapDaoImpl olapDaoImpl = new OlapDaoImpl(eventTypeDao);
        Cube eventTypesCube = createEventTypesCube();
        Cube speakersCube = createSpeakersCube();
        Cube companiesCube = createCompaniesCube();

        olapDaoImpl.fillDimensions(eventTypesCube, speakersCube, companiesCube);

        assertDoesNotThrow(() -> olapDaoImpl.fillMeasures(eventTypesCube, speakersCube, companiesCube));
    }

    @Test
    void iterateSpeakers() {
    }

    @Test
    void iterateCompanies() {
    }
}
