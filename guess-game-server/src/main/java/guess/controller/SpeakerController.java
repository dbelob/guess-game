package guess.controller;

import guess.domain.Language;
import guess.domain.source.Speaker;
import guess.dto.speaker.SpeakerBriefDto;
import guess.service.LocaleService;
import guess.service.SpeakerService;
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
 * Speaker controller.
 */
@Controller
@RequestMapping("/api/speaker")
public class SpeakerController {
    private final SpeakerService speakerService;
    private final LocaleService localeService;

    @Autowired
    public SpeakerController(SpeakerService speakerService, LocaleService localeService) {
        this.speakerService = speakerService;
        this.localeService = localeService;
    }

    @GetMapping("/first-letter-speakers")
    @ResponseBody
    public List<SpeakerBriefDto> getSpeakersByFirstLetter(@RequestParam String firstLetter, HttpSession httpSession) {
        Language language = localeService.getLanguage(httpSession);
        List<Speaker> speakers = speakerService.getSpeakersByFirstLetter(firstLetter, language);
        List<SpeakerBriefDto> speakerBriefDtoList = SpeakerBriefDto.convertToBriefDto(speakers, language);

        Comparator<SpeakerBriefDto> comparatorByName = Comparator.comparing(SpeakerBriefDto::getDisplayName, String.CASE_INSENSITIVE_ORDER);
        Comparator<SpeakerBriefDto> comparatorByCompany = Comparator.comparing(SpeakerBriefDto::getCompany, String.CASE_INSENSITIVE_ORDER);

        speakerBriefDtoList.sort(comparatorByName.thenComparing(comparatorByCompany));

        return speakerBriefDtoList;
    }
}
