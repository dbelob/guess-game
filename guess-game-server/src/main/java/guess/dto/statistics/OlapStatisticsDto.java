package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.statistics.olap.OlapStatistics;

/**
 * OLAP statistics DTO.
 */
public class OlapStatisticsDto {
    //TODO: implement

    private final String text;

    public OlapStatisticsDto(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static OlapStatisticsDto convertToDto(OlapStatistics olapStatistics, Language language) {
        //TODO: implement
        return new OlapStatisticsDto("result");
    }
}
