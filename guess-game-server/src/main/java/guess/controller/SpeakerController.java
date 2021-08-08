package guess.controller;

import guess.domain.Language;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.dto.company.CompanyDto;
import guess.dto.speaker.SpeakerBriefDto;
import guess.dto.speaker.SpeakerDetailsDto;
import guess.dto.talk.TalkBriefDto;
import guess.service.*;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Speaker controller.
 */
@Controller
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
    @ResponseBody
    public List<SpeakerBriefDto> getSpeakersByFirstLetter(@RequestParam String firstLetter, HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<Speaker> speakers = speakerService.getSpeakersByFirstLetter(firstLetter, language);

        return convertToBriefDtoAndSort(speakers, language);
    }

    @GetMapping("/first-letters-speakers")
    @ResponseBody
    public List<SpeakerBriefDto> getSpeakersByFirstLetters(@RequestParam String firstLetters, HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<Speaker> speakers = speakerService.getSpeakersByFirstLetters(firstLetters, language);
        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);
        List<SpeakerBriefDto> speakerBriefDtoList = SpeakerBriefDto.convertToBriefDto(speakers, language, speakerDuplicates);

        Comparator<SpeakerBriefDto> comparatorByName = Comparator.comparing(SpeakerBriefDto::getDisplayName, String.CASE_INSENSITIVE_ORDER);
        Comparator<SpeakerBriefDto> comparatorByCompany = Comparator.comparing(
                s -> s.getCompanies().stream()
                        .map(CompanyDto::getName)
                        .collect(Collectors.joining(", ")), String.CASE_INSENSITIVE_ORDER);

        speakerBriefDtoList.sort(comparatorByName.thenComparing(comparatorByCompany));

        return speakerBriefDtoList;
    }

    @GetMapping("/speakers")
    @ResponseBody
    public List<SpeakerBriefDto> getSpeakers(@RequestParam(required = false) String name, @RequestParam(required = false) String company,
                                             @RequestParam(required = false) String twitter, @RequestParam(required = false) String gitHub,
                                             @RequestParam boolean javaChampion, @RequestParam boolean mvp,
                                             HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<Speaker> speakers = speakerService.getSpeakers(name, company, twitter, gitHub, javaChampion, mvp);

        return convertToBriefDtoAndSort(speakers, language);
    }

    List<SpeakerBriefDto> convertToBriefDtoAndSort(List<Speaker> speakers, Language language) {
        List<SpeakerBriefDto> speakerBriefDtoList = SpeakerBriefDto.convertToBriefDto(speakers, language, Collections.emptySet());

        Comparator<SpeakerBriefDto> comparatorByName = Comparator.comparing(SpeakerBriefDto::getDisplayName, String.CASE_INSENSITIVE_ORDER);
        Comparator<SpeakerBriefDto> comparatorByCompany = Comparator.comparing(
                s -> s.getCompanies().stream()
                        .map(CompanyDto::getName)
                        .collect(Collectors.joining(", ")), String.CASE_INSENSITIVE_ORDER);

        speakerBriefDtoList.sort(comparatorByName.thenComparing(comparatorByCompany));

        return speakerBriefDtoList;
    }

    @GetMapping("/speaker/{id}")
    @ResponseBody
    public SpeakerDetailsDto getSpeaker(@PathVariable long id, HttpSession httpSession) {
        var speaker = speakerService.getSpeakerById(id);
        List<Talk> talks = talkService.getTalksBySpeaker(speaker);
        var language = localeService.getLanguage(httpSession);
        var speakerDetailsDto = SpeakerDetailsDto.convertToDto(speaker, talks, eventService::getEventByTalk,
                eventTypeService::getEventTypeByEvent, language);

        speakerDetailsDto.getTalks().sort(Comparator.comparing(TalkBriefDto::getTalkDate).reversed());

        return speakerDetailsDto;
    }
}
