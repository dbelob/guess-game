package guess.dao;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessType;
import guess.domain.Question;
import guess.domain.QuestionSet;

import java.util.List;

/**
 * Question DAO.
 */
public interface QuestionDao {
    List<QuestionSet> getQuestionSets();

    QuestionSet getQuestionSetById(long id) throws QuestionSetNotExistsException;

    List<Question> getQuestionByIds(List<Long> questionSetIds, GuessType guessType) throws QuestionSetNotExistsException;
}
