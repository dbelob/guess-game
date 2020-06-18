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
public class SpeakerDetailsDto {
    private final SpeakerDto speaker;
    private final List<TalkBriefDto> talks;

    public SpeakerDetailsDto(SpeakerDto speaker, List<TalkBriefDto> talks) {
        this.speaker = speaker;
        this.talks = talks;
    }

    public SpeakerDto getSpeaker() {
        return speaker;
    }

    public List<TalkBriefDto> getTalks() {
        return talks;
    }

    public static SpeakerDetailsDto convertToDto(Speaker speaker, List<Talk> talks, Function<Talk, Event> talkEventFunction,
                                                 Function<Event, EventType> eventEventTypeFunction, Language language) {
        return new SpeakerDetailsDto(
                SpeakerDto.convertToDto(speaker, language),
                TalkBriefDto.convertToDto(talks, talkEventFunction, eventEventTypeFunction, language));
    }
}
