package guess.service;

import guess.dao.QuestionDao;
import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.Question;
import guess.domain.QuestionSet;
import guess.util.QuestionUtils;
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

    @Override
    public List<Integer> getQuantities(Long[] questionSetIds) throws QuestionSetNotExistsException {
        List<Question> uniqueQuestions = questionDao.getQuestionByIds(questionSetIds);

        return QuestionUtils.getQuantities(uniqueQuestions.size());
    }
}
