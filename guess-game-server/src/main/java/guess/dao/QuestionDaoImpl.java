package guess.dao;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.Question;
import guess.domain.QuestionSet;
import guess.util.QuestionUtils;
import guess.util.YamlUtils;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Question DAO implementation.
 */
@Repository
public class QuestionDaoImpl implements QuestionDao {
    private static String QUESTIONS_DIRECTORY_NAME = "questions";

    private final List<QuestionSet> questionSets;

    public QuestionDaoImpl() throws IOException {
        this.questionSets = YamlUtils.readQuestionSets(QUESTIONS_DIRECTORY_NAME);
    }

    @Override
    public List<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    @Override
    public QuestionSet getQuestionSetById(long id) throws QuestionSetNotExistsException {
        Optional<QuestionSet> optional = questionSets.stream()
                .filter(q -> q.getId() == id)
                .findFirst();

        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new QuestionSetNotExistsException();
        }
    }

    @Override
    public List<Question> getQuestionByIds(List<Long> ids) throws QuestionSetNotExistsException {
        List<Question> questions = new ArrayList<>();

        for (Long id : ids) {
            QuestionSet questionSet = getQuestionSetById(id);
            questions.addAll(questionSet.getQuestions());
        }

        return QuestionUtils.removeDuplicatesByFileName(questions);
    }
}
