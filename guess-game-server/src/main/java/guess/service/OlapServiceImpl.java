package guess.service;

import guess.dao.OlapDao;
import guess.domain.statistics.olap.*;
import guess.domain.statistics.olap.dimension.Dimension;
import guess.domain.statistics.olap.dimension.DimensionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * OLAP service implementation.
 */
@Service
public class OlapServiceImpl implements OlapService {
    private final OlapDao olapDao;

    @Autowired
    public OlapServiceImpl(OlapDao olapDao) {
        this.olapDao = olapDao;
    }

    @Override
    public List<MeasureType> getMeasureTypes(CubeType cubeType) {
        return olapDao.getMeasureTypes(cubeType);
    }

    @Override
    public OlapStatistics getOlapStatistics(CubeType cubeType, MeasureType measureType, boolean isConferences, boolean isMeetups,
                                            Long organizerId, Long eventTypeId, List<Long> speakerIds, List<Long> companyIds) {
        return new OlapStatistics(
                CubeType.EVENT_TYPES.equals(cubeType) ?
                        getOlapEntityStatistics(cubeType, measureType, isConferences, isMeetups, organizerId,
                                eventTypeId, speakerIds, companyIds, DimensionType.EVENT_TYPE) :
                        null,
                CubeType.SPEAKERS.equals(cubeType) ?
                        getOlapEntityStatistics(cubeType, measureType, isConferences, isMeetups, organizerId,
                                eventTypeId, speakerIds, companyIds, DimensionType.SPEAKER) :
                        null,
                CubeType.COMPANIES.equals(cubeType) ?
                        getOlapEntityStatistics(cubeType, measureType, isConferences, isMeetups, organizerId,
                                eventTypeId, speakerIds, companyIds, DimensionType.COMPANY) :
                        null
        );
    }

    @SuppressWarnings("unchecked")
    private <T, S> OlapEntityStatistics<T, S> getOlapEntityStatistics(CubeType cubeType, MeasureType measureType,
                                                                      boolean isConferences, boolean isMeetups,
                                                                      Long organizerId, Long eventTypeId,
                                                                      List<Long> speakerIds, List<Long> companyIds,
                                                                      DimensionType secondDimensionType) {
        Cube cube = olapDao.getCube(cubeType);
        List<T> firstDimensionValues = cube.getDimensionValues(DimensionType.YEAR).stream()
                .map(v -> (T) v)
                .sorted()
                .collect(Collectors.toList());
        List<S> secondDimensionValues = cube.getDimensionValues(secondDimensionType).stream()
                .map(v -> (S) v)
                .collect(Collectors.toList());
        List<OlapEntityMetrics<S>> entityMetricsList = new ArrayList<>();

        for (S secondDimensionValue : secondDimensionValues) {
            List<Long> measureValues = new ArrayList<>();

            for (T firstDimensionValue : firstDimensionValues) {
                Set<Dimension<?>> dimensions = Set.of(
                        DimensionFactory.create(DimensionType.YEAR, firstDimensionValue),
                        DimensionFactory.create(secondDimensionType, secondDimensionValue));

                measureValues.add(cube.getMeasureValue(dimensions, measureType));
            }

            entityMetricsList.add(new OlapEntityMetrics<>(secondDimensionValue, measureValues));
        }

        return new OlapEntityStatistics<>(firstDimensionValues, entityMetricsList);
    }
}
