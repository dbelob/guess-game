package guess.dto.statistics.olap;

import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;

import java.util.List;

/**
 * OLAP common parameters DTO.
 */
public class OlapCommonParametersDto {
    private CubeType cubeType;
    private MeasureType measureType;
    private boolean isConferences;
    private boolean isMeetups;
    private Long organizerId;
    private List<Long> eventTypeIds;

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

    public boolean isConferences() {
        return isConferences;
    }

    public void setConferences(boolean conferences) {
        isConferences = conferences;
    }

    public boolean isMeetups() {
        return isMeetups;
    }

    public void setMeetups(boolean meetups) {
        isMeetups = meetups;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public List<Long> getEventTypeIds() {
        return eventTypeIds;
    }

    public void setEventTypeIds(List<Long> eventTypeIds) {
        this.eventTypeIds = eventTypeIds;
    }
}
