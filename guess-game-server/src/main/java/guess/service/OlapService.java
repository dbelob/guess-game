package guess.service;

import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;
import guess.domain.statistics.olap.OlapStatistics;

import java.util.List;

/**
 * OLAP service.
 */
public interface OlapService {
    List<MeasureType> getMeasureTypes(CubeType cubeType);

    OlapStatistics getOlapStatistics(CubeType cubeType, MeasureType measureType, boolean isConferences, boolean isMeetups,
                                     Long organizerId, Long eventTypeId, List<Long> speakerIds, List<Long> companyIds);
}
