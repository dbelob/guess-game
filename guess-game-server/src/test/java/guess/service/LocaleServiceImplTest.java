package guess.service;

import guess.dao.LocaleDao;
import guess.domain.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpSession;

@DisplayName("LocaleServiceImpl class tests")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LocaleServiceImplTest.LocaleServiceImplTestConfiguration.class})
class LocaleServiceImplTest {
    @Configuration
    @Import(LocaleServiceImpl.class)
    static class LocaleServiceImplTestConfiguration {
    }

    @MockBean
    private LocaleDao localeDao;

    @Autowired
    private LocaleService localeService;

    @Test
    void getLanguage() {
        HttpSession httpSession = new MockHttpSession();

        localeService.getLanguage(httpSession);
        Mockito.verify(localeDao, VerificationModeFactory.times(1)).getLanguage(httpSession);
        Mockito.verifyNoMoreInteractions(localeDao);
        Mockito.reset(localeDao);
    }

    @Test
    void setLanguage() {
        HttpSession httpSession = new MockHttpSession();

        localeService.setLanguage(Language.ENGLISH, httpSession);
        Mockito.verify(localeDao, VerificationModeFactory.times(1)).setLanguage(Language.ENGLISH, httpSession);
        Mockito.verifyNoMoreInteractions(localeDao);
        Mockito.reset(localeDao);
    }
}
