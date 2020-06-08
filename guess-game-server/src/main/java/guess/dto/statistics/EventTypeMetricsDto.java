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
    private final String name;
    private final LocalDate startDate;
    private final long age;
    private final long duration;
    private final long eventsQuantity;
    private final long talksQuantity;
    private final long speakersQuantity;

    public EventTypeMetricsDto(String name, LocalDate startDate, long age, long duration, long eventsQuantity, long talksQuantity, long speakersQuantity) {
        this.name = name;
        this.startDate = startDate;
        this.age = age;
        this.duration = duration;
        this.eventsQuantity = eventsQuantity;
        this.talksQuantity = talksQuantity;
        this.speakersQuantity = speakersQuantity;
    }

    public String getName() {
        return name;
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

    public static EventTypeMetricsDto convertToDto(EventTypeMetrics eventTypeMetrics, Language language) {
        EventType eventType = eventTypeMetrics.getEventType();
        String name = LocalizationUtils.getString(eventType.getName(), language);
        String resourceKey = (eventType.isEventTypeConference()) ? LocalizationUtils.CONFERENCES_EVENT_TYPE_TEXT : LocalizationUtils.MEETUPS_EVENT_TYPE_TEXT;
        String displayName = String.format(LocalizationUtils.getResourceString(resourceKey, language), name);

        return new EventTypeMetricsDto(
                displayName,
                eventTypeMetrics.getStartDate(),
                eventTypeMetrics.getAge(),
                eventTypeMetrics.getDuration(),
                eventTypeMetrics.getEventsQuantity(),
                eventTypeMetrics.getTalksQuantity(),
                eventTypeMetrics.getSpeakersQuantity());
    }

    public static List<EventTypeMetricsDto> convertToDto(List<EventTypeMetrics> eventTypeMetricsList, Language language) {
        return eventTypeMetricsList.stream()
                .map(etm -> convertToDto(etm, language))
                .collect(Collectors.toList());
    }
}
