package guess.dto.talk;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Talk;
import guess.dto.speaker.SpeakerSuperBriefDto;

import java.util.List;
import java.util.function.Function;

/**
 * Talk details DTO.
 */
public class TalkDetailsDto {
    private final TalkDto talk;
    private final List<SpeakerSuperBriefDto> speakers;

    public TalkDetailsDto(TalkDto talk, List<SpeakerSuperBriefDto> speakers) {
        this.talk = talk;
        this.speakers = speakers;
    }

    public TalkDto getTalk() {
        return talk;
    }

    public List<SpeakerSuperBriefDto> getSpeakers() {
        return speakers;
    }

    public static TalkDetailsDto convertToDto(Talk talk, Function<Talk, Event> talkEventFunction,
                                              Function<Event, EventType> eventEventTypeFunction, Language language) {
        return new TalkDetailsDto(
                TalkDto.convertToDto(talk, talkEventFunction, eventEventTypeFunction, language),
                SpeakerSuperBriefDto.convertToSuperBriefDto(talk.getSpeakers(), language));
    }
}
