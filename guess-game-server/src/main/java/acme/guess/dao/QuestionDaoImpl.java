package acme.guess.dao;

import acme.guess.domain.QuestionSet;
import acme.guess.util.YamlUtils;

import java.util.List;

public class QuestionDaoImpl implements QuestionDao {
    private final List<QuestionSet> questionSets;

    public QuestionDaoImpl() {
        this.questionSets = YamlUtils.readQuestionSets();
    }

    @Override
    public List<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    public static void main(String [] args) {
        QuestionDao questionDao = new QuestionDaoImpl();
        List<QuestionSet> questionSets = questionDao.getQuestionSets();
    }
}