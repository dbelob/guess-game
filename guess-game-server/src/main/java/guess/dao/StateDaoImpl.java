package guess.dao;

import guess.domain.QuestionAnswersSet;
import guess.domain.StartParameters;
import guess.domain.State;
import org.springframework.stereotype.Repository;

/**
 * State DAO implementation.
 */
@Repository
public class StateDaoImpl implements StateDao {
    private State state = State.START_STATE;
    private StartParameters startParameters;
    private QuestionAnswersSet questionAnswersSet;

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public StartParameters getStartParameters() {
        return startParameters;
    }

    @Override
    public void setStartParameters(StartParameters startParameters) {
        this.startParameters = startParameters;
    }

    @Override
    public QuestionAnswersSet getQuestionAnswersSet() {
        return questionAnswersSet;
    }

    @Override
    public void setQuestionAnswersSet(QuestionAnswersSet questionAnswersSet) {
        this.questionAnswersSet = questionAnswersSet;
    }
}
