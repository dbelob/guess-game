package acme.guess.service;

import acme.guess.dao.StateDao;
import acme.guess.domain.GuessType;
import acme.guess.domain.StartParameters;
import acme.guess.domain.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * State service implementation.
 */
@Service
public class StateServiceImpl implements StateService {
    private StateDao stateDao;

    @Autowired
    public StateServiceImpl(StateDao stateDao) {
        this.stateDao = stateDao;
    }

    @Override
    public void setStartParameters(StartParameters startParameters) {
        stateDao.setStartParameters(startParameters);
        stateDao.setState(GuessType.GUESS_NAME_TYPE.equals(startParameters.getGuessType()) ?
                State.GUESS_NAME_STATE :
                State.GUESS_PICTURE_STATE);
    }

    @Override
    public State getState() {
        return stateDao.getState();
    }
}
