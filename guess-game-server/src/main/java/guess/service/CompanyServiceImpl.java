package guess.service;

import guess.dao.CompanyDao;
import guess.domain.source.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Company service implementation.
 */
@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyDao companyDao;

    @Autowired
    public CompanyServiceImpl(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    @Override
    public List<Company> getCompanies() {
        return companyDao.getCompanies();
    }
}
