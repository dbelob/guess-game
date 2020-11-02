package guess.domain.source;

import java.util.List;

/**
 * Company list.
 */
public class CompanyList {
    private List<Company> companies;

    public CompanyList() {
    }

    public CompanyList(List<Company> companies) {
        this.companies = companies;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }
}
