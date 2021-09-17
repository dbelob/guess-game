package guess.dto.statistics.olap;

import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;

import java.util.List;

/**
 * OLAP parameters DTO.
 */
public class OlapParametersDto extends OlapCommonParametersDto {
    private List<Long> speakerIds;
    private List<Long> companyIds;

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
