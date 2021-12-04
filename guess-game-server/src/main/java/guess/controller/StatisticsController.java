package guess.controller;

import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.MeasureType;
import guess.dto.statistics.*;
import guess.dto.statistics.olap.*;
import guess.service.LocaleService;
import guess.service.OlapService;
import guess.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Statistics controller.
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
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
    public EventStatisticsDto getEventStatistics(@RequestParam(required = false) Long organizerId,
                                                 @RequestParam(required = false) Long eventTypeId, HttpSession httpSession) {
        var eventStatistics = statisticsService.getEventStatistics(organizerId, eventTypeId);
        var language = localeService.getLanguage(httpSession);
        var eventStatisticsDto = EventStatisticsDto.convertToDto(eventStatistics, language);

        eventStatisticsDto.getEventMetricsList().sort(Comparator.comparing(EventMetricsDto::getName, String.CASE_INSENSITIVE_ORDER));

        return eventStatisticsDto;
    }

    @GetMapping("/speaker-statistics")
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

    @GetMapping("/cube-types")
    public List<CubeType> getCubeTypes() {
        return List.of(CubeType.values());
    }

    @GetMapping("/measure-types")
    public List<MeasureType> getMeasureTypes(@RequestParam String cubeType) {
        if (cubeType.isEmpty()) {
            return Collections.emptyList();
        } else {
            return olapService.getMeasureTypes(CubeType.valueOf(cubeType));
        }
    }

    @PostMapping("/olap-statistics")
    public OlapStatisticsDto getOlapStatistics(@RequestBody OlapParametersDto olapParameters, HttpSession httpSession) {
        var olapStatistics = olapService.getOlapStatistics(olapParameters);
        var language = localeService.getLanguage(httpSession);
        var olapStatisticsDto = OlapStatisticsDto.convertToDto(olapStatistics, language);

        if (olapStatisticsDto.getEventTypeStatistics() != null) {
            Comparator<OlapEventTypeMetricsDto> comparatorByIsConference = Comparator.comparing(OlapEventTypeMetricsDto::isConference).reversed();
            Comparator<OlapEventTypeMetricsDto> comparatorByOrganizerName = Comparator.comparing(OlapEventTypeMetricsDto::getOrganizerName, String.CASE_INSENSITIVE_ORDER);
            Comparator<OlapEventTypeMetricsDto> comparatorByName = Comparator.comparing(OlapEventTypeMetricsDto::getName, String.CASE_INSENSITIVE_ORDER);

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
    public OlapEntityStatisticsDto<Integer, OlapEventTypeMetricsDto> getOlapEventTypeStatistics(
            @RequestBody OlapEventTypeParametersDto olapParameters, HttpSession httpSession) {
        var eventTypeStatistics = olapService.getOlapEventTypeStatistics(olapParameters);
        var language = localeService.getLanguage(httpSession);
        var olapEventTypeStatisticsDto = OlapEventTypeStatisticsDto.convertToDto(eventTypeStatistics, language);

        Comparator<OlapEventTypeMetricsDto> comparatorByIsConference = Comparator.comparing(OlapEventTypeMetricsDto::isConference).reversed();
        Comparator<OlapEventTypeMetricsDto> comparatorByOrganizerName = Comparator.comparing(OlapEventTypeMetricsDto::getOrganizerName, String.CASE_INSENSITIVE_ORDER);
        Comparator<OlapEventTypeMetricsDto> comparatorByName = Comparator.comparing(OlapEventTypeMetricsDto::getName, String.CASE_INSENSITIVE_ORDER);

        olapEventTypeStatisticsDto.getMetricsList().sort(comparatorByIsConference.thenComparing(comparatorByOrganizerName).thenComparing(comparatorByName));

        return olapEventTypeStatisticsDto;
    }

    @PostMapping("/olap-speaker-statistics")
    public OlapEntityStatisticsDto<Integer, OlapSpeakerMetricsDto> getOlapSpeakerStatistics(
            @RequestBody OlapSpeakerParametersDto olapParameters, HttpSession httpSession) {
        var speakerStatistics = olapService.getOlapSpeakerStatistics(olapParameters);
        var language = localeService.getLanguage(httpSession);
        var olapSpeakerStatisticsDto = OlapSpeakerStatisticsDto.convertToDto(speakerStatistics, language);

        Comparator<OlapSpeakerMetricsDto> comparatorByTotal = Comparator.comparing(OlapSpeakerMetricsDto::getTotal).reversed();
        Comparator<OlapSpeakerMetricsDto> comparatorByName = Comparator.comparing(OlapSpeakerMetricsDto::getName, String.CASE_INSENSITIVE_ORDER);

        olapSpeakerStatisticsDto.getMetricsList().sort(comparatorByTotal.thenComparing(comparatorByName));

        return olapSpeakerStatisticsDto;
    }

    @PostMapping("/olap-city-statistics")
    public OlapEntityStatisticsDto<Integer, OlapCityMetricsDto> getOlapCityStatistics(
            @RequestBody OlapCityParametersDto olapParameters, HttpSession httpSession) {
        var cityStatistics = olapService.getOlapCityStatistics(olapParameters);
        var language = localeService.getLanguage(httpSession);
        var olapCityStatisticsDto = OlapCityStatisticsDto.convertToDto(cityStatistics, language);

        olapCityStatisticsDto.getMetricsList().sort(Comparator.comparing(OlapCityMetricsDto::getName, String.CASE_INSENSITIVE_ORDER));

        return olapCityStatisticsDto;
    }
}
