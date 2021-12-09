package guess.service;

import guess.dao.AnswerDao;
import guess.dao.StateDao;
import guess.domain.GuessMode;
import guess.domain.Identifiable;
import guess.domain.answer.Answer;
import guess.domain.answer.AnswerSet;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.Result;
import guess.domain.question.QuestionAnswers;
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
    private final AnswerDao answerDao;
    private final StateDao stateDao;

    @Autowired
    public AnswerServiceImpl(AnswerDao answerDao, StateDao stateDao) {
        this.answerDao = answerDao;
        this.stateDao = stateDao;
    }

    @Override
    public void setAnswer(int questionIndex, long answerId, HttpSession httpSession) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets(httpSession);

        if (questionIndex < answerSets.size()) {
            var answerSet = answerSets.get(questionIndex);

            if (!answerSet.getYourAnswerIds().containsAll(answerSet.getCorrectAnswerIds()) &&
                    !answerSet.getYourAnswerIds().contains(answerId)) {
                answerSet.getYourAnswerIds().add(answerId);

                if (isSuccess(answerSet.getCorrectAnswerIds(), answerSet.getYourAnswerIds())) {
                    answerSet.setSuccess(true);
                }
            }
        } else {
            var questionAnswersSet = stateDao.getQuestionAnswersSet(httpSession);

            if (questionAnswersSet != null) {
                List<QuestionAnswers> questionAnswersList = questionAnswersSet.questionAnswersList();
                var questionAnswers = questionAnswersList.get(questionIndex);
                List<Long> correctAnswerIds = questionAnswers.correctAnswers().stream()
                        .map(Identifiable::getId)
                        .toList();
                List<Long> yourAnswerIds = new ArrayList<>(Collections.singletonList(answerId));
                boolean isSuccess = isSuccess(correctAnswerIds, yourAnswerIds);

                var answerSet = new AnswerSet(
                        correctAnswerIds,
                        yourAnswerIds,
                        isSuccess);

                answerDao.addAnswerSet(answerSet, httpSession);
            }
        }
    }

    static boolean isSuccess(List<Long> correctAnswerIds, List<Long> yourAnswerIds) {
        return yourAnswerIds.containsAll(correctAnswerIds) && correctAnswerIds.containsAll(yourAnswerIds);
    }

    @Override
    public int getCurrentQuestionIndex(HttpSession httpSession) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets(httpSession);

        if (answerSets.isEmpty()) {
            return 0;
        } else {
            var lastAnswerSet = answerSets.get(answerSets.size() - 1);

            if (lastAnswerSet.isSuccess() || lastAnswerSet.getYourAnswerIds().containsAll(lastAnswerSet.getCorrectAnswerIds())) {
                // Next question
                return answerSets.size();
            } else {
                // Same question
                return answerSets.size() - 1;
            }
        }
    }

    @Override
    public List<Long> getCorrectAnswerIds(int questionIndex, HttpSession httpSession) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets(httpSession);

        if (questionIndex < answerSets.size()) {
            return answerSets.get(questionIndex).getCorrectAnswerIds();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Long> getYourAnswerIds(int questionIndex, HttpSession httpSession) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets(httpSession);

        if (questionIndex < answerSets.size()) {
            return answerSets.get(questionIndex).getYourAnswerIds();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Result getResult(HttpSession httpSession) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets(httpSession);
        var startParameters = stateDao.getStartParameters(httpSession);
        GuessMode guessMode = (startParameters != null) ? startParameters.guessMode() : GuessMode.GUESS_NAME_BY_PHOTO_MODE;

        long correctAnswers = answerSets.stream()
                .filter(AnswerSet::isSuccess)
                .count();
        long wrongAnswers = answerSets.stream()
                .filter(a -> !a.isSuccess())
                .count();
        var questionAnswersSet = stateDao.getQuestionAnswersSet(httpSession);
        long totalQuestions = (questionAnswersSet != null) ? questionAnswersSet.questionAnswersList().size() : 0;
        long skippedAnswers = totalQuestions - (correctAnswers + wrongAnswers);
        float correctPercents = (totalQuestions != 0) ? (float) correctAnswers / totalQuestions : 0;
        float wrongPercents = (totalQuestions != 0) ? (float) wrongAnswers / totalQuestions : 0;
        float skippedPercents = (totalQuestions != 0) ? (float) skippedAnswers / totalQuestions : 0;

        return new Result(correctAnswers, wrongAnswers, skippedAnswers,
                correctPercents, wrongPercents, skippedPercents,
                guessMode);
    }

    @Override
    public List<ErrorDetails> getErrorDetailsList(HttpSession httpSession) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets(httpSession);
        var questionAnswersSet = stateDao.getQuestionAnswersSet(httpSession);
        List<QuestionAnswers> questionAnswersList = (questionAnswersSet != null) ? questionAnswersSet.questionAnswersList() : Collections.emptyList();
        List<ErrorDetails> errorDetailsList = new ArrayList<>();

        for (var i = 0; i < answerSets.size(); i++) {
            var answerSet = answerSets.get(i);

            if (!answerSet.isSuccess()) {
                List<Long> yourAnswersIds = new ArrayList<>(answerSet.getYourAnswerIds());

                List<Answer> yourAnswers = new ArrayList<>();
                for (long yourAnswersId : yourAnswersIds) {
                    Optional<Answer> optionalWrongAnswer = questionAnswersList.get(i).getAvailableAnswersAsList().stream()
                            .filter(a -> a.getId() == yourAnswersId)
                            .findFirst();

                    optionalWrongAnswer.ifPresent(yourAnswers::add);
                }

                if (!yourAnswers.isEmpty()) {
                    errorDetailsList.add(new ErrorDetails(
                            questionAnswersList.get(i).question(),
                            questionAnswersList.get(i).correctAnswers(),
                            questionAnswersList.get(i).getAvailableAnswersAsList(),
                            yourAnswers));
                }
            }
        }

        return errorDetailsList;
    }
}
