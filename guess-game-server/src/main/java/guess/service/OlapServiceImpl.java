package guess.service;

import guess.dao.OlapDao;
import guess.domain.source.Company;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.statistics.olap.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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
        Cube cube = olapDao.getCube(cubeType);
        List<Integer> firstDimensionValues = cube.getDimensionValues(DimensionType.YEAR).stream()
                .map(v -> (Integer) v)
                .sorted()
                .collect(Collectors.toList());
        List<OlapEntityMetrics<EventType>> eventTypeMetricsList = Collections.emptyList();
        List<OlapEntityMetrics<Speaker>> speakerMetricsList = Collections.emptyList();
        List<OlapEntityMetrics<Company>> companyMetricsList = Collections.emptyList();

        if (CubeType.EVENT_TYPES.equals(cubeType)) {
            //TODO: implement
        } else if (CubeType.SPEAKERS.equals(cubeType)) {
            //TODO: implement
        } else if (CubeType.COMPANIES.equals(cubeType)) {
            //TODO: implement
        }

        //TODO: implement
        return new OlapStatistics(
                CubeType.EVENT_TYPES.equals(cubeType) ?
                        new OlapEntityStatistics<>(
                                firstDimensionValues,
                                eventTypeMetricsList) :
                        null,
                CubeType.SPEAKERS.equals(cubeType) ?
                        new OlapEntityStatistics<>(
                                firstDimensionValues,
                                speakerMetricsList) :
                        null,
                CubeType.COMPANIES.equals(cubeType) ?
                        new OlapEntityStatistics<>(
                                firstDimensionValues,
                                companyMetricsList) :
                        null
        );
    }
}
