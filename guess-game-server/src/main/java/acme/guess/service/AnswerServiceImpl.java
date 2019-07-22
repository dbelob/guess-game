package acme.guess.service;

import acme.guess.dao.AnswerDao;
import acme.guess.dao.StateDao;
import acme.guess.domain.AnswerSet;
import acme.guess.domain.QuestionAnswers;
import acme.guess.domain.QuestionAnswersSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            Set<Long> answers = new HashSet<>(Collections.singletonList(answerId));
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
    public Set<Long> getInvalidAnswerIds(int questionIndex) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets();

        if (questionIndex < answerSets.size()) {
            return answerSets.get(questionIndex).getAnswers();
        } else {
            return Collections.emptySet();
        }
    }
}
