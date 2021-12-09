package guess.service;

import guess.dao.OlapDao;
import guess.domain.source.Company;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.statistics.olap.*;
import guess.domain.statistics.olap.dimension.City;
import guess.dto.statistics.olap.OlapCityParametersDto;
import guess.dto.statistics.olap.OlapEventTypeParametersDto;
import guess.dto.statistics.olap.OlapParametersDto;
import guess.dto.statistics.olap.OlapSpeakerParametersDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

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
    public OlapStatistics getOlapStatistics(OlapParametersDto op) {
        Predicate<EventType> eventTypePredicate = et ->
                ((op.isConferences() && et.isEventTypeConference()) || (op.isMeetups() && !et.isEventTypeConference())) &&
                        ((op.getOrganizerId() == null) || (et.getOrganizer().getId() == op.getOrganizerId())) &&
                        ((op.getEventTypeIds() == null) || op.getEventTypeIds().isEmpty() || op.getEventTypeIds().contains(et.getId()));
        Predicate<Speaker> speakerPredicate = s -> (op.getSpeakerIds() == null) || op.getSpeakerIds().isEmpty() || op.getSpeakerIds().contains(s.getId());
        Predicate<Company> companyPredicate = c -> (op.getCompanyIds() == null) || op.getCompanyIds().isEmpty() || op.getCompanyIds().contains(c.getId());

        return new OlapStatistics(
                CubeType.EVENT_TYPES.equals(op.getCubeType()) ?
                        getOlapEntityStatistics(op.getCubeType(), op.getMeasureType(), DimensionType.EVENT_TYPE, eventTypePredicate,
                                DimensionType.EVENT_TYPE, eventTypePredicate) :
                        null,
                CubeType.SPEAKERS.equals(op.getCubeType()) ?
                        getOlapEntityStatistics(op.getCubeType(), op.getMeasureType(), DimensionType.SPEAKER, speakerPredicate,
                                DimensionType.EVENT_TYPE, eventTypePredicate) :
                        null,
                CubeType.COMPANIES.equals(op.getCubeType()) ?
                        getOlapEntityStatistics(op.getCubeType(), op.getMeasureType(), DimensionType.COMPANY, companyPredicate,
                                DimensionType.EVENT_TYPE, eventTypePredicate) :
                        null
        );
    }

    @Override
    public OlapEntityStatistics<Integer, EventType> getOlapEventTypeStatistics(OlapEventTypeParametersDto op) {
        Predicate<EventType> eventTypePredicate = et ->
                ((op.isConferences() && et.isEventTypeConference()) || (op.isMeetups() && !et.isEventTypeConference())) &&
                        ((op.getOrganizerId() == null) || (et.getOrganizer().getId() == op.getOrganizerId())) &&
                        ((op.getEventTypeIds() == null) || op.getEventTypeIds().isEmpty() || op.getEventTypeIds().contains(et.getId()));
        OlapEntityStatistics<Integer, EventType> olapEventTypeStatistics;

        switch (op.getCubeType()) {
            case SPEAKERS:
                Predicate<Speaker> speakerPredicate = s -> (op.getSpeakerId() != null) && (s.getId() == op.getSpeakerId());

                olapEventTypeStatistics = getOlapEntityStatistics(op.getCubeType(), op.getMeasureType(), DimensionType.EVENT_TYPE, eventTypePredicate,
                        DimensionType.SPEAKER, speakerPredicate);
                break;
            case COMPANIES:
                Predicate<Company> companyPredicate = c -> (op.getCompanyId() != null) && (c.getId() == op.getCompanyId());

                olapEventTypeStatistics = getOlapEntityStatistics(op.getCubeType(), op.getMeasureType(), DimensionType.EVENT_TYPE, eventTypePredicate,
                        DimensionType.COMPANY, companyPredicate);
                break;
            default:
                throw new IllegalArgumentException(String.format("Invalid cube type %s", op.getCubeType()));
        }

        olapEventTypeStatistics.getMetricsList().removeIf(m -> m.total() == 0);

        return olapEventTypeStatistics;
    }

    @Override
    public OlapEntityStatistics<Integer, Speaker> getOlapSpeakerStatistics(OlapSpeakerParametersDto op) {
        Predicate<Speaker> speakerPredicate = s -> (op.getCompanyId() != null) && (s.getCompanyIds().contains(op.getCompanyId()));
        Predicate<EventType> eventTypePredicate = et -> (op.getEventTypeId() != null) && (et.getId() == op.getEventTypeId());
        OlapEntityStatistics<Integer, Speaker> olapSpeakerStatistics = getOlapEntityStatistics(op.getCubeType(), op.getMeasureType(),
                DimensionType.SPEAKER, speakerPredicate, DimensionType.EVENT_TYPE, eventTypePredicate);

        olapSpeakerStatistics.getMetricsList().removeIf(m -> m.total() == 0);

        return olapSpeakerStatistics;
    }

    @Override
    public OlapEntityStatistics<Integer, City> getOlapCityStatistics(OlapCityParametersDto op) {
        Predicate<City> cityPredicate = c -> true;
        Predicate<EventType> eventTypePredicate = et -> (op.getEventTypeId() != null) && (et.getId() == op.getEventTypeId());
        OlapEntityStatistics<Integer, City> olapCityStatistics = getOlapEntityStatistics(op.getCubeType(), op.getMeasureType(),
                DimensionType.CITY, cityPredicate, DimensionType.EVENT_TYPE, eventTypePredicate);

        olapCityStatistics.getMetricsList().removeIf(m -> m.total() == 0);

        return olapCityStatistics;
    }

    @SuppressWarnings("unchecked")
    <T, S, U> OlapEntityStatistics<T, S> getOlapEntityStatistics(CubeType cubeType, MeasureType measureType,
                                                                 DimensionType firstDimensionType,
                                                                 Predicate<S> firstDimensionPredicate,
                                                                 DimensionType filterDimensionType,
                                                                 Predicate<U> filterDimensionPredicate) {
        Cube cube = olapDao.getCube(cubeType);
        List<S> firstDimensionValues = cube.getDimensionValues(firstDimensionType).stream()
                .map(v -> (S) v)
                .filter(firstDimensionPredicate)
                .toList();
        List<T> secondDimensionValues = cube.getDimensionValues(DimensionType.YEAR).stream()
                .map(v -> (T) v)
                .sorted()
                .toList();
        List<U> filterDimensionValues = cube.getDimensionValues(filterDimensionType).stream()
                .map(v -> (U) v)
                .filter(filterDimensionPredicate)
                .toList();

        return cube.getMeasureValueEntities(
                new DimensionTypeValues<>(firstDimensionType, firstDimensionValues),
                new DimensionTypeValues<>(DimensionType.YEAR, secondDimensionValues),
                new DimensionTypeValues<>(filterDimensionType, filterDimensionValues),
                measureType, OlapEntityMetrics::new,
                (measureValues, total) -> new OlapEntityMetrics<Void>(null, measureValues, total),
                OlapEntityStatistics::new);
    }
}
