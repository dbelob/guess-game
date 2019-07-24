package guess.dao;

import guess.domain.AnswerSet;

import java.util.List;

/**
 * Answer DAO.
 */
public interface AnswerDao {
    List<AnswerSet> getAnswerSets();

    void clearAnswerSets();

    void addAnswerSet(AnswerSet answerSet);
}
