package guess.service;

import guess.dao.AnswerDao;
import guess.dao.StateDao;
import guess.domain.GuessType;
import guess.domain.StartParameters;
import guess.domain.answer.Answer;
import guess.domain.answer.AnswerSet;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.Result;
import guess.domain.question.QuestionAnswers2;
import guess.domain.question.QuestionAnswersSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Answer service implementation.
 */
@Service
public class AnswerServiceImpl implements AnswerService {
    private AnswerDao answerDao;
    private StateDao stateDao;

    @Autowired
    public AnswerServiceImpl(AnswerDao answerDao, StateDao stateDao) {
        this.answerDao = answerDao;
        this.stateDao = stateDao;
    }

    @Override
    public void setAnswer(int questionIndex, long answerId, HttpSession httpSession) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets(httpSession);

        if (questionIndex < answerSets.size()) {
            answerSets.get(questionIndex).getAnswers().add(answerId);
        } else {
            QuestionAnswersSet questionAnswersSet = stateDao.getQuestionAnswersSet(httpSession);

            if (questionAnswersSet != null) {
                List<QuestionAnswers2> questionAnswersList = questionAnswersSet.getQuestionAnswersList();
                QuestionAnswers2 questionAnswers = questionAnswersList.get(questionIndex);
                List<Long> answers = new ArrayList<>(Collections.singletonList(answerId));

                boolean isSuccess = questionAnswers.getCorrectAnswers().stream()
                        .anyMatch(e -> e.getId() == answerId);

                AnswerSet answerSet = new AnswerSet(
                        questionAnswers.getQuestion().getId(),
                        answers,
                        isSuccess);

                answerDao.addAnswerSet(answerSet, httpSession);
            }
        }
    }

    @Override
    public int getCurrentQuestionIndex(HttpSession httpSession) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets(httpSession);

        if (answerSets.size() <= 0) {
            return 0;
        } else {
            AnswerSet lastAnswerSet = answerSets.get(answerSets.size() - 1);

            if (lastAnswerSet.isSuccess() || lastAnswerSet.getAnswers().contains(lastAnswerSet.getQuestionId())) {
                // Next question
                return answerSets.size();
            } else {
                // Same question
                return answerSets.size() - 1;
            }
        }
    }

    @Override
    public List<Long> getWrongAnswerIds(int questionIndex, HttpSession httpSession) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets(httpSession);

        if (questionIndex < answerSets.size()) {
            return answerSets.get(questionIndex).getAnswers();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Result getResult(HttpSession httpSession) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets(httpSession);
        StartParameters startParameters = stateDao.getStartParameters(httpSession);
        GuessType guessType = (startParameters != null) ? startParameters.getGuessType() : GuessType.GUESS_NAME_TYPE;

        long correctAnswers = answerSets.stream()
                .filter(AnswerSet::isSuccess)
                .count();
        long wrongAnswers = answerSets.stream()
                .filter(a -> !a.isSuccess())
                .count();
        QuestionAnswersSet questionAnswersSet = stateDao.getQuestionAnswersSet(httpSession);
        long totalQuestions = (questionAnswersSet != null) ? questionAnswersSet.getQuestionAnswersList().size() : 0;
        long skippedAnswers = totalQuestions - (correctAnswers + wrongAnswers);
        float correctPercents = (totalQuestions != 0) ? (float) correctAnswers / totalQuestions : 0;
        float wrongPercents = (totalQuestions != 0) ? (float) wrongAnswers / totalQuestions : 0;
        float skippedPercents = (totalQuestions != 0) ? (float) skippedAnswers / totalQuestions : 0;

        return new Result(correctAnswers, wrongAnswers, skippedAnswers,
                correctPercents, wrongPercents, skippedPercents,
                guessType);
    }

    @Override
    public List<ErrorDetails> getErrorDetailsList(HttpSession httpSession) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets(httpSession);
        QuestionAnswersSet questionAnswersSet = stateDao.getQuestionAnswersSet(httpSession);
        List<QuestionAnswers2> questionAnswersList = (questionAnswersSet != null) ? questionAnswersSet.getQuestionAnswersList() : Collections.emptyList();
        List<ErrorDetails> errorDetailsList = new ArrayList<>();

        for (int i = 0; i < answerSets.size(); i++) {
            AnswerSet answerSet = answerSets.get(i);

            if (!answerSet.isSuccess()) {
                List<Long> wrongAnswersIds = new ArrayList<>(answerSet.getAnswers());
                wrongAnswersIds.remove(answerSet.getQuestionId());

                List<Answer> wrongAnswers = new ArrayList<>();
                for (long wrongAnswersId : wrongAnswersIds) {
                    Optional<Answer> optionalWrongAnswer = questionAnswersList.get(i).getAvailableAnswers().stream()
                            .filter(q -> q.getId() == wrongAnswersId)
                            .findFirst();

                    optionalWrongAnswer.ifPresent(wrongAnswers::add);
                }

                if (!wrongAnswers.isEmpty()) {
                    errorDetailsList.add(new ErrorDetails(
                            questionAnswersList.get(i).getQuestion(),
                            questionAnswersList.get(i).getAvailableAnswers(),
                            wrongAnswers));
                }
            }
        }

        return errorDetailsList;
    }
}
