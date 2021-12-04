package guess.controller;

import guess.domain.Language;
import guess.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Locale controller.
 */
@RestController
@RequestMapping("/api/locale")
public class LocaleController {
    private final LocaleService localeService;

    @Autowired
    public LocaleController(LocaleService localeService) {
        this.localeService = localeService;
    }

    @GetMapping("/language")
    public Language getLanguage(HttpSession httpSession) {
        return localeService.getLanguage(httpSession);
    }

    @PutMapping("/language")
    @ResponseStatus(HttpStatus.OK)
    public void setLanguage(@RequestBody String language, HttpSession httpSession) {
        localeService.setLanguage(Language.valueOf(language), httpSession);
    }
}
