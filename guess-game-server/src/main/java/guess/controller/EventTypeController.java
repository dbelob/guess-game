package guess.controller;

import guess.domain.Language;
import guess.domain.source.EventType;
import guess.dto.eventtype.EventTypeBriefDto;
import guess.dto.eventtype.EventTypeSuperBriefDto;
import guess.service.EventTypeService;
import guess.service.LocaleService;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;

/**
 * Event type controller.
 */
@Controller
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
    @ResponseBody
    public List<EventTypeBriefDto> getEventTypes(@RequestParam boolean conferences, @RequestParam boolean meetups,
                                                 HttpSession httpSession) {
        Language language = localeService.getLanguage(httpSession);
        List<EventType> eventTypes = getEventTypesAndSort(conferences, meetups, language);

        return EventTypeBriefDto.convertToBriefDto(eventTypes, language);
    }

    @GetMapping("/filter-event-types")
    @ResponseBody
    public List<EventTypeSuperBriefDto> getFilterEventTypes(@RequestParam boolean conferences, @RequestParam boolean meetups,
                                                            HttpSession httpSession) {
        Language language = localeService.getLanguage(httpSession);
        List<EventType> eventTypes = getEventTypesAndSort(conferences, meetups, language);

        return EventTypeSuperBriefDto.convertToSuperBriefDto(eventTypes, language);
    }

    private List<EventType> getEventTypesAndSort(boolean isConferences, boolean isMeetups, Language language) {
        List<EventType> eventTypes = eventTypeService.getEventTypes(isConferences, isMeetups);
        Comparator<EventType> comparatorByIsConference = Comparator.comparing(EventType::isEventTypeConference).reversed();
        Comparator<EventType> comparatorByName = Comparator.comparing(et -> LocalizationUtils.getString(et.getName(), language), String.CASE_INSENSITIVE_ORDER);

        eventTypes.sort(comparatorByIsConference.thenComparing(comparatorByName));

        return eventTypes;
    }
}
