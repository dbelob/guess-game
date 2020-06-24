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
    private final long id;
    private final String name;
    private final String logoFileName;
    private final LocalDate startDate;
    private final long duration;
    private final long talksQuantity;
    private final long speakersQuantity;
    private final long javaChampionsQuantity;
    private final long mvpsQuantity;

    public EventMetricsDto(long id, String name, String logoFileName, LocalDate startDate, long duration, long talksQuantity,
                           long speakersQuantity, long javaChampionsQuantity, long mvpsQuantity) {
        this.id = id;
        this.name = name;
        this.logoFileName = logoFileName;
        this.startDate = startDate;
        this.duration = duration;
        this.talksQuantity = talksQuantity;
        this.speakersQuantity = speakersQuantity;
        this.javaChampionsQuantity = javaChampionsQuantity;
        this.mvpsQuantity = mvpsQuantity;
    }

    public long getId() {
        return id;
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

    public long getJavaChampionsQuantity() {
        return javaChampionsQuantity;
    }

    public long getMvpsQuantity() {
        return mvpsQuantity;
    }

    public static EventMetricsDto convertToDto(EventMetrics eventMetrics, Language language) {
        Event event = eventMetrics.getEvent();

        return new EventMetricsDto(
                event.getId(),
                LocalizationUtils.getString(event.getName(), language),
                (event.getEventType() != null) ? event.getEventType().getLogoFileName() : null,
                eventMetrics.getStartDate(),
                eventMetrics.getDuration(),
                eventMetrics.getTalksQuantity(),
                eventMetrics.getSpeakersQuantity(),
                eventMetrics.getJavaChampionsQuantity(),
                eventMetrics.getMvpsQuantity());
    }

    public static List<EventMetricsDto> convertToDto(List<EventMetrics> eventMetricsList, Language language) {
        return eventMetricsList.stream()
                .map(etm -> convertToDto(etm, language))
                .collect(Collectors.toList());
    }
}
