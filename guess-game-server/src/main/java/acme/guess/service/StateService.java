package acme.guess.service;

import acme.guess.domain.StartParameters;
import acme.guess.domain.State;

/**
 * State service.
 */
public interface StateService {
    void setStartParameters(StartParameters startParameters);

    State getState();
}
