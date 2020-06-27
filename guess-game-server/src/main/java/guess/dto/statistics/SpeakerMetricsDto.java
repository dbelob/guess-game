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
    private final long id;
    private final String name;
    private final String photoFileName;
    private final boolean javaChampion;
    private final boolean mvp;
    private final boolean mvpReconnect;
    private final boolean anyMvp;
    private final long talksQuantity;
    private final long eventsQuantity;
    private final long eventTypesQuantity;
    private final long javaChampionsQuantity;
    private final long mvpsQuantity;

    public SpeakerMetricsDto(long id, String name, String photoFileName, boolean javaChampion, boolean mvp, boolean mvpReconnect,
                             boolean anyMvp, long talksQuantity, long eventsQuantity, long eventTypesQuantity,
                             long javaChampionsQuantity, long mvpsQuantity) {
        this.id = id;
        this.name = name;
        this.photoFileName = photoFileName;
        this.javaChampion = javaChampion;
        this.mvp = mvp;
        this.mvpReconnect = mvpReconnect;
        this.anyMvp = anyMvp;
        this.talksQuantity = talksQuantity;
        this.eventsQuantity = eventsQuantity;
        this.eventTypesQuantity = eventTypesQuantity;
        this.javaChampionsQuantity = javaChampionsQuantity;
        this.mvpsQuantity = mvpsQuantity;
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
        String name = LocalizationUtils.getSpeakerNameWithLastNameFirst(speaker, language, speakerDuplicates);

        return new SpeakerMetricsDto(
                speaker.getId(),
                name,
                speaker.getPhotoFileName(),
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
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);

        return speakerMetricsList.stream()
                .map(sm -> convertToDto(sm, language, speakerDuplicates))
                .collect(Collectors.toList());
    }
}
