package guess.dao;

import guess.domain.source.Company;

import java.util.List;

/**
 * Company DAO.
 */
public interface CompanyDao {
    List<Company> getCompanies();

    Company getCompanyById(long id);

    List<Company> getCompaniesByIds(List<Long> ids);
}
