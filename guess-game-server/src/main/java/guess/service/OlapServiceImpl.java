package guess.service;

import guess.dao.OlapDao;
import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;
import guess.domain.statistics.olap.OlapStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        //TODO: implement
        return new OlapStatistics();
    }
}
