package guess.controller;

import guess.domain.source.EventType;
import guess.domain.statistics.olap.Cube;
import guess.domain.statistics.olap.Measure;
import guess.dto.eventtype.EventTypeSuperBriefDto;
import guess.dto.statistics.*;
import guess.service.LocaleService;
import guess.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Statistics controller.
 */
@Controller
@RequestMapping("/api/statistics")
public class StatisticsController {
    private static final Logger log = LoggerFactory.getLogger(StatisticsController.class);

    private final StatisticsService statisticsService;
    private final LocaleService localeService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService, LocaleService localeService) {
        this.statisticsService = statisticsService;
        this.localeService = localeService;
    }

    @GetMapping("/event-type-statistics")
    @ResponseBody
    public EventTypeStatisticsDto getEventTypeStatistics(@RequestParam boolean conferences, @RequestParam boolean meetups,
                                                         @RequestParam(required = false) Long organizerId, HttpSession httpSession) {
        var eventTypeStatistics = statisticsService.getEventTypeStatistics(conferences, meetups, organizerId);
        var language = localeService.getLanguage(httpSession);
        var eventTypeStatisticsDto = EventTypeStatisticsDto.convertToDto(eventTypeStatistics, language);
        Comparator<EventTypeMetricsDto> comparatorByIsConference = Comparator.comparing(EventTypeMetricsDto::isConference).reversed();
        Comparator<EventTypeMetricsDto> comparatorByOrganizerName = Comparator.comparing(EventTypeMetricsDto::getOrganizerName, String.CASE_INSENSITIVE_ORDER);
        Comparator<EventTypeMetricsDto> comparatorByName = Comparator.comparing(EventTypeMetricsDto::getDisplayName, String.CASE_INSENSITIVE_ORDER);

        eventTypeStatisticsDto.getEventTypeMetricsList().sort(comparatorByIsConference.thenComparing(comparatorByOrganizerName).thenComparing(comparatorByName));

        return eventTypeStatisticsDto;
    }

    @GetMapping("/event-statistics")
    @ResponseBody
    public EventStatisticsDto getEventStatistics(@RequestParam(required = false) Long eventTypeId, HttpSession httpSession) {
        var eventStatistics = statisticsService.getEventStatistics(eventTypeId);
        var language = localeService.getLanguage(httpSession);
        var eventStatisticsDto = EventStatisticsDto.convertToDto(eventStatistics, language);

        eventStatisticsDto.getEventMetricsList().sort(Comparator.comparing(EventMetricsDto::getName, String.CASE_INSENSITIVE_ORDER));

        return eventStatisticsDto;
    }

    @GetMapping("/speaker-statistics")
    @ResponseBody
    public SpeakerStatisticsDto getSpeakerStatistics(@RequestParam boolean conferences, @RequestParam boolean meetups,
                                                     @RequestParam(required = false) Long organizerId,
                                                     @RequestParam(required = false) Long eventTypeId, HttpSession httpSession) {
        var speakerStatistics = statisticsService.getSpeakerStatistics(conferences, meetups, organizerId, eventTypeId);
        var language = localeService.getLanguage(httpSession);
        var speakerStatisticsDto = SpeakerStatisticsDto.convertToDto(speakerStatistics, language);
        Comparator<SpeakerMetricsDto> comparatorByTalksQuantity = Comparator.comparing(SpeakerMetricsDto::getTalksQuantity).reversed();
        Comparator<SpeakerMetricsDto> comparatorByEventsQuantity = Comparator.comparing(SpeakerMetricsDto::getEventsQuantity).reversed();
        Comparator<SpeakerMetricsDto> comparatorByEventTypesQuantity = Comparator.comparing(SpeakerMetricsDto::getEventTypesQuantity).reversed();

        speakerStatisticsDto.getSpeakerMetricsList().sort(comparatorByTalksQuantity.thenComparing(comparatorByEventsQuantity).thenComparing(comparatorByEventTypesQuantity));

        return speakerStatisticsDto;
    }

    @GetMapping("/company-statistics")
    @ResponseBody
    public CompanyStatisticsDto getCompanyStatistics(@RequestParam boolean conferences, @RequestParam boolean meetups,
                                                     @RequestParam(required = false) Long organizerId,
                                                     @RequestParam(required = false) Long eventTypeId, HttpSession httpSession) {
        var companyStatistics = statisticsService.getCompanyStatistics(conferences, meetups, organizerId, eventTypeId);
        var language = localeService.getLanguage(httpSession);
        var companyStatisticsDto = CompanyStatisticsDto.convertToDto(companyStatistics, language);
        Comparator<CompanyMetricsDto> comparatorByTalksQuantity = Comparator.comparing(CompanyMetricsDto::getTalksQuantity).reversed();
        Comparator<CompanyMetricsDto> comparatorByEventsQuantity = Comparator.comparing(CompanyMetricsDto::getEventsQuantity).reversed();
        Comparator<CompanyMetricsDto> comparatorByEventTypesQuantity = Comparator.comparing(CompanyMetricsDto::getEventTypesQuantity).reversed();

        companyStatisticsDto.getCompanyMetricsList().sort(comparatorByTalksQuantity.thenComparing(comparatorByEventsQuantity).thenComparing(comparatorByEventTypesQuantity));

        return companyStatisticsDto;
    }

    @GetMapping("/conferences")
    @ResponseBody
    public List<EventTypeSuperBriefDto> getConferences(HttpSession httpSession) {
        List<EventType> eventTypes = statisticsService.getConferences();
        var language = localeService.getLanguage(httpSession);
        List<EventTypeSuperBriefDto> eventTypeSuperBriefDtoList = EventTypeSuperBriefDto.convertToSuperBriefDto(eventTypes, language);

        eventTypeSuperBriefDtoList.sort(Comparator.comparing(EventTypeSuperBriefDto::getName, String.CASE_INSENSITIVE_ORDER));

        return eventTypeSuperBriefDtoList;
    }

    @GetMapping("/cubes")
    @ResponseBody
    public List<Cube> getCubes() {
        return List.of(Cube.values());
    }

    @GetMapping("/measures")
    @ResponseBody
    public List<Measure> getMeasures(@RequestParam String cube) {
        if ((cube == null) || cube.isEmpty()) {
            return Collections.emptyList();
        } else {
            Cube cubeEnum = Cube.valueOf(cube);

            return cubeEnum.getMeasures();
        }
    }

    @PostMapping("/olap-statistics")
    @ResponseBody
    public OlapStatisticsDto getOlapStatistics(@RequestBody OlapParametersDto olapParameters) {
        log.debug("cube: {}, measure: {}, organizerId: {}, eventTypeId: {}, speakerIds: {}, companyIds: {}",
                olapParameters.getCube(), olapParameters.getMeasure(), olapParameters.getOrganizerId(),
                olapParameters.getEventTypeId(), olapParameters.getSpeakerIds(), olapParameters.getCompanyIds());
        //TODO: implement
        return new OlapStatisticsDto();
    }
}
