package guess.dao;

import guess.domain.GameState;
import guess.domain.StartParameters;
import guess.domain.question.QuestionAnswersSet;
import guess.util.HttpSessionUtils;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpSession;

/**
 * State DAO implementation.
 */
@Repository
public class StateDaoImpl implements StateDao {
    @Override
    public GameState getGameState(HttpSession httpSession) {
        return HttpSessionUtils.getGameState(httpSession);
    }

    @Override
    public void setGameState(GameState state, HttpSession httpSession) {
        HttpSessionUtils.setGameState(state, httpSession);
    }

    @Override
    public StartParameters getStartParameters(HttpSession httpSession) {
        return HttpSessionUtils.getStartParameters(httpSession);
    }

    @Override
    public void setStartParameters(StartParameters startParameters, HttpSession httpSession) {
        HttpSessionUtils.setStartParameters(startParameters, httpSession);
    }

    @Override
    public void clearStartParameters(HttpSession httpSession) {
        HttpSessionUtils.clearStartParameters(httpSession);
    }

    @Override
    public QuestionAnswersSet getQuestionAnswersSet(HttpSession httpSession) {
        return HttpSessionUtils.getQuestionAnswersSet(httpSession);
    }

    @Override
    public void setQuestionAnswersSet(QuestionAnswersSet questionAnswersSet, HttpSession httpSession) {
        HttpSessionUtils.setQuestionAnswersSet(questionAnswersSet, httpSession);
    }

    @Override
    public void clearQuestionAnswersSet(HttpSession httpSession) {
        HttpSessionUtils.clearQuestionAnswersSet(httpSession);
    }
}
