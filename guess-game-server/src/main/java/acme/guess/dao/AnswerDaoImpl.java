package acme.guess.dao;

import acme.guess.domain.AnswerSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Answer DAO implementation.
 */
@Repository
public class AnswerDaoImpl implements AnswerDao {
    private List<AnswerSet> answerSets = new ArrayList<>();

    @Override
    public List<AnswerSet> getAnswerSets() {
        return answerSets;
    }

    @Override
    public void clearAnswerSets() {
        answerSets.clear();
    }

    @Override
    public void addAnswerSet(AnswerSet answerSet) {
        answerSets.add(answerSet);
    }
}
