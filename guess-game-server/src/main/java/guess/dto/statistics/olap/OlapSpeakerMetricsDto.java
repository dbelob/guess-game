package guess.dto.statistics.olap;

import guess.domain.Language;
import guess.domain.source.Speaker;
import guess.domain.statistics.olap.OlapEntityMetrics;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * OLAP speaker metrics DTO.
 */
public class OlapSpeakerMetricsDto extends OlapEntityMetricsDto {
    private final String photoFileName;

    public OlapSpeakerMetricsDto(long id, String name, String photoFileName, List<Long> measureValues, Long total) {
        super(id, name, measureValues, total);

        this.photoFileName = photoFileName;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public static OlapSpeakerMetricsDto convertToDto(OlapEntityMetrics<Speaker> speakerMetrics, Language language, Set<Speaker> speakerDuplicates) {
        var speaker = speakerMetrics.getEntity();
        String name = LocalizationUtils.getSpeakerNameWithLastNameFirst(speaker, language, speakerDuplicates);

        return new OlapSpeakerMetricsDto(
                speaker.getId(),
                name,
                speaker.getPhotoFileName(),
                speakerMetrics.getMeasureValues(),
                speakerMetrics.getTotal());
    }

    public static List<OlapSpeakerMetricsDto> convertToDto(List<OlapEntityMetrics<Speaker>> speakerMetricsList, Language language) {
        List<Speaker> speakers = speakerMetricsList.stream()
                .map(OlapEntityMetrics::getEntity)
                .toList();
        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);

        return speakerMetricsList.stream()
                .map(sm -> convertToDto(sm, language, speakerDuplicates))
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OlapSpeakerMetricsDto)) return false;
        if (!super.equals(o)) return false;
        OlapSpeakerMetricsDto that = (OlapSpeakerMetricsDto) o;
        return Objects.equals(getPhotoFileName(), that.getPhotoFileName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPhotoFileName());
    }

    @Override
    public String toString() {
        return "OlapSpeakerMetricsDto{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", photoFileName='" + photoFileName + '\'' +
                '}';
    }
}
