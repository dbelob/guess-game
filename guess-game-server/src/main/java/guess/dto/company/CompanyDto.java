package guess.dto.company;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.util.LocalizationUtils;

/**
 * Company DTO.
 */
public class CompanyDto extends CompanyBriefDto {
    private final String siteLink;

    public CompanyDto(long id, String name, String siteLink) {
        super(id, name);

        this.siteLink = siteLink;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public static CompanyDto convertToDto(Company company, Language language) {
        return new CompanyDto(
                company.getId(),
                LocalizationUtils.getString(company.getName(), language),
                company.getSiteLink());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
