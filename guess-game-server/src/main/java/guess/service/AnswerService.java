package guess.service;

import guess.domain.Result;

import java.util.Set;

/**
 * Answer service.
 */
public interface AnswerService {
    void setAnswer(int questionIndex, long answerId);

    int getCurrentQuestionIndex();

    Set<Long> getInvalidAnswerIds(int questionIndex);

    Result getResult();
}
