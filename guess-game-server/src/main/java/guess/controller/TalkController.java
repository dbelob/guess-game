package guess.controller;

import guess.domain.Language;
import guess.domain.source.Talk;
import guess.dto.talk.TalkBriefDto;
import guess.service.EventService;
import guess.service.EventTypeService;
import guess.service.LocaleService;
import guess.service.TalkService;
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
 * Talk controller.
 */
@Controller
@RequestMapping("/api/talk")
public class TalkController {
    private final TalkService talkService;
    private final EventService eventService;
    private final EventTypeService eventTypeService;
    private final LocaleService localeService;

    @Autowired
    public TalkController(TalkService talkService, EventService eventService, EventTypeService eventTypeService, LocaleService localeService) {
        this.talkService = talkService;
        this.eventService = eventService;
        this.eventTypeService = eventTypeService;
        this.localeService = localeService;
    }

    @GetMapping("/talks")
    @ResponseBody
    public List<TalkBriefDto> getTalks(@RequestParam(required = false) Long eventTypeId,
                                       @RequestParam(required = false) Long eventId,
                                       @RequestParam(required = false) String talkName,
                                       @RequestParam(required = false) String speakerName,
                                       HttpSession httpSession) {
        List<Talk> talks = talkService.getTalks(eventTypeId, eventId, talkName, speakerName);
        Language language = localeService.getLanguage(httpSession);
        List<TalkBriefDto> talkBriefDtoList = TalkBriefDto.convertToDto(talks, eventService::getEventByTalk,
                eventTypeService::getEventTypeByEvent, language);

        talkBriefDtoList.sort(Comparator.comparing(TalkBriefDto::getEventName).thenComparing(TalkBriefDto::getName));

        return talkBriefDtoList;
    }
}
