package guess.dto.company;

import guess.domain.Language;
import guess.domain.statistics.company.CompanySearchResult;
import guess.util.LocalizationUtils;

/**
 * Company search result DTO.
 */
public record CompanySearchResultDto(long id, String name, long speakersQuantity, long javaChampionsQuantity,
                                     long mvpsQuantity) {
    public static CompanySearchResultDto convertToDto(CompanySearchResult companySearchResult, Language language) {
        return new CompanySearchResultDto(
                companySearchResult.company().getId(),
                LocalizationUtils.getString(companySearchResult.company().getName(), language),
                companySearchResult.speakersQuantity(),
                companySearchResult.javaChampionsQuantity(),
                companySearchResult.mvpsQuantity()
        );
    }
}
