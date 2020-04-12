package guess.domain.source;

public class NameCompany {
    private final String name;
    private final String company;

    public NameCompany(String name, String company) {
        this.name = name;
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NameCompany that = (NameCompany) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return company != null ? company.equals(that.company) : that.company == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (company != null ? company.hashCode() : 0);
        return result;
    }
}
