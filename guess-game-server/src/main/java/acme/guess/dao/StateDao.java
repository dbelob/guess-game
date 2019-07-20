package acme.guess.dao;

import acme.guess.domain.QuestionAnswersSet;
import acme.guess.domain.StartParameters;
import acme.guess.domain.State;

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
