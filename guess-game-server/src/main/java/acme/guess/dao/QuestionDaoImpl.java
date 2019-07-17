package acme.guess.dao;

import acme.guess.domain.QuestionSet;
import acme.guess.util.YamlUtils;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

/**
 * Question DAO implementation.
 */
@Repository
public class QuestionDaoImpl implements QuestionDao {
    private final List<QuestionSet> questionSets;

    public QuestionDaoImpl() throws IOException {
        this.questionSets = YamlUtils.readQuestionSets();
    }

    @Override
    public List<QuestionSet> getQuestionSets() {
        return questionSets;
    }
}
