package acme.guess.service;

import acme.guess.domain.StartParameters;

/**
 * State service.
 */
public interface StateService {
    void setStartParameters(StartParameters startParameters);
}
