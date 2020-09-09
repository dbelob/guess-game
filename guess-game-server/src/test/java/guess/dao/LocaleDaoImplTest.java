package guess.dao;

import guess.domain.Language;
import guess.util.HttpSessionUtils;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LocaleDaoImpl class tests")
class LocaleDaoImplTest {
    private final static LocaleDao localeDao = new LocaleDaoImpl();

    @Test
    void getLanguage(@Mocked HttpSessionUtils mock) {
        HttpSession httpSession = new MockHttpSession();

        localeDao.getLanguage(httpSession);

        new Verifications() {{
            HttpSession capturedHttpSession;

            HttpSessionUtils.getLanguage(capturedHttpSession = withCapture());
            times = 1;

            assertEquals(httpSession, capturedHttpSession);
        }};
    }

    @Test
    void setLanguage(@Mocked HttpSessionUtils mock) {
        Language language = Language.ENGLISH;
        HttpSession httpSession = new MockHttpSession();

        localeDao.setLanguage(language, httpSession);

        new Verifications() {{
            Language capturedLanguage;
            HttpSession capturedHttpSession;

            HttpSessionUtils.setLanguage(capturedLanguage = withCapture(), capturedHttpSession = withCapture());
            times = 1;

            assertEquals(language, capturedLanguage);
            assertEquals(httpSession, capturedHttpSession);
        }};
    }
}
