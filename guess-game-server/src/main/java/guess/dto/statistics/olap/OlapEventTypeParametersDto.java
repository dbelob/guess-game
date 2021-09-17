package guess.dto.statistics.olap;

/**
 * OLAP event type parameters DTO.
 */
public class OlapEventTypeParametersDto extends OlapCommonParametersDto {
    private Long speakerId;
    private Long companyId;

    public Long getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(Long speakerId) {
        this.speakerId = speakerId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
