package guess.service;

import guess.domain.answer.ErrorDetails;
import guess.domain.answer.Result;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Answer service.
 */
public interface AnswerService {
    void setAnswer(int questionIndex, long answerId, HttpSession httpSession);

    int getCurrentQuestionIndex(HttpSession httpSession);

    List<Long> getWrongAnswerIds(int questionIndex, HttpSession httpSession);

    Result getResult(HttpSession httpSession);

    List<ErrorDetails> getErrorDetailsList(HttpSession httpSession);
}
