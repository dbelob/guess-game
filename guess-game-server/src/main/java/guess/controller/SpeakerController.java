package guess.controller;

import guess.domain.Language;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.dto.common.SelectedEntitiesDto;
import guess.dto.company.CompanyDto;
import guess.dto.speaker.SpeakerBriefDto;
import guess.dto.speaker.SpeakerDetailsDto;
import guess.dto.speaker.SpeakerSuperBriefDto;
import guess.dto.talk.TalkBriefDto;
import guess.service.*;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Speaker controller.
 */
@RestController
@RequestMapping("/api/speaker")
public class SpeakerController {
    private final SpeakerService speakerService;
    private final TalkService talkService;
    private final EventService eventService;
    private final EventTypeService eventTypeService;
    private final LocaleService localeService;

    @Autowired
    public SpeakerController(SpeakerService speakerService, TalkService talkService, EventService eventService,
                             EventTypeService eventTypeService, LocaleService localeService) {
        this.speakerService = speakerService;
        this.talkService = talkService;
        this.eventService = eventService;
        this.eventTypeService = eventTypeService;
        this.localeService = localeService;
    }

    @GetMapping("/first-letter-speakers")
    public List<SpeakerBriefDto> getSpeakersByFirstLetter(@RequestParam String firstLetter, HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<Speaker> speakers = speakerService.getSpeakersByFirstLetter(firstLetter, language);

        return convertToBriefDtoAndSort(speakers, s -> SpeakerBriefDto.convertToBriefDto(s, language));
    }

    @GetMapping("/first-letters-speakers")
    public List<SpeakerSuperBriefDto> getSpeakersByFirstLetters(@RequestParam String firstLetters, HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<Speaker> speakers = speakerService.getSpeakersByFirstLetters(firstLetters, language);

        return createDuplicatesAndConvertToDtoAndSort(speakers, language);
    }

    @PostMapping("/selected-speakers")
    public List<SpeakerSuperBriefDto> getSelectedSpeakers(@RequestBody SelectedEntitiesDto selectedEntities, HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<Speaker> speakers = speakerService.getSpeakerByIds(selectedEntities.getIds());

        return createDuplicatesAndConvertToDtoAndSort(speakers, language);
    }

    @GetMapping("/speakers")
    public List<SpeakerBriefDto> getSpeakers(@RequestParam(required = false) String name, @RequestParam(required = false) String company,
                                             @RequestParam(required = false) String twitter, @RequestParam(required = false) String gitHub,
                                             @RequestParam boolean javaChampion, @RequestParam boolean mvp,
                                             HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<Speaker> speakers = speakerService.getSpeakers(name, company, twitter, gitHub, javaChampion, mvp);

        return convertToBriefDtoAndSort(speakers, s -> SpeakerBriefDto.convertToBriefDto(s, language));
    }

    @GetMapping("/speaker/{id}")
    public SpeakerDetailsDto getSpeaker(@PathVariable long id, HttpSession httpSession) {
        var speaker = speakerService.getSpeakerById(id);
        List<Talk> talks = talkService.getTalksBySpeaker(speaker);
        var language = localeService.getLanguage(httpSession);
        var speakerDetailsDto = SpeakerDetailsDto.convertToDto(speaker, talks, eventService::getEventByTalk,
                eventTypeService::getEventTypeByEvent, language);

        List<TalkBriefDto> sortedTalks = speakerDetailsDto.getTalks().stream()
                .sorted(Comparator.comparing(TalkBriefDto::getTalkDate).reversed())
                .toList();

        return new SpeakerDetailsDto(speakerDetailsDto.getSpeaker(), sortedTalks);
    }

    List<SpeakerBriefDto> convertToBriefDtoAndSort(List<Speaker> speakers, Function<List<Speaker>, List<SpeakerBriefDto>> speakerFunction) {
        List<SpeakerBriefDto> speakerBriefDtoList = speakerFunction.apply(speakers);

        Comparator<SpeakerBriefDto> comparatorByName = Comparator.comparing(SpeakerBriefDto::getDisplayName, String.CASE_INSENSITIVE_ORDER);
        Comparator<SpeakerBriefDto> comparatorByCompany = Comparator.comparing(
                s -> s.getCompanies().stream()
                        .map(CompanyDto::getName)
                        .collect(Collectors.joining(", ")), String.CASE_INSENSITIVE_ORDER);

        speakerBriefDtoList.sort(comparatorByName.thenComparing(comparatorByCompany));

        return speakerBriefDtoList;
    }

    List<SpeakerSuperBriefDto> createDuplicatesAndConvertToDtoAndSort(List<Speaker> speakers, Language language) {
        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);
        List<SpeakerBriefDto> speakerBriefDtoList = convertToBriefDtoAndSort(
                speakers,
                s -> SpeakerBriefDto.convertToBriefDto(s, language, speakerDuplicates));

        return speakerBriefDtoList.stream()
                .map(s -> new SpeakerSuperBriefDto(s.getId(), s.getDisplayName()))
                .toList();
    }
}
