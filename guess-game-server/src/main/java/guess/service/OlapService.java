package guess.service;

import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;
import guess.domain.statistics.olap.OlapEntityStatistics;
import guess.domain.statistics.olap.OlapStatistics;

import java.util.List;

/**
 * OLAP service.
 */
public interface OlapService {
    List<MeasureType> getMeasureTypes(CubeType cubeType);

    OlapStatistics getOlapStatistics(CubeType cubeType, MeasureType measureType, boolean isConferences, boolean isMeetups,
                                     Long organizerId, List<Long> eventTypeIds, List<Long> speakerIds, List<Long> companyIds);

    OlapEntityStatistics<Integer, EventType> getOlapEventTypeStatistics(CubeType cubeType, MeasureType measureType,
                                                                        boolean isConferences, boolean isMeetups,
                                                                        Long organizerId, List<Long> eventTypeIds,
                                                                        Long speakerId, Long companyId);

    OlapEntityStatistics<Integer, Speaker> getOlapSpeakerStatistics(CubeType cubeType, MeasureType measureType,
                                                                    Long companyId, Long eventTypeId);
}
