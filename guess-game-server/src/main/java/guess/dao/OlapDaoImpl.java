package guess.dao;

import guess.domain.statistics.olap.Cube;
import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.DimensionType;
import guess.domain.statistics.olap.MeasureType;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * OLAP DAO implementation.
 */
@Repository
public class OlapDaoImpl implements OlapDao {
    private final Map<CubeType, Cube> cubes = new HashMap<>();

    public OlapDaoImpl() {
        cubes.put(CubeType.EVENT_TYPES,
                new Cube(
                        List.of(DimensionType.EVENT_TYPE, DimensionType.YEAR),
                        List.of(MeasureType.DURATION, MeasureType.EVENTS_QUANTITY, MeasureType.TALKS_QUANTITY,
                                MeasureType.SPEAKERS_QUANTITY, MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY)));
        cubes.put(CubeType.SPEAKERS,
                new Cube(
                        List.of(DimensionType.EVENT_TYPE, DimensionType.SPEAKER, DimensionType.YEAR),
                        List.of(MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY, MeasureType.EVENT_TYPES_QUANTITY,
                                MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY)));
        cubes.put(CubeType.COMPANIES,
                new Cube(
                        List.of(DimensionType.EVENT_TYPE, DimensionType.COMPANY, DimensionType.YEAR),
                        List.of(MeasureType.SPEAKERS_QUANTITY, MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY,
                                MeasureType.EVENT_TYPES_QUANTITY, MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY)));
    }

    @Override
    public List<MeasureType> getMeasureTypes(CubeType cubeType) {
        Cube cube = cubes.get(cubeType);

        return Objects.requireNonNull(cube, () -> String.format("Cube type %s not found", cubeType))
                .getMeasureTypes();
    }
}
