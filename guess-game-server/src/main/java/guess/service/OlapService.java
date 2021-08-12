package guess.service;

import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;

import java.util.List;

/**
 * OLAP service.
 */
public interface OlapService {
    List<MeasureType> getMeasureTypes(CubeType cubeType);
}
