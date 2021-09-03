package guess.dto.statistics.olap;

import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;

import java.util.List;

/**
 * OLAP parameters DTO.
 */
public class OlapParametersDto {
    private CubeType cubeType;
    private MeasureType measureType;
    private boolean isConferences;
    private boolean isMeetups;
    private Long organizerId;
    private Long eventTypeId;
    private List<Long> speakerIds;
    private List<Long> companyIds;

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

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public List<Long> getSpeakerIds() {
        return speakerIds;
    }

    public void setSpeakerIds(List<Long> speakerIds) {
        this.speakerIds = speakerIds;
    }

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds;
    }
}
