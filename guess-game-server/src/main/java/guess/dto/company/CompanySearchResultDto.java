package guess.dto.company;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.util.LocalizationUtils;

/**
 * Company search result DTO.
 */
public record CompanySearchResultDto(long id, String name, long speakersQuantity, long javaChampionsQuantity,
                                     long mvpsQuantity) {
    public static CompanySearchResultDto convertToDto(Company company, long speakersQuantity, long javaChampionsQuantity,
                                                      long mvpsQuantity, Language language) {
        return new CompanySearchResultDto(
                company.getId(),
                LocalizationUtils.getString(company.getName(), language),
                speakersQuantity, javaChampionsQuantity, mvpsQuantity
        );
    }
}
