package guess.controller;

import guess.domain.Language;
import guess.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * Locale controller.
 */
@Controller
@RequestMapping("/api/locale")
public class LocaleController {
    private LocaleService localeService;

    @Autowired
    public LocaleController(LocaleService localeService) {
        this.localeService = localeService;
    }

    @GetMapping("/language")
    @ResponseBody
    public String getLanguage(HttpSession httpSession) {
        return localeService.getLanguage(httpSession).getCode();
    }

    @PutMapping("/language")
    @ResponseStatus(HttpStatus.OK)
    public void setLanguage(@RequestBody String languageCode, HttpSession httpSession) {
        Language language = Language.getLanguageByCode(languageCode);

        Objects.requireNonNull(language,
                () -> String.format("Language code %s not found", languageCode));
        localeService.setLanguage(language, httpSession);
    }
}
