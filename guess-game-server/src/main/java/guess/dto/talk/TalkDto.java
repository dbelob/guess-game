package guess.dto.talk;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Talk;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.function.Function;

/**
 * Talk DTO.
 */
public class TalkDto extends TalkBriefDto {
    private final String description;
    private final String language;
    private final List<String> presentationLinks;
    private final List<String> materialLinks;
    private final List<String> videoLinks;

    public TalkDto(TalkBriefDto talkBriefDto, String description, String language, List<String> presentationLinks,
                   List<String> materialLinks, List<String> videoLinks) {
        super(talkBriefDto.getId(), talkBriefDto.getName(), talkBriefDto.getTalkDate(), talkBriefDto.getTalkDay(),
                talkBriefDto.getTalkTime(), talkBriefDto.getTrack(),
                new TalkBriefDtoDetails(
                        talkBriefDto.getEvent(),
                        talkBriefDto.getEventTypeLogoFileName(),
                        talkBriefDto.getSpeakers()));

        this.description = description;
        this.language = language;
        this.presentationLinks = presentationLinks;
        this.materialLinks = materialLinks;
        this.videoLinks = videoLinks;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getPresentationLinks() {
        return presentationLinks;
    }

    public List<String> getMaterialLinks() {
        return materialLinks;
    }

    public List<String> getVideoLinks() {
        return videoLinks;
    }

    public static TalkDto convertToDto(Talk talk, Function<Talk, Event> talkEventFunction,
                                       Function<Event, EventType> eventEventTypeFunction, Language language) {
        var description = LocalizationUtils.getString(talk.getLongDescription(), language);

        if ((description == null) || description.isEmpty()) {
            description = LocalizationUtils.getString(talk.getShortDescription(), language);
        }

        return new TalkDto(
                convertToBriefDto(talk, talkEventFunction, eventEventTypeFunction, language),
                description,
                talk.getLanguage(),
                talk.getPresentationLinks(),
                talk.getMaterialLinks(),
                talk.getVideoLinks());
    }

    public static List<TalkDto> convertToDto(List<Talk> talks, Function<Talk, Event> talkEventFunction,
                                             Function<Event, EventType> eventEventTypeFunction, Language language) {
        return talks.stream()
                .map(t -> convertToDto(t, talkEventFunction, eventEventTypeFunction, language))
                .toList();
    }
}
