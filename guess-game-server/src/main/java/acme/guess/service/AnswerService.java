package acme.guess.service;

import acme.guess.domain.AnswerSet;

import java.util.List;

/**
 * Answer service.
 */
public interface AnswerService {
    List<AnswerSet> getAnswerSets();

    void addAnswerSet(AnswerSet answerSet);
}
