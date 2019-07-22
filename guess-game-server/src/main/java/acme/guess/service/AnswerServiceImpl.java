package acme.guess.service;

import acme.guess.dao.AnswerDao;
import acme.guess.domain.AnswerSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Answer service implementation.
 */
@Service
public class AnswerServiceImpl implements AnswerService {
    private AnswerDao answerDao;

    @Autowired
    public AnswerServiceImpl(AnswerDao answerDao) {
        this.answerDao = answerDao;
    }

    @Override
    public List<AnswerSet> getAnswerSets() {
        return answerDao.getAnswerSets();
    }

    @Override
    public void addAnswerSet(AnswerSet answerSet) {
        answerDao.addAnswerSet(answerSet);
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
    public Set<Long> getInvalidAnswerIds(int index) {
        List<AnswerSet> answerSets = answerDao.getAnswerSets();

        if (index < answerSets.size()) {
            return answerSets.get(index).getAnswers();
        } else {
            return Collections.emptySet();
        }
    }
}
