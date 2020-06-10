package guess.controller;

import guess.domain.Language;
import guess.domain.statistics.EventStatistics;
import guess.domain.statistics.EventTypeStatistics;
import guess.dto.statistics.EventMetricsDto;
import guess.dto.statistics.EventStatisticsDto;
import guess.dto.statistics.EventTypeMetricsDto;
import guess.dto.statistics.EventTypeStatisticsDto;
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
    public EventTypeStatisticsDto getEventTypeStatistics(@RequestParam boolean conferences, @RequestParam boolean meetups,
                                                         HttpSession httpSession) {
        EventTypeStatistics eventTypeStatistics = statisticsService.getEventTypeStatistics(conferences, meetups);
        Language language = localeService.getLanguage(httpSession);
        EventTypeStatisticsDto eventTypeStatisticsDto = EventTypeStatisticsDto.convertToDto(eventTypeStatistics, language);

        eventTypeStatisticsDto.getEventTypeMetricsList().sort(Comparator.comparing(EventTypeMetricsDto::getSortName, String.CASE_INSENSITIVE_ORDER));

        return eventTypeStatisticsDto;
    }

    @GetMapping("/events")
    @ResponseBody
    public EventStatisticsDto getEventStatistics(@RequestParam Long eventId, HttpSession httpSession) {
        EventStatistics eventStatistics = statisticsService.getEventStatistics(eventId);
        Language language = localeService.getLanguage(httpSession);
        EventStatisticsDto eventStatisticsDto = EventStatisticsDto.convertToDto(eventStatistics, language);

        eventStatisticsDto.getEventMetricsList().sort(Comparator.comparing(EventMetricsDto::getName, String.CASE_INSENSITIVE_ORDER));

        return eventStatisticsDto;
    }
}
