package guess.service;

import guess.dao.AnswerDao;
import guess.dao.QuestionDao;
import guess.dao.StateDao;
import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessType;
import guess.domain.Language;
import guess.domain.StartParameters;
import guess.domain.State;
import guess.domain.question.*;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        List<QuestionAnswers> questionAnswersList = new ArrayList<>();      //TODO: delete
        List<QuestionAnswers2> questionAnswersList2 = new ArrayList<>();
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
                List<Question> shuffledQuestionsWithoutCurrentAndSimilarQuestions = new ArrayList<>(shuffledQuestions);

                // Remove current and same questions
                shuffledQuestionsWithoutCurrentAndSimilarQuestions.remove(question);
                shuffledQuestionsWithoutCurrentAndSimilarQuestions.removeIf(q -> q.isSimilar(question));

                Collections.shuffle(shuffledQuestionsWithoutCurrentAndSimilarQuestions);

                Question transformedQuestion = question.transform();

                // Select (QUESTION_ANSWERS_LIST_SIZE - 1) first elements, add current, shuffle
                List<Question> answers = shuffledQuestionsWithoutCurrentAndSimilarQuestions.subList(
                        0,
                        Math.min(QuestionAnswersSet.QUESTION_ANSWERS_LIST_SIZE - 1, shuffledQuestionsWithoutCurrentAndSimilarQuestions.size()));
                answers.add(transformedQuestion);
                Collections.shuffle(answers);

                questionAnswersList.add(new QuestionAnswers(transformedQuestion, answers)); //TODO: delete
                questionAnswersList2.add(createQuestionAnswers2(transformedQuestion, answers, startParameters.getGuessType()));
            }
        }

        List<LocaleItem> name;
        String logoFileName;

        // Set name and logo filename
        if (startParameters.getQuestionSetIds().size() == 1) {
            QuestionSet questionSet = questionDao.getQuestionSetById(startParameters.getQuestionSetIds().get(0));
            name = questionSet.getName();
            logoFileName = questionSet.getLogoFileName();
        } else {
            final String SELECTED_SETS = "selectedSets";

            name = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), String.format(
                            LocalizationUtils.getResourceString(SELECTED_SETS, Language.ENGLISH),
                            startParameters.getQuestionSetIds().size())),
                    new LocaleItem(Language.RUSSIAN.getCode(), String.format(
                            LocalizationUtils.getResourceString(SELECTED_SETS, Language.RUSSIAN),
                            startParameters.getQuestionSetIds().size())));
            logoFileName = null;
        }

        return new QuestionAnswersSet(name, logoFileName, questionAnswersList, questionAnswersList2);
    }

    private QuestionAnswers2 createQuestionAnswers2(Question question, List<Question> availableAnswers, GuessType guessType) {
        switch (guessType) {
            case GUESS_NAME_TYPE:
            case GUESS_PICTURE_TYPE:
                Speaker speaker = ((SpeakerQuestion) question).getSpeaker();

                return new QuestionAnswers2<>(
                        speaker,
                        Collections.singletonList(speaker),
                        availableAnswers.stream()
                                .map(q -> ((SpeakerQuestion) q).getSpeaker())
                                .collect(Collectors.toList()));
            case GUESS_TALK_TYPE:
                return new QuestionAnswers2<>(
                        ((TalkQuestion) question).getSpeakers().get(0),
                        Collections.singletonList(((TalkQuestion) question).getTalk()),
                        availableAnswers.stream()
                                .map(q -> ((TalkQuestion) q).getTalk())
                                .collect(Collectors.toList()));
            case GUESS_SPEAKER_TYPE:
                return new QuestionAnswers2<>(
                        ((TalkQuestion) question).getTalk(),
                        ((TalkQuestion) question).getSpeakers(),
                        availableAnswers.stream()
                                .map(q -> ((TalkQuestion) q).getSpeakers().get(0))      //TODO: is it right?
                                .collect(Collectors.toList()));
            default:
                throw new IllegalArgumentException(String.format("Unknown guess type: %s", guessType));
        }
    }
}
