package guess.dao;

import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;

import java.util.List;

/**
 * OLAP DAO.
 */
public interface OlapDao {
    List<MeasureType> getMeasureTypes(CubeType cubeType);
}
