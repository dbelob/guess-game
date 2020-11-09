package guess.domain.answer;

import guess.domain.QuestionAnswer;
import guess.domain.source.Company;

/**
 * Answer about company.
 */
public class CompanyAnswer extends QuestionAnswer<Company> implements Answer {
    public CompanyAnswer(Company company) {
        super(company);
    }

    public Company getCompany() {
        return getEntity();
    }
}
