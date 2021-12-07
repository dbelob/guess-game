package guess.controller;

import guess.domain.source.Organizer;
import guess.dto.organizer.OrganizerDto;
import guess.service.EventService;
import guess.service.LocaleService;
import guess.service.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;

/**
 * Organizer controller.
 */
@RestController
@RequestMapping("/api/organizer")
public class OrganizerController {
    private final OrganizerService organizerService;
    private final EventService eventService;
    private final LocaleService localeService;

    @Autowired
    public OrganizerController(OrganizerService organizerService, EventService eventService, LocaleService localeService) {
        this.organizerService = organizerService;
        this.eventService = eventService;
        this.localeService = localeService;
    }

    @GetMapping("/organizers")
    public List<OrganizerDto> getOrganizers(HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<Organizer> organizers = organizerService.getOrganizers();
        List<OrganizerDto> organizerDtoList = OrganizerDto.convertToDto(organizers, language);

        return organizerDtoList.stream()
                .sorted(Comparator.comparing(OrganizerDto::getName))
                .toList();
    }

    @GetMapping("/default-event-organizer")
    public OrganizerDto getDefaultEventOrganizer(HttpSession httpSession) {
        var defaultEvent = eventService.getDefaultEvent(true, true);

        if (defaultEvent != null) {
            var language = localeService.getLanguage(httpSession);

            return OrganizerDto.convertToDto(defaultEvent.getEventType().getOrganizer(), language);
        } else {
            return null;
        }
    }
}
