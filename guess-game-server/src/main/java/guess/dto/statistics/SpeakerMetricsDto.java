package guess.dto.statistics;

import guess.domain.Language;
import guess.domain.source.Speaker;
import guess.domain.statistics.SpeakerMetrics;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Speaker metrics DTO.
 */
public class SpeakerMetricsDto {
    private final String name;
    private final String fileName;
    private final long talksQuantity;
    private final long eventsQuantity;
    private final long eventTypesQuantity;

    public SpeakerMetricsDto(String name, String fileName, long talksQuantity, long eventsQuantity, long eventTypesQuantity) {
        this.name = name;
        this.fileName = fileName;
        this.talksQuantity = talksQuantity;
        this.eventsQuantity = eventsQuantity;
        this.eventTypesQuantity = eventTypesQuantity;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public long getTalksQuantity() {
        return talksQuantity;
    }

    public long getEventsQuantity() {
        return eventsQuantity;
    }

    public long getEventTypesQuantity() {
        return eventTypesQuantity;
    }

    public static SpeakerMetricsDto convertToDto(SpeakerMetrics speakerMetrics, Language language, Set<Speaker> speakerDuplicates) {
        Speaker speaker = speakerMetrics.getSpeaker();
        String name = LocalizationUtils.getSpeakerName(speaker, language, speakerDuplicates);

        return new SpeakerMetricsDto(
                name,
                speaker.getFileName(),
                speakerMetrics.getTalksQuantity(),
                speakerMetrics.getEventsQuantity(),
                speakerMetrics.getEventTypesQuantity());
    }

    public static List<SpeakerMetricsDto> convertToDto(List<SpeakerMetrics> speakerMetricsList, Language language) {
        List<Speaker> speakers = speakerMetricsList.stream()
                .map(SpeakerMetrics::getSpeaker)
                .collect(Collectors.toList());
        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers,
                language,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);

        return speakerMetricsList.stream()
                .map(sm -> convertToDto(sm, language, speakerDuplicates))
                .collect(Collectors.toList());
    }
}
