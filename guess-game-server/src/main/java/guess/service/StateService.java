package guess.service;

import guess.domain.StartParameters;
import guess.domain.State;
import guess.domain.question.QuestionAnswersSet;

import javax.servlet.http.HttpSession;

/**
 * State service.
 */
public interface StateService {
    void setStartParameters(StartParameters startParameters, HttpSession httpSession);

    State getState(HttpSession httpSession);

    void setState(State state, HttpSession httpSession);

    QuestionAnswersSet getQuestionAnswersSet(HttpSession httpSession);
}
