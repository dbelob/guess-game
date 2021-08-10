package guess.dto.statistics;

import guess.domain.statistics.olap.Cube;
import guess.domain.statistics.olap.Measure;

import java.util.List;

/**
 * OLAP parameters DTO.
 */
public class OlapParametersDto {
    private Cube cube;
    private Measure measure;
    private Long organizerId;
    private Long eventTypeId;
    private List<Long> speakerIds;
    private List<Long> companyIds;

    public Cube getCube() {
        return cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
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
