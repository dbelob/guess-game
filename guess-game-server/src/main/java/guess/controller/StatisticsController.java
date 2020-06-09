package guess.controller;

import guess.domain.Language;
import guess.domain.statistics.EventTypeMetrics;
import guess.dto.statistics.EventTypeMetricsDto;
import guess.service.LocaleService;
import guess.service.StatisticsService;
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
        List<EventTypeMetricsDto> eventTypeMetricsDtoList = EventTypeMetricsDto.convertToDto(eventTypeMetrics, language);

        eventTypeMetricsDtoList.sort(Comparator.comparing(EventTypeMetricsDto::getSortName, String.CASE_INSENSITIVE_ORDER));

        return eventTypeMetricsDtoList;
    }
}
