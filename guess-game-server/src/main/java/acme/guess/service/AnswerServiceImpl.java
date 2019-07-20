package acme.guess.service;

import acme.guess.dao.AnswerDao;
import acme.guess.domain.AnswerSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
