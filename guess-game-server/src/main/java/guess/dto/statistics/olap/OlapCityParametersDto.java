package guess.dto.statistics.olap;

import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;

/**
 * OLAP city parameters DTO.
 */
public class OlapCityParametersDto {
    private CubeType cubeType;
    private MeasureType measureType;
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

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
}
