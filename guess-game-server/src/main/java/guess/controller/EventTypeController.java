package guess.controller;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.dto.event.EventBriefDto;
import guess.dto.eventtype.EventTypeBriefDto;
import guess.dto.eventtype.EventTypeDetailsDto;
import guess.dto.eventtype.EventTypeSuperBriefDto;
import guess.service.EventTypeService;
import guess.service.LocaleService;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;

/**
 * Event type controller.
 */
@RestController
@RequestMapping("/api/event-type")
public class EventTypeController {
    private final EventTypeService eventTypeService;
    private final LocaleService localeService;

    @Autowired
    public EventTypeController(EventTypeService eventTypeService, LocaleService localeService) {
        this.eventTypeService = eventTypeService;
        this.localeService = localeService;
    }

    @GetMapping("/event-types")
    public List<EventTypeBriefDto> getEventTypes(@RequestParam boolean conferences, @RequestParam boolean meetups,
                                                 @RequestParam(required = false) Long organizerId, HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<EventType> eventTypes = getEventTypesAndSort(conferences, meetups, organizerId, language);

        return EventTypeBriefDto.convertToBriefDto(eventTypes, language);
    }

    @GetMapping("/filter-event-types")
    public List<EventTypeSuperBriefDto> getFilterEventTypes(@RequestParam boolean conferences, @RequestParam boolean meetups,
                                                            @RequestParam(required = false) Long organizerId, HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<EventType> eventTypes = getEventTypesAndSort(conferences, meetups, organizerId, language);

        return EventTypeSuperBriefDto.convertToSuperBriefDto(eventTypes, language);
    }

    List<EventType> getEventTypesAndSort(boolean isConferences, boolean isMeetups, Long organizerId, Language language) {
        List<EventType> eventTypes = eventTypeService.getEventTypes(isConferences, isMeetups, organizerId);
        Comparator<EventType> comparatorByIsConference = Comparator.comparing(EventType::isEventTypeConference).reversed();
        Comparator<EventType> comparatorByOrganizerName = Comparator.comparing(et -> LocalizationUtils.getString(et.getOrganizer().getName(), language), String.CASE_INSENSITIVE_ORDER);
        Comparator<EventType> comparatorByName = Comparator.comparing(et -> LocalizationUtils.getString(et.getName(), language), String.CASE_INSENSITIVE_ORDER);

        return eventTypes.stream()
                .sorted(comparatorByIsConference.thenComparing(comparatorByOrganizerName).thenComparing(comparatorByName))
                .toList();
    }

    @GetMapping("/event-type/{id}")
    public EventTypeDetailsDto getEventType(@PathVariable long id, HttpSession httpSession) {
        var eventType = eventTypeService.getEventTypeById(id);
        var language = localeService.getLanguage(httpSession);
        var eventTypeDetailsDto = EventTypeDetailsDto.convertToDto(eventType, eventType.getEvents(), language);

        List<EventBriefDto> sortedEvents = eventTypeDetailsDto.getEvents().stream()
                .sorted(Comparator.comparing(EventBriefDto::getStartDate).reversed())
                .toList();

        return new EventTypeDetailsDto(eventTypeDetailsDto.getEventType(), sortedEvents);
    }
}
