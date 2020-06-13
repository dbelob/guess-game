package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.domain.statistics.EventTypeMetrics;
import guess.util.LocalizationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Event type metrics DTO.
 */
public class EventTypeMetricsDto {
    private final String displayName;
    private final String sortName;
    private final boolean conference;
    private final String logoFileName;
    private final LocalDate startDate;
    private final long age;
    private final long duration;
    private final long eventsQuantity;
    private final long talksQuantity;
    private final long speakersQuantity;
    private final long javaChampionsQuantity;
    private final long mvpsQuantity;

    public EventTypeMetricsDto(String displayName, String sortName, boolean conference, String logoFileName,
                               LocalDate startDate, long age, long duration, long eventsQuantity, long talksQuantity,
                               long speakersQuantity, long javaChampionsQuantity, long mvpsQuantity) {
        this.displayName = displayName;
        this.sortName = sortName;
        this.conference = conference;
        this.logoFileName = logoFileName;
        this.startDate = startDate;
        this.age = age;
        this.duration = duration;
        this.eventsQuantity = eventsQuantity;
        this.talksQuantity = talksQuantity;
        this.speakersQuantity = speakersQuantity;
        this.javaChampionsQuantity = javaChampionsQuantity;
        this.mvpsQuantity = mvpsQuantity;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public long getAge() {
        return age;
    }

    public long getDuration() {
        return duration;
    }

    public long getEventsQuantity() {
        return eventsQuantity;
    }

    public long getTalksQuantity() {
        return talksQuantity;
    }

    public long getSpeakersQuantity() {
        return speakersQuantity;
    }

    public long getJavaChampionsQuantity() {
        return javaChampionsQuantity;
    }

    public long getMvpsQuantity() {
        return mvpsQuantity;
    }

    public static EventTypeMetricsDto convertToDto(EventTypeMetrics eventTypeMetrics, Language language) {
        EventType eventType = eventTypeMetrics.getEventType();
        String displayName = LocalizationUtils.getString(eventType.getName(), language);
        String resourceKey = (eventType.isEventTypeConference()) ? LocalizationUtils.CONFERENCES_EVENT_TYPE_TEXT : LocalizationUtils.MEETUPS_EVENT_TYPE_TEXT;
        String sortName = String.format(LocalizationUtils.getResourceString(resourceKey, language), displayName);

        return new EventTypeMetricsDto(
                displayName,
                sortName,
                eventType.isEventTypeConference(),
                eventType.getLogoFileName(),
                eventTypeMetrics.getStartDate(),
                eventTypeMetrics.getAge(),
                eventTypeMetrics.getDuration(),
                eventTypeMetrics.getEventsQuantity(),
                eventTypeMetrics.getTalksQuantity(),
                eventTypeMetrics.getSpeakersQuantity(),
                eventTypeMetrics.getJavaChampionsQuantity(),
                eventTypeMetrics.getMvpsQuantity());
    }

    public static List<EventTypeMetricsDto> convertToDto(List<EventTypeMetrics> eventTypeMetricsList, Language language) {
        return eventTypeMetricsList.stream()
                .map(etm -> convertToDto(etm, language))
                .collect(Collectors.toList());
    }
}
