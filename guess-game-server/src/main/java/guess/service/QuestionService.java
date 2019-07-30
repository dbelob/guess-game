package guess.service;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.QuestionSet;

import java.util.List;

/**
 * Question service.
 */
public interface QuestionService {
    List<QuestionSet> getQuestionSets();

    List<Integer> getQuantities(Long[] questionSetIds) throws QuestionSetNotExistsException;
}
