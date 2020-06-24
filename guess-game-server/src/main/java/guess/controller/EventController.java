package guess.controller;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.dto.event.EventBriefDto;
import guess.dto.event.EventDetailsDto;
import guess.dto.event.EventSuperBriefDto;
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
        Language language = localeService.getLanguage(httpSession);

        events.sort(Comparator.comparing(Event::getStartDate).reversed());

        return EventBriefDto.convertToBriefDto(events, language);
    }

    @GetMapping("/default-event")
    @ResponseBody
    public EventSuperBriefDto getDefaultEvent(HttpSession httpSession) {
        Event defaultEvent = eventService.getDefaultEvent();
        Language language = localeService.getLanguage(httpSession);

        return (defaultEvent != null) ? EventSuperBriefDto.convertToSuperBriefDto(defaultEvent, language) : null;
    }

    @GetMapping("/event/{id}")
    @ResponseBody
    public EventDetailsDto getEvent(@PathVariable long id, HttpSession httpSession) {
        Event event = eventService.getEventById(id);
        Language language = localeService.getLanguage(httpSession);
        List<Talk> talks = event.getTalks();
        List<Speaker> speakers = talks.stream()
                .flatMap(t -> t.getSpeakers().stream())
                .collect(Collectors.toList());

        // TODO: sort speakers and talks

        return EventDetailsDto.convertToDto(event, speakers, talks, eventService::getEventByTalk,
                eventTypeService::getEventTypeByEvent, language);
    }
}
