package guess.controller;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.dto.start.EventBriefDto;
import guess.service.EventService;
import guess.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Event controller.
 */
@Controller
@RequestMapping("/api/event")
public class EventController {
    private final EventService eventService;
    private final LocaleService localeService;

    @Autowired
    public EventController(EventService eventService, LocaleService localeService) {
        this.eventService = eventService;
        this.localeService = localeService;
    }

    @GetMapping("/default-event")
    @ResponseBody
    public EventBriefDto getDefaultEvent(HttpSession httpSession) {
        Event defaultEvent = eventService.getDefaultEvent();
        Language language = localeService.getLanguage(httpSession);

        return (defaultEvent != null) ? EventBriefDto.convertToBriefDto(defaultEvent, language) : null;
    }
}
