package guess.dao;

import guess.domain.Language;
import guess.util.HttpSessionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;

@DisplayName("LocaleDaoImpl class tests")
class LocaleDaoImplTest {
    private final static LocaleDao localeDao = new LocaleDaoImpl();

    @Test
    void getLanguage() {
        try (MockedStatic<HttpSessionUtils> mockedStatic = Mockito.mockStatic(HttpSessionUtils.class)) {
            HttpSession httpSession = new MockHttpSession();

            localeDao.getLanguage(httpSession);

            mockedStatic.verify(() -> HttpSessionUtils.getLanguage(httpSession), Mockito.times(1));
        }
    }

    @Test
    void setLanguage() {
        try (MockedStatic<HttpSessionUtils> mockedStatic = Mockito.mockStatic(HttpSessionUtils.class)) {
            Language language = Language.ENGLISH;
            HttpSession httpSession = new MockHttpSession();

            localeDao.setLanguage(language, httpSession);

            mockedStatic.verify(() -> HttpSessionUtils.setLanguage(language, httpSession), Mockito.times(1));
        }
    }
}
