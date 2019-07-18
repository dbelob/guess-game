package acme.guess.service;

import acme.guess.dao.exception.QuestionSetNotExistsException;
import acme.guess.domain.QuestionSet;

import java.util.List;

/**
 * Question service.
 */
public interface QuestionService {
    List<QuestionSet> getQuestionSets();

    List<Integer> getQuantities(long questionSetId) throws QuestionSetNotExistsException;
}
