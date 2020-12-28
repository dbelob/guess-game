package guess.domain.source;

import java.util.Objects;

public class NameCompany {
    private final String name;
    private final Company company;

    public NameCompany(String name, Company company) {
        this.name = name;
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public Company getCompany() {
        return company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NameCompany)) return false;
        NameCompany that = (NameCompany) o;
        return Objects.equals(name, that.name) && Objects.equals(company, that.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, company);
    }
}
