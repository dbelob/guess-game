package guess.service;

import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;
import guess.domain.statistics.olap.OlapEntityStatistics;
import guess.domain.statistics.olap.OlapStatistics;
import guess.domain.statistics.olap.dimension.City;
import guess.dto.statistics.olap.OlapCityParametersDto;
import guess.dto.statistics.olap.OlapEventTypeParametersDto;
import guess.dto.statistics.olap.OlapParametersDto;
import guess.dto.statistics.olap.OlapSpeakerParametersDto;

import java.util.List;

/**
 * OLAP service.
 */
public interface OlapService {
    List<MeasureType> getMeasureTypes(CubeType cubeType);

    OlapStatistics getOlapStatistics(OlapParametersDto olapParameters);

    OlapEntityStatistics<Integer, EventType> getOlapEventTypeStatistics(OlapEventTypeParametersDto olapParameters);

    OlapEntityStatistics<Integer, Speaker> getOlapSpeakerStatistics(OlapSpeakerParametersDto olapParameters);

    OlapEntityStatistics<Integer, City> getOlapCityStatistics(OlapCityParametersDto olapParameters);
}
