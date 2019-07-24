package guess.dao;

import guess.domain.QuestionAnswersSet;
import guess.domain.StartParameters;
import guess.domain.State;

/**
 * State DAO.
 */
public interface StateDao {
    State getState();

    void setState(State state);

    StartParameters getStartParameters();

    void setStartParameters(StartParameters startParameters);

    QuestionAnswersSet getQuestionAnswersSet();

    void setQuestionAnswersSet(QuestionAnswersSet questionAnswersSet);
}
