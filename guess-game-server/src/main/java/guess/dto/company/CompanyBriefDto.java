package guess.dto.company;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.Objects;

/**
 * Company DTO (brief).
 */
public class CompanyBriefDto {
    private final long id;
    private final String name;

    public CompanyBriefDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static CompanyBriefDto convertToBriefDto(Company company, Language language) {
        return new CompanyBriefDto(
                company.getId(),
                LocalizationUtils.getString(company.getName(), language));
    }

    public static List<CompanyBriefDto> convertToBriefDto(List<Company> companies, Language language) {
        return companies.stream()
                .map(c -> convertToBriefDto(c, language))
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyBriefDto)) return false;
        CompanyBriefDto that = (CompanyBriefDto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
