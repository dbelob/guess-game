package guess.dto.company;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Company DTO.
 */
public class CompanyDto {
    private final long id;
    private final String name;

    public CompanyDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static CompanyDto convertToDto(Company company, Language language) {
        return new CompanyDto(
                company.getId(),
                LocalizationUtils.getString(company.getName(), language));
    }

    public static List<CompanyDto> convertToDto(List<Company> companies, Language language) {
        return companies.stream()
                .map(c -> convertToDto(c, language))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyDto)) return false;
        CompanyDto that = (CompanyDto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
