package guess.service;

import guess.dao.*;
import guess.domain.*;
import guess.domain.answer.Answer;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.answer.TalkAnswer;
import guess.domain.question.*;
import guess.domain.source.Event;
import guess.domain.source.EventType;
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
    private final StateDao stateDao;
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;
    private final EventTypeDao eventTypeDao;
    private final EventDao eventDao;

    @Autowired
    public StateServiceImpl(StateDao stateDao, QuestionDao questionDao, AnswerDao answerDao, EventTypeDao eventTypeDao, EventDao eventDao) {
        this.stateDao = stateDao;
        this.questionDao = questionDao;
        this.answerDao = answerDao;
        this.eventTypeDao = eventTypeDao;
        this.eventDao = eventDao;
    }

    @Override
    public void setStartParameters(StartParameters startParameters, HttpSession httpSession) {
        stateDao.setStartParameters(startParameters, httpSession);

        QuestionAnswersSet questionAnswersSet = createQuestionAnswersSet(startParameters);
        stateDao.setQuestionAnswersSet(questionAnswersSet, httpSession);

        answerDao.clearAnswerSets(httpSession);
        stateDao.setState(
                questionAnswersSet.getQuestionAnswersList().isEmpty() ?
                        State.RESULT_STATE :
                        getStateByGuessMode(startParameters.getGuessMode()),
                httpSession);
    }

    private State getStateByGuessMode(GuessMode guessMode) {
        switch (guessMode) {
            case GUESS_NAME_BY_PHOTO_MODE:
                return State.GUESS_NAME_BY_PHOTO_STATE;
            case GUESS_PHOTO_BY_NAME_MODE:
                return State.GUESS_PHOTO_BY_NAME_STATE;
            case GUESS_TALK_BY_SPEAKER_MODE:
                return State.GUESS_TALK_BY_SPEAKER_STATE;
            case GUESS_SPEAKER_BY_TALK_MODE:
                return State.GUESS_SPEAKER_BY_TALK_STATE;
            case GUESS_ACCOUNT_BY_SPEAKER_MODE:
                return State.GUESS_ACCOUNT_BY_SPEAKER_STATE;
            default:
                return State.GUESS_SPEAKER_BY_ACCOUNT_STATE;
        }
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

    private QuestionAnswersSet createQuestionAnswersSet(StartParameters startParameters) {
        // Find unique questions by ids
        List<Question> uniqueQuestions = questionDao.getQuestionByIds(startParameters.getEventTypeIds(), startParameters.getEventIds(), startParameters.getGuessMode());

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
                List<Answer> correctAnswers = getCorrectAnswers(question, startParameters.getGuessMode());

                // Correct answers size must be < QUESTION_ANSWERS_LIST_SIZE
                correctAnswers = correctAnswers.subList(
                        0,
                        Math.min(QuestionAnswersSet.QUESTION_ANSWERS_LIST_SIZE - 1, correctAnswers.size()));
                List<Answer> shuffledAllAvailableAnswersWithoutCorrectAnswers = getAllAvailableAnswers(shuffledQuestions, correctAnswers, startParameters.getGuessMode());

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
        if (startParameters.getEventTypeIds().size() == 1) {
            EventType eventType = eventTypeDao.getEventTypeById(startParameters.getEventTypeIds().get(0));
            Event event;

            if (eventType.isEventTypeConference() &&
                    (startParameters.getEventIds().size() == 1) &&
                    ((event = eventDao.getEventById(startParameters.getEventIds().get(0))) != null)) {
                name = event.getName();
            } else {
                name = eventType.getName();
            }

            logoFileName = eventType.getLogoFileName();
        } else {
            final String SELECTED_EVENT_TYPES = "selectedEventTypes";

            name = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), String.format(
                            LocalizationUtils.getResourceString(SELECTED_EVENT_TYPES, Language.ENGLISH),
                            startParameters.getEventTypeIds().size())),
                    new LocaleItem(Language.RUSSIAN.getCode(), String.format(
                            LocalizationUtils.getResourceString(SELECTED_EVENT_TYPES, Language.RUSSIAN),
                            startParameters.getEventTypeIds().size())));
            logoFileName = null;
        }

        return new QuestionAnswersSet(name, logoFileName, questionAnswersList);
    }

    private List<Answer> getCorrectAnswers(Question question, GuessMode guessMode) {
        switch (guessMode) {
            case GUESS_NAME_BY_PHOTO_MODE:
            case GUESS_PHOTO_BY_NAME_MODE:
            case GUESS_ACCOUNT_BY_SPEAKER_MODE:
            case GUESS_SPEAKER_BY_ACCOUNT_MODE:
                return Collections.singletonList(new SpeakerAnswer(((SpeakerQuestion) question).getSpeaker()));
            case GUESS_TALK_BY_SPEAKER_MODE:
                return Collections.singletonList(new TalkAnswer(((TalkQuestion) question).getTalk()));
            case GUESS_SPEAKER_BY_TALK_MODE:
                return ((TalkQuestion) question).getSpeakers().stream()
                        .map(SpeakerAnswer::new)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException(String.format("Unknown guess mode: %s", guessMode));
        }
    }

    private List<Answer> getAllAvailableAnswers(List<Question> questions, List<Answer> correctAnswers, GuessMode guessMode) {
        switch (guessMode) {
            case GUESS_NAME_BY_PHOTO_MODE:
            case GUESS_PHOTO_BY_NAME_MODE:
            case GUESS_ACCOUNT_BY_SPEAKER_MODE:
            case GUESS_SPEAKER_BY_ACCOUNT_MODE:
                return questions.stream()
                        .map(q -> new SpeakerAnswer(((SpeakerQuestion) q).getSpeaker()))
                        .collect(Collectors.toList());
            case GUESS_TALK_BY_SPEAKER_MODE:
                Talk correctAnswerTalk = ((TalkAnswer) correctAnswers.get(0)).getTalk();

                return questions.stream()
                        .map(q -> ((TalkQuestion) q).getTalk())
                        .filter(t -> (t.getId() == correctAnswerTalk.getId()) || !t.getSpeakerIds().containsAll(correctAnswerTalk.getSpeakerIds()))
                        .map(TalkAnswer::new)
                        .collect(Collectors.toList());
            case GUESS_SPEAKER_BY_TALK_MODE:
                return questions.stream()
                        .map(q -> ((TalkQuestion) q).getTalk().getSpeakers())
                        .flatMap(Collection::stream)
                        .distinct()
                        .map(SpeakerAnswer::new)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException(String.format("Unknown guess mode: %s", guessMode));
        }
    }
}
