package guess.domain.statistics.company;

import guess.domain.source.Company;

/**
 * Company search result.
 */
public record CompanySearchResult(Company company, long speakersQuantity, long javaChampionsQuantity,
                                  long mvpsQuantity) {
}
