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
                                            Long organizerId, List<Long> eventTypeIds, List<Long> speakerIds, List<Long> companyIds) {
        Predicate<EventType> eventTypePredicate = et ->
                ((isConferences && et.isEventTypeConference()) || (isMeetups && !et.isEventTypeConference())) &&
                        ((organizerId == null) || (et.getOrganizer().getId() == organizerId)) &&
                        ((eventTypeIds == null) || eventTypeIds.isEmpty() || eventTypeIds.contains(et.getId()));
        Predicate<Speaker> speakerPredicate = s -> (speakerIds == null) || speakerIds.isEmpty() || speakerIds.contains(s.getId());
        Predicate<Company> companyPredicate = c -> (companyIds == null) || companyIds.isEmpty() || companyIds.contains(c.getId());

        return new OlapStatistics(
                CubeType.EVENT_TYPES.equals(cubeType) ?
                        getOlapEntityStatistics(cubeType, measureType, DimensionType.EVENT_TYPE, eventTypePredicate,
                                eventTypePredicate) :
                        null,
                CubeType.SPEAKERS.equals(cubeType) ?
                        getOlapEntityStatistics(cubeType, measureType, DimensionType.SPEAKER, speakerPredicate,
                                eventTypePredicate) :
                        null,
                CubeType.COMPANIES.equals(cubeType) ?
                        getOlapEntityStatistics(cubeType, measureType, DimensionType.COMPANY, companyPredicate,
                                eventTypePredicate) :
                        null
        );
    }

    @SuppressWarnings("unchecked")
    private <T, S, U> OlapEntityStatistics<T, S> getOlapEntityStatistics(CubeType cubeType, MeasureType measureType,
                                                                         DimensionType firstDimensionType,
                                                                         Predicate<S> firstDimensionPredicate,
                                                                         Predicate<U> filterDimensionPredicate) {
        Cube cube = olapDao.getCube(cubeType);
        List<S> firstDimensionValues = cube.getDimensionValues(firstDimensionType).stream()
                .map(v -> (S) v)
                .filter(firstDimensionPredicate)
                .collect(Collectors.toList());
        List<T> secondDimensionValues = cube.getDimensionValues(DimensionType.YEAR).stream()
                .map(v -> (T) v)
                .sorted()
                .collect(Collectors.toList());
        List<U> filterDimensionValues = cube.getDimensionValues(DimensionType.EVENT_TYPE).stream()
                .map(v -> (U) v)
                .filter(filterDimensionPredicate)
                .collect(Collectors.toList());

        return cube.getMeasureValueEntities(
                firstDimensionType, firstDimensionValues, DimensionType.YEAR, secondDimensionValues,
                DimensionType.EVENT_TYPE, filterDimensionValues, measureType, OlapEntityMetrics::new,
                (measureValues, total) -> new OlapEntityMetrics<Void>(null, measureValues, total),
                OlapEntityStatistics::new);
    }
}
