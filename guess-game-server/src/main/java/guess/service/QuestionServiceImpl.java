package guess.service;

import guess.dao.EventTypeDao;
import guess.dao.QuestionDao;
import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessType;
import guess.domain.question.Question;
import guess.domain.question.QuestionSet;
import guess.domain.source.EventType;
import guess.util.QuestionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Question service implementation.
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionDao questionDao;
    private final EventTypeDao eventTypeDao;

    @Autowired
    public QuestionServiceImpl(QuestionDao questionDao, EventTypeDao eventTypeDao) {
        this.questionDao = questionDao;
        this.eventTypeDao = eventTypeDao;
    }

    @Override
    public List<QuestionSet> getQuestionSets() {
        return questionDao.getQuestionSets();
    }

    @Override
    public Long getDefaultQuestionSetId(LocalDate date) {
        return questionDao.getDefaultQuestionSetId(date);
    }

    @Override
    public List<EventType> getEventTypes() {
        return eventTypeDao.getEventTypes();
    }

    @Override
    public List<Integer> getQuantities(List<Long> questionSetIds, GuessType guessType) throws QuestionSetNotExistsException {
        List<Question> uniqueQuestions = questionDao.getQuestionByIds(questionSetIds, guessType);

        return QuestionUtils.getQuantities(uniqueQuestions.size());
    }
}
