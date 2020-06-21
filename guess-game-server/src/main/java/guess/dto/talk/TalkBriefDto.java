package guess.dto.talk;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Talk;
import guess.dto.speaker.SpeakerSuperBriefDto;
import guess.util.LocalizationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Talk DTO (brief).
 */
public class TalkBriefDto {
    private final long id;
    private final String name;
    private final LocalDate talkDate;
    private final Long eventId;
    private final String eventName;
    private final String eventTypeLogoFileName;
    private final List<SpeakerSuperBriefDto> speakers;

    public TalkBriefDto(long id, String name, LocalDate talkDate, Long eventId, String eventName,
                        String eventTypeLogoFileName, List<SpeakerSuperBriefDto> speakers) {
        this.id = id;
        this.name = name;
        this.talkDate = talkDate;
        this.eventId = eventId;
        this.eventName = eventName;
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

    public Long getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventTypeLogoFileName() {
        return eventTypeLogoFileName;
    }

    public List<SpeakerSuperBriefDto> getSpeakers() {
        return speakers;
    }

    public static TalkBriefDto convertToDto(Talk talk, Event event, EventType eventType, Language language) {
        LocalDate eventStartDate = (event != null) ? event.getStartDate() : null;
        Long talkDay = talk.getTalkDay();
        LocalDate talkDate = (eventStartDate != null) ?
                ((talkDay != null) ? eventStartDate.plusDays(talkDay - 1) : eventStartDate) :
                null;
        Long eventId = (event != null) ? event.getId() : null;
        String eventName = (event != null) ? LocalizationUtils.getString(event.getName(), language) : null;
        String eventTypeLogoFileName = (eventType != null) ? eventType.getLogoFileName() : null;
        List<SpeakerSuperBriefDto> speakers = SpeakerSuperBriefDto.convertToSuperBriefDto(talk.getSpeakers(), language);

        return new TalkBriefDto(
                talk.getId(),
                LocalizationUtils.getString(talk.getName(), language),
                talkDate,
                eventId,
                eventName,
                eventTypeLogoFileName,
                speakers);
    }

    public static List<TalkBriefDto> convertToDto(List<Talk> talks, Function<Talk, Event> talkEventFunction,
                                                  Function<Event, EventType> eventEventTypeFunction,
                                                  Language language) {
        return talks.stream()
                .map(t -> {
                    Event event = talkEventFunction.apply(t);
                    EventType eventType = eventEventTypeFunction.apply(event);

                    return convertToDto(t, event, eventType, language);
                })
                .collect(Collectors.toList());
    }
}
