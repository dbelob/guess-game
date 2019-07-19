package acme.guess.dao;

import acme.guess.domain.StartParameters;
import acme.guess.domain.State;
import org.springframework.stereotype.Repository;

/**
 * State DAO implementation.
 */
@Repository
public class StateDaoImpl implements StateDao {
    private State state = State.START_STATE;
    private StartParameters startParameters;

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
}
