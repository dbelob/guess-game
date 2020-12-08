package guess.service;

import guess.dao.CompanyDao;
import guess.domain.source.Company;
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

@DisplayName("CompanyServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class CompanyServiceImplTest {
    private static final Company company0 = new Company(0, Collections.emptyList());
    private static final Company company1 = new Company(1, Collections.emptyList());
    private static final Company company2 = new Company(2, Collections.emptyList());

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        CompanyDao companyDao() {
            CompanyDao companyDao = Mockito.mock(CompanyDao.class);

            Mockito.when(companyDao.getCompanies()).thenReturn(List.of(company0, company1, company2));

            return companyDao;
        }

        @Bean
        CompanyService companyService() {
            return new CompanyServiceImpl(companyDao());
        }
    }

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private CompanyService companyService;

    @Test
    void getCompanies() {
        companyService.getCompanies();
        Mockito.verify(companyDao, VerificationModeFactory.times(1)).getCompanies();
        Mockito.verifyNoMoreInteractions(companyDao);
    }
}
