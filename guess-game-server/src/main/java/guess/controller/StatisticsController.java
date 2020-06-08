package guess.controller;

import guess.domain.Language;
import guess.domain.statistics.EventTypeMetrics;
import guess.dto.statistics.EventTypeMetricsDto;
import guess.service.LocaleService;
import guess.service.StatisticsService;
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
 * Statistics controller.
 */
@Controller
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final LocaleService localeService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService, LocaleService localeService) {
        this.statisticsService = statisticsService;
        this.localeService = localeService;
    }

    @GetMapping("/event-types")
    @ResponseBody
    public List<EventTypeMetricsDto> getEventTypeMetrics(@RequestParam boolean conferences, @RequestParam boolean meetups,
                                                         HttpSession httpSession) {
        List<EventTypeMetrics> eventTypeMetrics = statisticsService.getEventTypeMetrics(conferences, meetups);
        Language language = localeService.getLanguage(httpSession);
        Comparator<EventTypeMetrics> comparatorByIsConference = Comparator.comparing(etm -> !etm.getEventType().isEventTypeConference());
        Comparator<EventTypeMetrics> comparatorByInactive = Comparator.comparing(etm -> etm.getEventType().isInactive());
        Comparator<EventTypeMetrics> comparatorByName = Comparator.comparing(etm -> LocalizationUtils.getString(etm.getEventType().getName(), language));

        eventTypeMetrics.sort(comparatorByIsConference.thenComparing(comparatorByInactive).thenComparing(comparatorByName));

        return EventTypeMetricsDto.convertToDto(eventTypeMetrics, language);
    }
}
