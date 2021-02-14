package guess.dao;

import guess.domain.GameState;
import guess.domain.question.QuestionAnswersSet;
import guess.domain.StartParameters;

import javax.servlet.http.HttpSession;

/**
 * State DAO.
 */
public interface StateDao {
    GameState getGameState(HttpSession httpSession);

    void setGameState(GameState state, HttpSession httpSession);

    StartParameters getStartParameters(HttpSession httpSession);

    void setStartParameters(StartParameters startParameters, HttpSession httpSession);

    QuestionAnswersSet getQuestionAnswersSet(HttpSession httpSession);

    void setQuestionAnswersSet(QuestionAnswersSet questionAnswersSet, HttpSession httpSession);
}
