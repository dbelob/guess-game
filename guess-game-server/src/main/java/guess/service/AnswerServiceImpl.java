package guess.service;

import guess.dao.AnswerDao;
import guess.dao.StateDao;
import guess.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void setAnswer(int questionIndex, long answerId) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets();

        if (questionIndex < answerSets.size()) {
            answerSets.get(questionIndex).getAnswers().add(answerId);
        } else {
            QuestionAnswersSet questionAnswersSet = stateDao.getQuestionAnswersSet();
            List<QuestionAnswers> questionAnswersList = questionAnswersSet.getQuestionAnswersList();
            QuestionAnswers questionAnswers = questionAnswersList.get(questionIndex);
            List<Long> answers = new ArrayList<>(Collections.singletonList(answerId));
            AnswerSet answerSet = new AnswerSet(
                    questionAnswers.getQuestion().getId(),
                    answers,
                    (questionAnswers.getQuestion().getId() == answerId));

            answerDao.addAnswerSet(answerSet);
        }
    }

    @Override
    public int getCurrentQuestionIndex() {
        List<AnswerSet> answerSets = answerDao.getAnswerSets();

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
    public List<Long> getWrongAnswerIds(int questionIndex) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets();

        if (questionIndex < answerSets.size()) {
            return answerSets.get(questionIndex).getAnswers();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Result getResult() {
        List<AnswerSet> answerSets = answerDao.getAnswerSets();
        StartParameters startParameters = stateDao.getStartParameters();

        long correctAnswers = answerSets.stream()
                .filter(AnswerSet::isSuccess)
                .count();
        long wrongAnswers = answerSets.stream()
                .filter(a -> !a.isSuccess())
                .count();
        QuestionAnswersSet questionAnswersSet = stateDao.getQuestionAnswersSet();
        long totalQuestions = questionAnswersSet.getQuestionAnswersList().size();
        long skippedAnswers = totalQuestions - (correctAnswers + wrongAnswers);
        float correctPercents = (totalQuestions != 0) ? (float) correctAnswers / totalQuestions : 0;
        float wrongPercents = (totalQuestions != 0) ? (float) wrongAnswers / totalQuestions : 0;
        float skippedPercents = (totalQuestions != 0) ? (float) skippedAnswers / totalQuestions : 0;

        return new Result(correctAnswers, wrongAnswers, skippedAnswers,
                correctPercents, wrongPercents, skippedPercents,
                startParameters.getGuessType());
    }

    @Override
    public List<ErrorDetails> getErrorDetailsList() {
        List<AnswerSet> answerSets = answerDao.getAnswerSets();
        List<QuestionAnswers> questionAnswersList = stateDao.getQuestionAnswersSet().getQuestionAnswersList();
        List<ErrorDetails> errorDetailsList = new ArrayList<>();

        for (int i = 0; i < answerSets.size(); i++) {
            AnswerSet answerSet = answerSets.get(i);

            if (!answerSet.isSuccess()) {
                List<Long> wrongAnswersIds = new ArrayList<>(answerSet.getAnswers());
                wrongAnswersIds.remove(answerSet.getQuestionId());

                List<Question> wrongAnswers = new ArrayList<>();
                for (long wrongAnswersId : wrongAnswersIds) {
                    Optional<Question> optionalWrongAnswer = questionAnswersList.get(i).getAnswers().stream()
                            .filter(q -> q.getId() == wrongAnswersId)
                            .findFirst();

                    optionalWrongAnswer.ifPresent(wrongAnswers::add);
                }

                if (!wrongAnswers.isEmpty()) {
                    errorDetailsList.add(new ErrorDetails(
                            questionAnswersList.get(i).getQuestion(),
                            wrongAnswers));
                }
            }
        }

        return errorDetailsList;
    }
}
