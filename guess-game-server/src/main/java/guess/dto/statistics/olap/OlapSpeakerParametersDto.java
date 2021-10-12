package guess.dto.statistics.olap;

import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;

/**
 * OLAP speaker parameters DTO.
 */
public class OlapSpeakerParametersDto {
    private CubeType cubeType;
    private MeasureType measureType;
    private Long companyId;
    private Long eventTypeId;

    public CubeType getCubeType() {
        return cubeType;
    }

    public void setCubeType(CubeType cubeType) {
        this.cubeType = cubeType;
    }

    public MeasureType getMeasureType() {
        return measureType;
    }

    public void setMeasureType(MeasureType measureType) {
        this.measureType = measureType;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
}
