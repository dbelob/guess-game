package guess.service;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.QuestionAnswersSet;
import guess.domain.StartParameters;
import guess.domain.State;

import javax.servlet.http.HttpSession;

/**
 * State service.
 */
public interface StateService {
    void setStartParameters(StartParameters startParameters, HttpSession httpSession) throws QuestionSetNotExistsException;

    State getState(HttpSession httpSession);

    void setState(State state, HttpSession httpSession);

    QuestionAnswersSet getQuestionAnswersSet(HttpSession httpSession);
}
