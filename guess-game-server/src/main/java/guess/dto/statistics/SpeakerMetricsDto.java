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
    private final boolean javaChampion;
    private final boolean mvp;
    private final boolean mvpReconnect;
    private final boolean anyMvp;
    private final long talksQuantity;
    private final long eventsQuantity;
    private final long eventTypesQuantity;
    private final long javaChampionsQuantity;
    private final long mvpsQuantity;

    public SpeakerMetricsDto(String name, String fileName, boolean javaChampion, boolean mvp, boolean mvpReconnect,
                             boolean anyMvp, long talksQuantity, long eventsQuantity, long eventTypesQuantity,
                             long javaChampionsQuantity, long mvpsQuantity) {
        this.name = name;
        this.fileName = fileName;
        this.javaChampion = javaChampion;
        this.mvp = mvp;
        this.anyMvp = anyMvp;
        this.mvpReconnect = mvpReconnect;
        this.talksQuantity = talksQuantity;
        this.eventsQuantity = eventsQuantity;
        this.eventTypesQuantity = eventTypesQuantity;
        this.javaChampionsQuantity = javaChampionsQuantity;
        this.mvpsQuantity = mvpsQuantity;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isJavaChampion() {
        return javaChampion;
    }

    public boolean isMvp() {
        return mvp;
    }

    public boolean isMvpReconnect() {
        return mvpReconnect;
    }

    public boolean isAnyMvp() {
        return anyMvp;
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

    public long getJavaChampionsQuantity() {
        return javaChampionsQuantity;
    }

    public long getMvpsQuantity() {
        return mvpsQuantity;
    }

    public static SpeakerMetricsDto convertToDto(SpeakerMetrics speakerMetrics, Language language, Set<Speaker> speakerDuplicates) {
        Speaker speaker = speakerMetrics.getSpeaker();
        String name = LocalizationUtils.getSpeakerName(speaker, language, speakerDuplicates);

        return new SpeakerMetricsDto(
                name,
                speaker.getFileName(),
                speaker.isJavaChampion(),
                speaker.isMvp(),
                speaker.isMvpReconnect(),
                speaker.isAnyMvp(),
                speakerMetrics.getTalksQuantity(),
                speakerMetrics.getEventsQuantity(),
                speakerMetrics.getEventTypesQuantity(),
                speakerMetrics.getJavaChampionsQuantity(),
                speakerMetrics.getMvpsQuantity());
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
