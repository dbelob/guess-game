package guess.service;

import guess.domain.ErrorDetails;
import guess.domain.Result;

import java.util.List;
import java.util.Set;

/**
 * Answer service.
 */
public interface AnswerService {
    void setAnswer(int questionIndex, long answerId);

    int getCurrentQuestionIndex();

    List<Long> getWrongAnswerIds(int questionIndex);

    Result getResult();

    List<ErrorDetails> getErrorDetailsList();
}
