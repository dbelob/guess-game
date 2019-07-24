package guess.service;

import guess.dao.AnswerDao;
import guess.dao.QuestionDao;
import guess.dao.StateDao;
import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * State service implementation.
 */
@Service
public class StateServiceImpl implements StateService {
    private StateDao stateDao;
    private QuestionDao questionDao;
    private AnswerDao answerDao;

    @Autowired
    public StateServiceImpl(StateDao stateDao, QuestionDao questionDao, AnswerDao answerDao) {
        this.stateDao = stateDao;
        this.questionDao = questionDao;
        this.answerDao = answerDao;
    }

    @Override
    public void setStartParameters(StartParameters startParameters) throws QuestionSetNotExistsException {
        stateDao.setStartParameters(startParameters);

        QuestionAnswersSet questionAnswersSet = createQuestionAnswersSet(startParameters);
        stateDao.setQuestionAnswersSet(questionAnswersSet);

        answerDao.clearAnswerSets();
        stateDao.setState(GuessType.GUESS_NAME_TYPE.equals(startParameters.getGuessType()) ?
                State.GUESS_NAME_STATE :
                State.GUESS_PICTURE_STATE);
    }

    @Override
    public State getState() {
        return stateDao.getState();
    }

    @Override
    public void setState(State state) {
        stateDao.setState(state);
    }

    @Override
    public QuestionAnswersSet getQuestionAnswersSet() {
        return stateDao.getQuestionAnswersSet();
    }

    private QuestionAnswersSet createQuestionAnswersSet(StartParameters startParameters) throws QuestionSetNotExistsException {
        // Find question set by id
        QuestionSet questionSet = questionDao.getQuestionSetById(startParameters.getQuestionSetId());

        // Shuffle questions
        List<Question> shuffledQuestions = new ArrayList<>(questionSet.getQuestions());
        Collections.shuffle(shuffledQuestions);

        // Select first "quantity" elements
        List<Question> selectedShuffledQuestions = shuffledQuestions.subList(
                0,
                Math.min(startParameters.getQuantity(), shuffledQuestions.size()));

        // Create question/answers list
        List<QuestionAnswers> questionAnswersList = new ArrayList<>();
        for (Question question : selectedShuffledQuestions) {
            List<Question> shuffledQuestionsWithoutCurrentQuestion = new ArrayList<>(shuffledQuestions);
            shuffledQuestionsWithoutCurrentQuestion.remove(question);
            Collections.shuffle(shuffledQuestionsWithoutCurrentQuestion);

            // Select 3 first elements, add current, shuffle
            List<Question> answers = shuffledQuestionsWithoutCurrentQuestion.subList(
                    0,
                    Math.min(QuestionAnswersSet.QUESTION_ANSWERS_LIST_SIZE - 1, shuffledQuestionsWithoutCurrentQuestion.size()));
            answers.add(question);
            Collections.shuffle(answers);

            questionAnswersList.add(new QuestionAnswers(question, answers));
        }

        return new QuestionAnswersSet(questionSet.getName(), questionSet.getDirectoryName(), questionSet.getLogoFileName(), questionAnswersList);
    }
}
