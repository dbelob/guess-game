package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.domain.statistics.AbstractEventTypeMetrics;
import guess.domain.statistics.EventTypeMetrics;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Event type metrics DTO.
 */
public class EventTypeMetricsDto extends AbstractEventTypeMetrics {
    private final long id;
    private final String displayName;
    private final String sortName;
    private final boolean conference;
    private final String logoFileName;

    public EventTypeMetricsDto(long id, String displayName, String sortName, boolean conference, String logoFileName,
                               AbstractEventTypeMetrics eventTypeMetrics) {
        super(eventTypeMetrics.getStartDate(), eventTypeMetrics.getAge(), eventTypeMetrics.getDuration(),
                eventTypeMetrics.getEventsQuantity(), eventTypeMetrics.getTalksQuantity(),
                eventTypeMetrics.getSpeakersQuantity(), eventTypeMetrics.getJavaChampionsQuantity(),
                eventTypeMetrics.getMvpsQuantity());

        this.id = id;
        this.displayName = displayName;
        this.sortName = sortName;
        this.conference = conference;
        this.logoFileName = logoFileName;
    }

    public long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSortName() {
        return sortName;
    }

    public boolean isConference() {
        return conference;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public static EventTypeMetricsDto convertToDto(EventTypeMetrics eventTypeMetrics, Language language) {
        EventType eventType = eventTypeMetrics.getEventType();
        String displayName = LocalizationUtils.getString(eventType.getName(), language);
        String resourceKey = (eventType.isEventTypeConference()) ? LocalizationUtils.CONFERENCES_EVENT_TYPE_TEXT : LocalizationUtils.MEETUPS_EVENT_TYPE_TEXT;
        String sortName = String.format(LocalizationUtils.getResourceString(resourceKey, language), displayName);

        return new EventTypeMetricsDto(
                eventType.getId(),
                displayName,
                sortName,
                eventType.isEventTypeConference(),
                eventType.getLogoFileName(),
                eventTypeMetrics);
    }

    public static List<EventTypeMetricsDto> convertToDto(List<EventTypeMetrics> eventTypeMetricsList, Language language) {
        return eventTypeMetricsList.stream()
                .map(etm -> convertToDto(etm, language))
                .collect(Collectors.toList());
    }
}
