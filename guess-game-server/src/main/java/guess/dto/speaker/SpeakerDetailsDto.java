package guess.dto.speaker;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.dto.talk.TalkBriefDto;

import java.util.List;
import java.util.function.Function;

/**
 * Speaker details DTO.
 */
public record SpeakerDetailsDto(SpeakerDto speaker, List<TalkBriefDto> talks) {
    public static SpeakerDetailsDto convertToDto(Speaker speaker, List<Talk> talks, Function<Talk, Event> talkEventFunction,
                                                 Function<Event, EventType> eventEventTypeFunction, Language language) {
        return new SpeakerDetailsDto(
                SpeakerDto.convertToDto(speaker, language),
                TalkBriefDto.convertToBriefDto(talks, talkEventFunction, eventEventTypeFunction, language));
    }
}
