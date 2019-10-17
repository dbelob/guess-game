package guess.dao;

import guess.domain.question.QuestionAnswersSet;
import guess.domain.StartParameters;
import guess.domain.State;
import guess.util.HttpSessionUtils;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpSession;

/**
 * State DAO implementation.
 */
@Repository
public class StateDaoImpl implements StateDao {
    @Override
    public State getState(HttpSession httpSession) {
        return HttpSessionUtils.getState(httpSession);
    }

    @Override
    public void setState(State state, HttpSession httpSession) {
        HttpSessionUtils.setState(state, httpSession);
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
    public QuestionAnswersSet getQuestionAnswersSet(HttpSession httpSession) {
        return HttpSessionUtils.getQuestionAnswersSet(httpSession);
    }

    @Override
    public void setQuestionAnswersSet(QuestionAnswersSet questionAnswersSet, HttpSession httpSession) {
        HttpSessionUtils.setQuestionAnswersSet(questionAnswersSet, httpSession);
    }
}
