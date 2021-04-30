package guess.controller;

import guess.domain.source.Event;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.dto.company.CompanyDto;
import guess.dto.event.EventBriefDto;
import guess.dto.event.EventDetailsDto;
import guess.dto.event.EventHomeInfoDto;
import guess.dto.event.EventSuperBriefDto;
import guess.dto.speaker.SpeakerBriefDto;
import guess.dto.talk.TalkBriefDto;
import guess.service.EventService;
import guess.service.EventTypeService;
import guess.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Event controller.
 */
@Controller
@RequestMapping("/api/event")
public class EventController {
    private final EventService eventService;
    private final EventTypeService eventTypeService;
    private final LocaleService localeService;

    @Autowired
    public EventController(EventService eventService, EventTypeService eventTypeService, LocaleService localeService) {
        this.eventService = eventService;
        this.eventTypeService = eventTypeService;
        this.localeService = localeService;
    }

    @GetMapping("/events")
    @ResponseBody
    public List<EventBriefDto> getEvents(@RequestParam boolean conferences, @RequestParam boolean meetups,
                                         @RequestParam(required = false) Long eventTypeId, HttpSession httpSession) {
        List<Event> events = eventService.getEvents(conferences, meetups, eventTypeId);
        var language = localeService.getLanguage(httpSession);

        events.sort(Comparator.comparing(Event::getStartDate).reversed());

        return EventBriefDto.convertToBriefDto(events, language);
    }

    @GetMapping("/default-event")
    @ResponseBody
    public EventSuperBriefDto getDefaultEvent(HttpSession httpSession) {
        var defaultEvent = eventService.getDefaultEvent(true, true);
        var language = localeService.getLanguage(httpSession);

        return (defaultEvent != null) ? EventSuperBriefDto.convertToSuperBriefDto(defaultEvent, language) : null;
    }

    @GetMapping("/default-event-home-info")
    @ResponseBody
    public EventHomeInfoDto getDefaultEventHomeInfo(HttpSession httpSession) {
        var defaultEvent = eventService.getDefaultEvent(true, true);
        var language = localeService.getLanguage(httpSession);

        return (defaultEvent != null) ? EventHomeInfoDto.convertToDto(defaultEvent, language) : null;
    }

    @GetMapping("/default-conference")
    @ResponseBody
    public EventSuperBriefDto getDefaultConference(HttpSession httpSession) {
        var defaultEvent = eventService.getDefaultEvent(true, false);
        var language = localeService.getLanguage(httpSession);

        return (defaultEvent != null) ? EventSuperBriefDto.convertToSuperBriefDto(defaultEvent, language) : null;
    }

    @GetMapping("/event/{id}")
    @ResponseBody
    public EventDetailsDto getEvent(@PathVariable long id, HttpSession httpSession) {
        var event = eventService.getEventById(id);
        var language = localeService.getLanguage(httpSession);
        List<Talk> talks = event.getTalks();
        List<Speaker> speakers = talks.stream()
                .flatMap(t -> t.getSpeakers().stream())
                .distinct()
                .collect(Collectors.toList());
        var eventDetailsDto = EventDetailsDto.convertToDto(event, speakers, talks, eventService::getEventByTalk,
                eventTypeService::getEventTypeByEvent, language);

        Comparator<SpeakerBriefDto> comparatorByName = Comparator.comparing(SpeakerBriefDto::getDisplayName, String.CASE_INSENSITIVE_ORDER);
        Comparator<SpeakerBriefDto> comparatorByCompany = Comparator.comparing(
                s -> s.getCompanies().stream()
                        .map(CompanyDto::getName)
                        .collect(Collectors.joining(", ")), String.CASE_INSENSITIVE_ORDER);
        eventDetailsDto.getSpeakers().sort(comparatorByName.thenComparing(comparatorByCompany));

        Comparator<TalkBriefDto> comparatorByTalkDate = Comparator.nullsLast(
                Comparator.comparing(
                        TalkBriefDto::getTalkDay,
                        Comparator.nullsLast(Comparator.naturalOrder())));
        Comparator<TalkBriefDto> comparatorByTalkTime = Comparator.nullsLast(
                Comparator.comparing(
                        TalkBriefDto::getTalkTime,
                        Comparator.nullsLast(Comparator.naturalOrder())));
        Comparator<TalkBriefDto> comparatorByTrack = Comparator.nullsLast(
                Comparator.comparing(
                        TalkBriefDto::getTrack,
                        Comparator.nullsLast(Comparator.naturalOrder())));
        eventDetailsDto.getTalks().sort(comparatorByTalkDate.thenComparing(comparatorByTalkTime).thenComparing(comparatorByTrack));

        return eventDetailsDto;
    }
}
