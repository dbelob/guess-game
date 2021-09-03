package guess.dto.statistics.olap;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.domain.statistics.olap.OlapEntityMetrics;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * OLAP event type metrics DTO.
 */
public class OlapEventTypeMetricsDto extends OlapEntityMetricsDto {
    private final long id;
    private final String displayName;
    private final boolean conference;
    private final String logoFileName;
    private final String organizerName;

    public OlapEventTypeMetricsDto(long id, String displayName, boolean conference, String logoFileName,
                                   String organizerName, List<Long> measureValues) {
        super(measureValues);

        this.id = id;
        this.displayName = displayName;
        this.conference = conference;
        this.logoFileName = logoFileName;
        this.organizerName = organizerName;
    }

    public long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isConference() {
        return conference;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public static OlapEventTypeMetricsDto convertToDto(OlapEntityMetrics<EventType> eventTypeMetrics, Language language) {
        var eventType = eventTypeMetrics.getEntity();
        var displayName = LocalizationUtils.getString(eventType.getName(), language);
        String organizerName = (eventType.getOrganizer() != null) ? LocalizationUtils.getString(eventType.getOrganizer().getName(), language) : null;

        return new OlapEventTypeMetricsDto(
                eventType.getId(),
                displayName,
                eventType.isEventTypeConference(),
                eventType.getLogoFileName(),
                organizerName,
                eventTypeMetrics.getMeasureValues());
    }

    public static List<OlapEventTypeMetricsDto> convertToDto(List<OlapEntityMetrics<EventType>> eventTypeMetricsList, Language language) {
        return eventTypeMetricsList.stream()
                .map(etm -> convertToDto(etm, language))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OlapEventTypeMetricsDto)) return false;
        if (!super.equals(o)) return false;
        OlapEventTypeMetricsDto that = (OlapEventTypeMetricsDto) o;
        return getId() == that.getId() && isConference() == that.isConference() && Objects.equals(getDisplayName(), that.getDisplayName()) && Objects.equals(getLogoFileName(), that.getLogoFileName()) && Objects.equals(getOrganizerName(), that.getOrganizerName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getDisplayName(), isConference(), getLogoFileName(), getOrganizerName());
    }

    @Override
    public String toString() {
        return "OlapEventTypeMetricsDto{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", conference=" + conference +
                ", logoFileName='" + logoFileName + '\'' +
                ", organizerName='" + organizerName + '\'' +
                '}';
    }
}
