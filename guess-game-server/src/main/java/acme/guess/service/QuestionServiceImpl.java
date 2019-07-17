package acme.guess.service;

import acme.guess.dao.QuestionDao;
import acme.guess.domain.QuestionSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Question service implementation.
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    private QuestionDao questionDao;

    @Autowired
    public QuestionServiceImpl(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public List<QuestionSet> getQuestionSets() {
        List<QuestionSet> questionSets = questionDao.getQuestionSets();
        questionSets.sort(Comparator.comparing(QuestionSet::getName));

        return questionSets;
    }
}
