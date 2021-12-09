package guess.dto.talk;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Talk;
import guess.dto.speaker.SpeakerBriefDto;

import java.util.List;
import java.util.function.Function;

/**
 * Talk details DTO.
 */
public record TalkDetailsDto(TalkDto talk, List<SpeakerBriefDto> speakers) {
    public static TalkDetailsDto convertToDto(Talk talk, Function<Talk, Event> talkEventFunction,
                                              Function<Event, EventType> eventEventTypeFunction, Language language) {
        return new TalkDetailsDto(
                TalkDto.convertToDto(talk, talkEventFunction, eventEventTypeFunction, language),
                SpeakerBriefDto.convertToBriefDto(talk.getSpeakers(), language));
    }
}
