package guess.service;

import guess.dao.AnswerDao;
import guess.dao.QuestionDao;
import guess.dao.StateDao;
import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.*;
import guess.domain.answer.Answer;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.answer.TalkAnswer;
import guess.domain.question.*;
import guess.domain.source.LocaleItem;
import guess.domain.source.Talk;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
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
                List<Answer> correctAnswers = getCorrectAnswers(question, startParameters.getGuessType());

                // Correct answers size must be < QUESTION_ANSWERS_LIST_SIZE
                correctAnswers = correctAnswers.subList(
                        0,
                        Math.min(QuestionAnswersSet.QUESTION_ANSWERS_LIST_SIZE - 1, correctAnswers.size()));
                List<Answer> shuffledAllAvailableAnswersWithoutCorrectAnswers = getAllAvailableAnswers(shuffledQuestions, correctAnswers, startParameters.getGuessType());

                shuffledAllAvailableAnswersWithoutCorrectAnswers.removeAll(correctAnswers);
                Collections.shuffle(shuffledAllAvailableAnswersWithoutCorrectAnswers);

                // Select (QUESTION_ANSWERS_LIST_SIZE - correctAnswers.size()) first elements, add correct answers, shuffle
                List<Answer> availableAnswers = shuffledAllAvailableAnswersWithoutCorrectAnswers.subList(
                        0,
                        Math.min(QuestionAnswersSet.QUESTION_ANSWERS_LIST_SIZE - correctAnswers.size(), shuffledAllAvailableAnswersWithoutCorrectAnswers.size()));
                availableAnswers.addAll(correctAnswers);
                Collections.shuffle(availableAnswers);

                questionAnswersList.add(new QuestionAnswers(
                        question,
                        correctAnswers,
                        new Quadruple<>(availableAnswers.get(0), availableAnswers.get(1), availableAnswers.get(2), availableAnswers.get(3))));
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

        return new QuestionAnswersSet(name, logoFileName, questionAnswersList);
    }

    private List<Answer> getCorrectAnswers(Question question, GuessType guessType) {
        switch (guessType) {
            case GUESS_NAME_TYPE:
            case GUESS_PICTURE_TYPE:
                return Collections.singletonList(new SpeakerAnswer(((SpeakerQuestion) question).getSpeaker()));
            case GUESS_TALK_TYPE:
                return Collections.singletonList(new TalkAnswer(((TalkQuestion) question).getTalk()));
            case GUESS_SPEAKER_TYPE:
                return ((TalkQuestion) question).getSpeakers().stream()
                        .map(SpeakerAnswer::new)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException(String.format("Unknown guess type: %s", guessType));
        }
    }

    private List<Answer> getAllAvailableAnswers(List<Question> questions, List<Answer> correctAnswers, GuessType guessType) {
        switch (guessType) {
            case GUESS_NAME_TYPE:
            case GUESS_PICTURE_TYPE:
                return questions.stream()
                        .map(q -> new SpeakerAnswer(((SpeakerQuestion) q).getSpeaker()))
                        .collect(Collectors.toList());
            case GUESS_TALK_TYPE:
                Talk correctAnswerTalk = ((TalkAnswer) correctAnswers.get(0)).getTalk();

                return questions.stream()
                        .map(q -> ((TalkQuestion) q).getTalk())
                        .filter(t -> (t.getId() == correctAnswerTalk.getId()) || !t.getSpeakerIds().containsAll(correctAnswerTalk.getSpeakerIds()))
                        .map(TalkAnswer::new)
                        .collect(Collectors.toList());
            case GUESS_SPEAKER_TYPE:
                return questions.stream()
                        .map(q -> ((TalkQuestion) q).getTalk().getSpeakers())
                        .flatMap(Collection::stream)
                        .distinct()
                        .map(SpeakerAnswer::new)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException(String.format("Unknown guess type: %s", guessType));
        }
    }
}
