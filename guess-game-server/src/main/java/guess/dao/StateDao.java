package guess.dao;

import guess.domain.QuestionAnswersSet;
import guess.domain.StartParameters;
import guess.domain.State;

import javax.servlet.http.HttpSession;

/**
 * State DAO.
 */
public interface StateDao {
    State getState(HttpSession httpSession);

    void setState(State state, HttpSession httpSession);

    StartParameters getStartParameters(HttpSession httpSession);

    void setStartParameters(StartParameters startParameters, HttpSession httpSession);

    QuestionAnswersSet getQuestionAnswersSet(HttpSession httpSession);

    void setQuestionAnswersSet(QuestionAnswersSet questionAnswersSet, HttpSession httpSession);
}
