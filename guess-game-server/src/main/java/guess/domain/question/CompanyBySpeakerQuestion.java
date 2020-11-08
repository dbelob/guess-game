package guess.domain.question;

import guess.domain.QuestionAnswer;
import guess.domain.source.Company;
import guess.domain.source.Speaker;

import java.util.List;

/**
 * Question about company by speaker.
 */
public class CompanyBySpeakerQuestion extends QuestionAnswer<Speaker> implements Question {
    private final List<Company> companies;

    public CompanyBySpeakerQuestion(List<Company> companies, Speaker speaker) {
        super(speaker);

        this.companies = companies;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public Speaker getSpeaker() {
        return getEntity();
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
