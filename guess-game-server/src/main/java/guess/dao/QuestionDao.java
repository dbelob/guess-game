package guess.dao;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessMode;
import guess.domain.question.QuestionSet;
import guess.domain.question.Question;

import java.time.LocalDate;
import java.util.List;

/**
 * Question DAO.
 */
public interface QuestionDao {
    List<QuestionSet> getQuestionSets();

    Long getDefaultQuestionSetId(LocalDate date);

    QuestionSet getQuestionSetById(long id) throws QuestionSetNotExistsException;

    List<Question> getQuestionByIds(List<Long> questionSetIds, GuessMode guessMode) throws QuestionSetNotExistsException;
}
