package guess.dto.talk;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Talk;
import guess.dto.event.EventSuperBriefDto;
import guess.dto.speaker.SpeakerSuperBriefDto;
import guess.util.LocalizationUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Talk DTO (brief).
 */
public class TalkBriefDto {
    private final long id;
    private final String name;
    private final LocalDate talkDate;
    private final Long talkDay;
    private final LocalDateTime talkTime;
    private final Long track;

    private final EventSuperBriefDto event;
    private final String eventTypeLogoFileName;
    private final List<SpeakerSuperBriefDto> speakers;

    public TalkBriefDto(long id, String name, LocalDate talkDate, Long talkDay, LocalDateTime talkTime, Long track,
                        EventSuperBriefDto event, String eventTypeLogoFileName, List<SpeakerSuperBriefDto> speakers) {
        this.id = id;
        this.name = name;
        this.talkDate = talkDate;
        this.talkDay = talkDay;
        this.talkTime = talkTime;
        this.track = track;
        this.event = event;
        this.eventTypeLogoFileName = eventTypeLogoFileName;
        this.speakers = speakers;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getTalkDate() {
        return talkDate;
    }

    public Long getTalkDay() {
        return talkDay;
    }

    public LocalDateTime getTalkTime() {
        return talkTime;
    }

    public Long getTrack() {
        return track;
    }

    public EventSuperBriefDto getEvent() {
        return event;
    }

    public String getEventTypeLogoFileName() {
        return eventTypeLogoFileName;
    }

    public List<SpeakerSuperBriefDto> getSpeakers() {
        return speakers;
    }

    public static TalkBriefDto convertToBriefDto(Talk talk, Function<Talk, Event> talkEventFunction,
                                                 Function<Event, EventType> eventEventTypeFunction, Language language) {
        Event event = talkEventFunction.apply(talk);
        EventType eventType = eventEventTypeFunction.apply(event);

        LocalDate eventStartDate = (event != null) ? event.getStartDate() : null;
        Long talkDay = talk.getTalkDay();
        long safeTalkDay = Optional.ofNullable(talkDay).orElse(1L);

        LocalDate talkDate;
        if (eventStartDate != null) {
            talkDate = (talkDay != null) ? eventStartDate.plusDays(safeTalkDay - 1) : eventStartDate;
        } else {
            talkDate = null;
        }

        LocalDate safeLocalDate = Optional.ofNullable(talkDate).orElse(LocalDate.now());
        LocalDateTime talkTime = (talk.getTrackTime() != null) ? LocalDateTime.of(safeLocalDate, talk.getTrackTime()) : null;

        EventSuperBriefDto eventSuperBriefDto = (event != null) ? EventSuperBriefDto.convertToSuperBriefDto(event, language) : null;
        String eventTypeLogoFileName = (eventType != null) ? eventType.getLogoFileName() : null;
        List<SpeakerSuperBriefDto> speakers = SpeakerSuperBriefDto.convertToSuperBriefDto(talk.getSpeakers(), language);

        return new TalkBriefDto(
                talk.getId(),
                LocalizationUtils.getString(talk.getName(), language),
                talkDate,
                talk.getTalkDay(),
                talkTime,
                talk.getTrack(),
                eventSuperBriefDto,
                eventTypeLogoFileName,
                speakers);
    }

    public static List<TalkBriefDto> convertToBriefDto(List<Talk> talks, Function<Talk, Event> talkEventFunction,
                                                       Function<Event, EventType> eventEventTypeFunction,
                                                       Language language) {
        return talks.stream()
                .map(t -> convertToBriefDto(t, talkEventFunction, eventEventTypeFunction, language))
                .collect(Collectors.toList());
    }
}
