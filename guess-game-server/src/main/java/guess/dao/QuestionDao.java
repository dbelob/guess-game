package guess.dao;

import guess.domain.GuessMode;
import guess.domain.question.Question;
import guess.domain.question.QuestionSet;

import java.util.List;

/**
 * Question DAO.
 */
public interface QuestionDao {
    List<QuestionSet> readQuestionSets();

    List<QuestionSet> getQuestionSets();

    List<Question> getQuestionByIds(List<Long> eventTypeIds, List<Long> eventIds, GuessMode guessMode);

    List<QuestionSet> getSubQuestionSets(List<Long> eventTypeIds, List<Long> eventIds);
}
