package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.dto.speaker.SpeakerBriefDto;
import guess.dto.talk.TalkBriefDto;

import java.util.List;
import java.util.function.Function;

/**
 * Event details DTO.
 */
public record EventDetailsDto(EventDto event, List<SpeakerBriefDto> speakers, List<TalkBriefDto> talks) {
    public static EventDetailsDto convertToDto(Event event, List<Speaker> speakers, List<Talk> talks,
                                               Function<Talk, Event> talkEventFunction,
                                               Function<Event, EventType> eventEventTypeFunction, Language language) {
        return new EventDetailsDto(
                EventDto.convertToDto(event, eventEventTypeFunction, language),
                SpeakerBriefDto.convertToBriefDto(speakers, language),
                TalkBriefDto.convertToBriefDto(talks, talkEventFunction, eventEventTypeFunction, language)
        );
    }
}
