package guess.service;

import guess.dao.LocaleDao;
import guess.domain.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * Locale service implementation.
 */
@Service
public class LocaleServiceImpl implements LocaleService {
    private final LocaleDao localeDao;

    @Autowired
    public LocaleServiceImpl(LocaleDao localeDao) {
        this.localeDao = localeDao;
    }

    @Override
    public Language getLanguage(HttpSession httpSession) {
        return localeDao.getLanguage(httpSession);
    }

    @Override
    public void setLanguage(Language language, HttpSession httpSession) {
        localeDao.setLanguage(language, httpSession);
    }
}
