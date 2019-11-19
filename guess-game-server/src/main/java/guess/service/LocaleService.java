package guess.service;

import guess.domain.Language;

import javax.servlet.http.HttpSession;

/**
 * Locale service.
 */
public interface LocaleService {
    Language getLanguage(HttpSession httpSession);

    void setLanguage(Language language, HttpSession httpSession);
}
