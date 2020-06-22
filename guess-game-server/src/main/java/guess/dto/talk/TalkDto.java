package guess.dto.talk;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Talk;
import guess.util.LocalizationUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Talk DTO.
 */
public class TalkDto extends TalkBriefDto {
    private final String description;
    private final Long talkDay;
    private final LocalTime trackTime;
    private final Long track;
    private final String language;
    private final List<String> presentationLinks;
    private final List<String> videoLinks;

    public TalkDto(TalkBriefDto talkBriefDto, String description, Long talkDay, LocalTime trackTime, Long track,
                   String language, List<String> presentationLinks, List<String> videoLinks) {
        super(talkBriefDto.getId(), talkBriefDto.getName(), talkBriefDto.getTalkDate(), talkBriefDto.getEventId(),
                talkBriefDto.getEvent(), talkBriefDto.getEventTypeLogoFileName(), talkBriefDto.getSpeakers());

        this.description = description;
        this.talkDay = talkDay;
        this.trackTime = trackTime;
        this.track = track;
        this.language = language;
        this.presentationLinks = presentationLinks;
        this.videoLinks = videoLinks;
    }

    public String getDescription() {
        return description;
    }

    public Long getTalkDay() {
        return talkDay;
    }

    public LocalTime getTrackTime() {
        return trackTime;
    }

    public Long getTrack() {
        return track;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getPresentationLinks() {
        return presentationLinks;
    }

    public List<String> getVideoLinks() {
        return videoLinks;
    }

    public static TalkDto convertToDto(Talk talk, Function<Talk, Event> talkEventFunction,
                                       Function<Event, EventType> eventEventTypeFunction, Language language) {
        String description = LocalizationUtils.getString(talk.getLongDescription(), language);

        if ((description == null) || description.isEmpty()) {
            description = LocalizationUtils.getString(talk.getShortDescription(), language);
        }

        return new TalkDto(
                convertToBriefDto(talk, talkEventFunction, eventEventTypeFunction, language),
                description,
                talk.getTalkDay(),
                talk.getTrackTime(),
                talk.getTrack(),
                talk.getLanguage(),
                talk.getPresentationLinks(),
                talk.getVideoLinks());
    }

    public static List<TalkDto> convertToDto(List<Talk> talks, Function<Talk, Event> talkEventFunction,
                                             Function<Event, EventType> eventEventTypeFunction, Language language) {
        return talks.stream()
                .map(t -> convertToDto(t, talkEventFunction, eventEventTypeFunction, language))
                .collect(Collectors.toList());
    }
}
