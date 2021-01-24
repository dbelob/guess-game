package guess.service;

import guess.dao.OrganizerDao;
import guess.domain.source.Organizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@DisplayName("OrganizerServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class OrganizerServiceImplTest {
    private static final Organizer organizer0 = new Organizer(0, Collections.emptyList());
    private static final Organizer organizer1 = new Organizer(1, Collections.emptyList());

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        OrganizerDao organizerDao() {
            OrganizerDao organizerDao = Mockito.mock(OrganizerDao.class);

            Mockito.when(organizerDao.getOrganizers()).thenReturn(List.of(organizer0, organizer1));

            return organizerDao;
        }

        @Bean
        OrganizerService organizerService() {
            return new OrganizerServiceImpl(organizerDao());
        }
    }

    @Autowired
    private OrganizerDao organizerDao;

    @Autowired
    private OrganizerService organizerService;

    @Test
    void getOrganizers() {
        organizerService.getOrganizers();
        Mockito.verify(organizerDao, VerificationModeFactory.times(1)).getOrganizers();
        Mockito.verifyNoMoreInteractions(organizerDao);
    }
}
