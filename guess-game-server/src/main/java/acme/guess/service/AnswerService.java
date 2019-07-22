package acme.guess.service;

import acme.guess.domain.AnswerSet;

import java.util.List;
import java.util.Set;

/**
 * Answer service.
 */
public interface AnswerService {
    List<AnswerSet> getAnswerSets();

    void addAnswerSet(AnswerSet answerSet);

    int getCurrentQuestionIndex();

    Set<Long> getInvalidAnswerIds(int index);
}
