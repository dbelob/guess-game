package guess.controller;

import guess.domain.Language;
import guess.domain.source.Organizer;
import guess.dto.organizer.OrganizerDto;
import guess.service.LocaleService;
import guess.service.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;

/**
 * Organizer controller.
 */
@Controller
@RequestMapping("/api/organizer")
public class OrganizerController {
    private final OrganizerService organizerService;
    private final LocaleService localeService;

    @Autowired
    public OrganizerController(OrganizerService organizerService, LocaleService localeService) {
        this.organizerService = organizerService;
        this.localeService = localeService;
    }

    @GetMapping("/organizers")
    @ResponseBody
    public List<OrganizerDto> getOrganizers(HttpSession httpSession) {
        Language language = localeService.getLanguage(httpSession);
        List<Organizer> organizers = organizerService.getOrganizers();
        List<OrganizerDto> organizerDtoList = OrganizerDto.convertToDto(organizers, language);

        organizerDtoList.sort(Comparator.comparing(OrganizerDto::getName));

        return organizerDtoList;
    }
}
