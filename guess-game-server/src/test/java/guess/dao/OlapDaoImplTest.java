package guess.dao;

import guess.domain.Conference;
import guess.domain.source.EventType;
import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    void fillDimensions() {
        //TODO: implement
    }

    @Test
    void fillMeasures() {
        //TODO: implement
    }

    @Test
    void iterateSpeakers() {
        //TODO: implement
    }
}
