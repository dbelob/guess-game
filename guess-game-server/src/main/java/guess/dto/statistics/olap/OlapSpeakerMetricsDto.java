package guess.dto.statistics.olap;

import guess.domain.Language;
import guess.domain.source.Speaker;
import guess.domain.statistics.olap.OlapEntityMetrics;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * OLAP speaker metrics DTO.
 */
public class OlapSpeakerMetricsDto extends OlapEntityMetricsDto {
    private final long id;
    private final String name;
    private final String photoFileName;

    public OlapSpeakerMetricsDto(long id, String name, String photoFileName, List<Long> measureValues) {
        super(measureValues);

        this.id = id;
        this.name = name;
        this.photoFileName = photoFileName;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
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
                speakerMetrics.getMeasureValues());
    }

    public static List<OlapSpeakerMetricsDto> convertToDto(List<OlapEntityMetrics<Speaker>> speakerMetricsList, Language language) {
        List<Speaker> speakers = speakerMetricsList.stream()
                .map(OlapEntityMetrics::getEntity)
                .collect(Collectors.toList());
        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);

        return speakerMetricsList.stream()
                .map(sm -> convertToDto(sm, language, speakerDuplicates))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OlapSpeakerMetricsDto)) return false;
        if (!super.equals(o)) return false;
        OlapSpeakerMetricsDto that = (OlapSpeakerMetricsDto) o;
        return getId() == that.getId() && Objects.equals(getName(), that.getName()) && Objects.equals(getPhotoFileName(), that.getPhotoFileName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getName(), getPhotoFileName());
    }

    @Override
    public String toString() {
        return "OlapSpeakerMetricsDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", photoFileName='" + photoFileName + '\'' +
                '}';
    }
}
