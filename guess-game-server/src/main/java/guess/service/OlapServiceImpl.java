package guess.service;

import guess.dao.OlapDao;
import guess.domain.source.Company;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.statistics.olap.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
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
        Predicate<EventType> eventTypePredicate = et ->
                ((isConferences && et.isEventTypeConference()) || (isMeetups && !et.isEventTypeConference())) &&
                        ((organizerId == null) || (et.getOrganizer().getId() == organizerId)) &&
                        ((eventTypeId == null) || (et.getId() == eventTypeId));
        Predicate<Speaker> speakerPredicate = s -> (speakerIds == null) || speakerIds.isEmpty() || speakerIds.contains(s.getId());
        Predicate<Company> companyPredicate = c -> (companyIds == null) || companyIds.isEmpty() || companyIds.contains(c.getId());

        return new OlapStatistics(
                CubeType.EVENT_TYPES.equals(cubeType) ?
                        getOlapEntityStatistics(cubeType, measureType, isConferences, isMeetups, organizerId,
                                eventTypeId, DimensionType.EVENT_TYPE, eventTypePredicate) :
                        null,
                CubeType.SPEAKERS.equals(cubeType) ?
                        getOlapEntityStatistics(cubeType, measureType, isConferences, isMeetups, organizerId,
                                eventTypeId, DimensionType.SPEAKER, speakerPredicate) :
                        null,
                CubeType.COMPANIES.equals(cubeType) ?
                        getOlapEntityStatistics(cubeType, measureType, isConferences, isMeetups, organizerId,
                                eventTypeId, DimensionType.COMPANY, companyPredicate) :
                        null
        );
    }

    @SuppressWarnings("unchecked")
    private <T, S> OlapEntityStatistics<T, S> getOlapEntityStatistics(CubeType cubeType, MeasureType measureType,
                                                                      boolean isConferences, boolean isMeetups,
                                                                      Long organizerId, Long eventTypeId,
                                                                      DimensionType firstDimensionType,
                                                                      Predicate<S> firstDimensionPredicate) {
        Cube cube = olapDao.getCube(cubeType);
        List<S> firstDimensionValues = cube.getDimensionValues(firstDimensionType).stream()
                .map(v -> (S) v)
                .filter(firstDimensionPredicate)
                .collect(Collectors.toList());
        List<T> secondDimensionValues = cube.getDimensionValues(DimensionType.YEAR).stream()
                .map(v -> (T) v)
                .sorted()
                .collect(Collectors.toList());

        List<OlapEntityMetrics<S>> entityMetricsList = cube.getMeasureValueEntities(
                firstDimensionType, firstDimensionValues, DimensionType.YEAR, secondDimensionValues,
                measureType, OlapEntityMetrics::new);

        return new OlapEntityStatistics<>(secondDimensionValues, entityMetricsList);
    }
}
