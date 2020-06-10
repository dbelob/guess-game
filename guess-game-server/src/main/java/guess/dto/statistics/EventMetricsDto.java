package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.statistics.EventMetrics;
import guess.util.LocalizationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Event metrics DTO.
 */
public class EventMetricsDto {
    private final String name;
    private final String logoFileName;
    private final LocalDate startDate;
    private final long duration;
    private final long talksQuantity;
    private final long speakersQuantity;

    public EventMetricsDto(String name, String logoFileName, LocalDate startDate, long duration, long talksQuantity,
                           long speakersQuantity) {
        this.name = name;
        this.logoFileName = logoFileName;
        this.startDate = startDate;
        this.duration = duration;
        this.talksQuantity = talksQuantity;
        this.speakersQuantity = speakersQuantity;
    }

    public String getName() {
        return name;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public long getDuration() {
        return duration;
    }

    public long getTalksQuantity() {
        return talksQuantity;
    }

    public long getSpeakersQuantity() {
        return speakersQuantity;
    }

    public static EventMetricsDto convertToDto(EventMetrics eventMetrics, Language language) {
        Event event = eventMetrics.getEvent();

        return new EventMetricsDto(
                LocalizationUtils.getString(event.getName(), language),
                event.getEventType().getLogoFileName(),
                eventMetrics.getStartDate(),
                eventMetrics.getDuration(),
                eventMetrics.getTalksQuantity(),
                eventMetrics.getSpeakersQuantity());
    }

    public static List<EventMetricsDto> convertToDto(List<EventMetrics> eventMetricsList, Language language) {
        return eventMetricsList.stream()
                .map(etm -> convertToDto(etm, language))
                .collect(Collectors.toList());
    }
}
