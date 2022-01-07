package guess.service;

import guess.domain.Language;
import guess.domain.source.Company;

import java.util.List;

/**
 * Company service.
 */
public interface CompanyService {
    List<Company> getCompanies();

    Company getCompanyById(long id);

    List<Company> getCompaniesByIds(List<Long> ids);

    List<Company> getCompaniesByFirstLetters(String firstLetters, Language language);
}
