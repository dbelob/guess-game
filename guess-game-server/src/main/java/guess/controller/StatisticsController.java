package guess.controller;

import guess.domain.source.EventType;
import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;
import guess.dto.eventtype.EventTypeSuperBriefDto;
import guess.dto.statistics.*;
import guess.dto.statistics.olap.*;
import guess.service.LocaleService;
import guess.service.OlapService;
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
    private final OlapService olapService;
    private final LocaleService localeService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService, OlapService olapService, LocaleService localeService) {
        this.statisticsService = statisticsService;
        this.olapService = olapService;
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

    @GetMapping("/cube-types")
    @ResponseBody
    public List<CubeType> getCubeTypes() {
        return List.of(CubeType.values());
    }

    @GetMapping("/measure-types")
    @ResponseBody
    public List<MeasureType> getMeasureTypes(@RequestParam String cubeType) {
        if ((cubeType == null) || cubeType.isEmpty()) {
            return Collections.emptyList();
        } else {
            CubeType cubeTypeEnum = CubeType.valueOf(cubeType);

            return olapService.getMeasureTypes(cubeTypeEnum);
        }
    }

    @PostMapping("/olap-statistics")
    @ResponseBody
    public OlapStatisticsDto getOlapStatistics(@RequestBody OlapParametersDto olapParameters, HttpSession httpSession) {
        var olapStatistics = olapService.getOlapStatistics(olapParameters.getCubeType(), olapParameters.getMeasureType(),
                olapParameters.isConferences(), olapParameters.isMeetups(), olapParameters.getOrganizerId(),
                olapParameters.getEventTypeIds(), olapParameters.getSpeakerIds(), olapParameters.getCompanyIds());
        var language = localeService.getLanguage(httpSession);
        var olapStatisticsDto = OlapStatisticsDto.convertToDto(olapStatistics, language);

        if (olapStatisticsDto.getEventTypeStatistics() != null) {
            Comparator<OlapEventTypeMetricsDto> comparatorByIsConference = Comparator.comparing(OlapEventTypeMetricsDto::isConference).reversed();
            Comparator<OlapEventTypeMetricsDto> comparatorByOrganizerName = Comparator.comparing(OlapEventTypeMetricsDto::getOrganizerName, String.CASE_INSENSITIVE_ORDER);
            Comparator<OlapEventTypeMetricsDto> comparatorByName = Comparator.comparing(OlapEventTypeMetricsDto::getDisplayName, String.CASE_INSENSITIVE_ORDER);

            olapStatisticsDto.getEventTypeStatistics().getMetricsList().sort(comparatorByIsConference.thenComparing(comparatorByOrganizerName).thenComparing(comparatorByName));
        }

        if (olapStatisticsDto.getSpeakerStatistics() != null) {
            Comparator<OlapSpeakerMetricsDto> comparatorByTotal = Comparator.comparing(OlapSpeakerMetricsDto::getTotal).reversed();
            Comparator<OlapSpeakerMetricsDto> comparatorByName = Comparator.comparing(OlapSpeakerMetricsDto::getName, String.CASE_INSENSITIVE_ORDER);

            olapStatisticsDto.getSpeakerStatistics().getMetricsList().sort(comparatorByTotal.thenComparing(comparatorByName));
        }

        if (olapStatisticsDto.getCompanyStatistics() != null) {
            Comparator<OlapCompanyMetricsDto> comparatorByTotal = Comparator.comparing(OlapCompanyMetricsDto::getTotal).reversed();
            Comparator<OlapCompanyMetricsDto> comparatorByName = Comparator.comparing(OlapCompanyMetricsDto::getName, String.CASE_INSENSITIVE_ORDER);

            olapStatisticsDto.getCompanyStatistics().getMetricsList().sort(comparatorByTotal.thenComparing(comparatorByName));
        }

        return olapStatisticsDto;
    }

    @PostMapping("/olap-event-type-statistics")
    @ResponseBody
    public OlapEntityStatisticsDto<Integer, OlapEventTypeMetricsDto> getOlapEventTypeStatistics(
            @RequestBody OlapEventTypeParametersDto olapParameters, HttpSession httpSession) {
        var eventTypeStatistics = olapService.getOlapEventTypeStatistics(
                olapParameters.getCubeType(), olapParameters.getMeasureType(), olapParameters.isConferences(),
                olapParameters.isMeetups(), olapParameters.getOrganizerId(), olapParameters.getEventTypeIds(),
                olapParameters.getSpeakerId(), olapParameters.getCompanyId());
        var language = localeService.getLanguage(httpSession);
        var olapEventTypeStatisticsDto = OlapEventTypeStatisticsDto.convertToDto(eventTypeStatistics, language);

        Comparator<OlapEventTypeMetricsDto> comparatorByIsConference = Comparator.comparing(OlapEventTypeMetricsDto::isConference).reversed();
        Comparator<OlapEventTypeMetricsDto> comparatorByOrganizerName = Comparator.comparing(OlapEventTypeMetricsDto::getOrganizerName, String.CASE_INSENSITIVE_ORDER);
        Comparator<OlapEventTypeMetricsDto> comparatorByName = Comparator.comparing(OlapEventTypeMetricsDto::getDisplayName, String.CASE_INSENSITIVE_ORDER);

        olapEventTypeStatisticsDto.getMetricsList().sort(comparatorByIsConference.thenComparing(comparatorByOrganizerName).thenComparing(comparatorByName));

        return olapEventTypeStatisticsDto;
    }

    @PostMapping("/olap-speaker-statistics")
    @ResponseBody
    public OlapEntityStatisticsDto<Integer, OlapSpeakerMetricsDto> getOlapSpeakerStatistics(
            @RequestBody OlapSpeakerParametersDto olapParameters, HttpSession httpSession) {
        var speakerStatistics = olapService.getOlapSpeakerStatistics(
                olapParameters.getCubeType(), olapParameters.getMeasureType(), olapParameters.getCompanyId(),
                olapParameters.getEventTypeId());
        var language = localeService.getLanguage(httpSession);
        var olapSpeakerStatisticsDto = OlapSpeakerStatisticsDto.convertToDto(speakerStatistics, language);

        Comparator<OlapSpeakerMetricsDto> comparatorByTotal = Comparator.comparing(OlapSpeakerMetricsDto::getTotal).reversed();
        Comparator<OlapSpeakerMetricsDto> comparatorByName = Comparator.comparing(OlapSpeakerMetricsDto::getName, String.CASE_INSENSITIVE_ORDER);

        olapSpeakerStatisticsDto.getMetricsList().sort(comparatorByTotal.thenComparing(comparatorByName));

        return olapSpeakerStatisticsDto;
    }
}
