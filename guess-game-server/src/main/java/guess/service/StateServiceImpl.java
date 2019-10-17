package guess.service;

import guess.dao.AnswerDao;
import guess.dao.QuestionDao;
import guess.dao.StateDao;
import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.*;
import guess.domain.question.Question;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.QuestionAnswersSet;
import guess.domain.question.QuestionSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
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
    public void setStartParameters(StartParameters startParameters, HttpSession httpSession) throws QuestionSetNotExistsException {
        stateDao.setStartParameters(startParameters, httpSession);

        QuestionAnswersSet questionAnswersSet = createQuestionAnswersSet(startParameters);
        stateDao.setQuestionAnswersSet(questionAnswersSet, httpSession);

        answerDao.clearAnswerSets(httpSession);
        stateDao.setState(
                questionAnswersSet.getQuestionAnswersList().isEmpty() ?
                        State.RESULT_STATE :
                        (GuessType.GUESS_NAME_TYPE.equals(startParameters.getGuessType()) ?
                                State.GUESS_NAME_STATE :
                                (GuessType.GUESS_PICTURE_TYPE.equals(startParameters.getGuessType()) ?
                                        State.GUESS_PICTURE_STATE :
                                        (GuessType.GUESS_TALK_TYPE.equals(startParameters.getGuessType()) ?
                                                State.GUESS_TALK_STATE :
                                                State.GUESS_SPEAKER_STATE))),
                httpSession);
    }

    @Override
    public State getState(HttpSession httpSession) {
        return stateDao.getState(httpSession);
    }

    @Override
    public void setState(State state, HttpSession httpSession) {
        stateDao.setState(state, httpSession);
    }

    @Override
    public QuestionAnswersSet getQuestionAnswersSet(HttpSession httpSession) {
        return stateDao.getQuestionAnswersSet(httpSession);
    }

    private QuestionAnswersSet createQuestionAnswersSet(StartParameters startParameters) throws QuestionSetNotExistsException {
        // Find unique questions by ids
        List<Question> uniqueQuestions = questionDao.getQuestionByIds(startParameters.getQuestionSetIds(), startParameters.getGuessType());

        // Fill question and answers list
        List<QuestionAnswers> questionAnswersList = new ArrayList<>();
        if (uniqueQuestions.size() >= QuestionAnswersSet.QUESTION_ANSWERS_LIST_SIZE) {
            // Shuffle questions
            List<Question> shuffledQuestions = new ArrayList<>(uniqueQuestions);
            Collections.shuffle(shuffledQuestions);

            // Select first "quantity" elements
            List<Question> selectedShuffledQuestions = shuffledQuestions.subList(
                    0,
                    Math.min(startParameters.getQuantity(), shuffledQuestions.size()));

            // Create question/answers list
            for (Question question : selectedShuffledQuestions) {
                List<Question> shuffledQuestionsWithoutCurrentAndSameQuestions = new ArrayList<>(shuffledQuestions);

                // Remove current and same questions
                shuffledQuestionsWithoutCurrentAndSameQuestions.remove(question);
                shuffledQuestionsWithoutCurrentAndSameQuestions.removeIf(q -> q.isSame(question));

                Collections.shuffle(shuffledQuestionsWithoutCurrentAndSameQuestions);

                Question transformedQuestion = question.transform();

                // Select (QUESTION_ANSWERS_LIST_SIZE - 1) first elements, add current, shuffle
                List<Question> answers = shuffledQuestionsWithoutCurrentAndSameQuestions.subList(
                        0,
                        Math.min(QuestionAnswersSet.QUESTION_ANSWERS_LIST_SIZE - 1, shuffledQuestionsWithoutCurrentAndSameQuestions.size()));
                answers.add(transformedQuestion);
                Collections.shuffle(answers);

                questionAnswersList.add(new QuestionAnswers(transformedQuestion, answers));
            }
        }

        String name;
        String logoFileName;

        // Set name and logo filename
        if (startParameters.getQuestionSetIds().size() == 1) {
            QuestionSet questionSet = questionDao.getQuestionSetById(startParameters.getQuestionSetIds().get(0));
            name = questionSet.getName();
            logoFileName = questionSet.getLogoFileName();
        } else {
            name = String.format("%d selected sets", startParameters.getQuestionSetIds().size());
            logoFileName = null;
        }

        return new QuestionAnswersSet(name, logoFileName, questionAnswersList);
    }
}
