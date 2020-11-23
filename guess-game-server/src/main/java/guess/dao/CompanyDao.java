package guess.dao;

import guess.domain.source.Company;

import java.util.List;

/**
 * Company DAO.
 */
public interface CompanyDao {
    List<Company> getCompanies();
}
