package guess.dao;

import guess.domain.Language;
import guess.util.HttpSessionUtils;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpSession;

/**
 * Locale DAO implementation.
 */
@Repository
public class LocaleDaoImpl implements LocaleDao {
    @Override
    public Language getLanguage(HttpSession httpSession) {
        return HttpSessionUtils.getLanguage(httpSession);
    }

    @Override
    public void setLanguage(Language language, HttpSession httpSession) {
        HttpSessionUtils.setLanguage(language, httpSession);
    }
}
