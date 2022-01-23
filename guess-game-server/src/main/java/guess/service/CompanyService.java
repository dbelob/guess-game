package guess.service;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.domain.statistics.company.CompanySearchResult;

import java.util.List;

/**
 * Company service.
 */
public interface CompanyService {
    List<Company> getCompanies();

    List<Company> getCompanies(String name);

    Company getCompanyById(long id);

    List<Company> getCompaniesByIds(List<Long> ids);

    List<Company> getCompaniesByFirstLetter(boolean isDigit, String firstLetter, Language language);

    List<Company> getCompaniesByFirstLetters(String firstLetters, Language language);

    List<CompanySearchResult> getCompanySearchResults(List<Company> companies);
}
