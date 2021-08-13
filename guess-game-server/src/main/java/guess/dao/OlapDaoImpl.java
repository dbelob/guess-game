package guess.dao;

import guess.domain.statistics.olap.Cube;
import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.DimensionType;
import guess.domain.statistics.olap.MeasureType;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * OLAP DAO implementation.
 */
@Repository
public class OlapDaoImpl implements OlapDao {
    private final Map<CubeType, Cube> cubes = new EnumMap<>(CubeType.class);

    public OlapDaoImpl() {
        cubes.put(CubeType.EVENT_TYPES,
                new Cube(
                        new LinkedHashSet<>(Arrays.asList(DimensionType.EVENT_TYPE, DimensionType.YEAR)),
                        new LinkedHashSet<>(Arrays.asList(MeasureType.DURATION, MeasureType.EVENTS_QUANTITY,
                                MeasureType.TALKS_QUANTITY, MeasureType.SPEAKERS_QUANTITY,
                                MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY))));
        cubes.put(CubeType.SPEAKERS,
                new Cube(
                        new LinkedHashSet<>(Arrays.asList(DimensionType.EVENT_TYPE, DimensionType.SPEAKER, DimensionType.YEAR)),
                        new LinkedHashSet<>(Arrays.asList(MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY,
                                MeasureType.EVENT_TYPES_QUANTITY, MeasureType.JAVA_CHAMPIONS_QUANTITY,
                                MeasureType.MVPS_QUANTITY))));
        cubes.put(CubeType.COMPANIES,
                new Cube(
                        new LinkedHashSet<>(Arrays.asList(DimensionType.EVENT_TYPE, DimensionType.COMPANY, DimensionType.YEAR)),
                        new LinkedHashSet<>(Arrays.asList(MeasureType.SPEAKERS_QUANTITY, MeasureType.TALKS_QUANTITY,
                                MeasureType.EVENTS_QUANTITY, MeasureType.EVENT_TYPES_QUANTITY,
                                MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY))));
    }

    @Override
    public List<MeasureType> getMeasureTypes(CubeType cubeType) {
        Cube cube = cubes.get(cubeType);

        return List.copyOf(Objects.requireNonNull(cube, () -> String.format("Cube type %s not found", cubeType))
                .getMeasureTypes());
    }
}
