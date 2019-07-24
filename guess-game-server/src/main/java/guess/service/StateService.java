package guess.service;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.QuestionAnswersSet;
import guess.domain.StartParameters;
import guess.domain.State;

/**
 * State service.
 */
public interface StateService {
    void setStartParameters(StartParameters startParameters) throws QuestionSetNotExistsException;

    State getState();

    void setState(State state);

    QuestionAnswersSet getQuestionAnswersSet();
}
