package guess.domain.question;

import guess.domain.QuestionAnswer;
import guess.domain.source.Company;
import guess.domain.source.Speaker;

import java.util.List;

/**
 * Question about speaker by company.
 */
public class SpeakerByCompanyQuestion extends QuestionAnswer<Company> implements Question {
    private final List<Speaker> speakers;

    public SpeakerByCompanyQuestion(List<Speaker> speakers, Company company) {
        super(company);

        this.speakers = speakers;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public Company getCompany() {
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
