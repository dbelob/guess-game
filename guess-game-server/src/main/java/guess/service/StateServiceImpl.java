package guess.service;

import guess.dao.*;
import guess.domain.*;
import guess.domain.answer.*;
import guess.domain.question.*;
import guess.domain.source.Company;
import guess.domain.source.Event;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
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
    private static final String UNKNOWN_GUESS_MODE_TEXT = "Unknown guess mode: %s";

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

        var questionAnswersSet = createQuestionAnswersSet(startParameters);
        stateDao.setQuestionAnswersSet(questionAnswersSet, httpSession);

        answerDao.clearAnswerSets(httpSession);
        stateDao.setGameState(
                questionAnswersSet.questionAnswersList().isEmpty() ?
                        GameState.RESULT_STATE :
                        getStateByGuessMode(startParameters.guessMode()),
                httpSession);
    }

    @Override
    public void deleteStartParameters(HttpSession httpSession) {
        stateDao.clearStartParameters(httpSession);
        stateDao.clearQuestionAnswersSet(httpSession);
        answerDao.clearAnswerSets(httpSession);
        stateDao.setGameState(GameState.START_STATE, httpSession);
    }

    GameState getStateByGuessMode(GuessMode guessMode) {
        if (GuessMode.GUESS_NAME_BY_PHOTO_MODE.equals(guessMode)) {
            return GameState.GUESS_NAME_BY_PHOTO_STATE;
        } else if (GuessMode.GUESS_PHOTO_BY_NAME_MODE.equals(guessMode)) {
            return GameState.GUESS_PHOTO_BY_NAME_STATE;
        } else if (GuessMode.GUESS_TALK_BY_SPEAKER_MODE.equals(guessMode)) {
            return GameState.GUESS_TALK_BY_SPEAKER_STATE;
        } else if (GuessMode.GUESS_SPEAKER_BY_TALK_MODE.equals(guessMode)) {
            return GameState.GUESS_SPEAKER_BY_TALK_STATE;
        } else if (GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode)) {
            return GameState.GUESS_COMPANY_BY_SPEAKER_STATE;
        } else if (GuessMode.GUESS_SPEAKER_BY_COMPANY_MODE.equals(guessMode)) {
            return GameState.GUESS_SPEAKER_BY_COMPANY_STATE;
        } else if (GuessMode.GUESS_ACCOUNT_BY_SPEAKER_MODE.equals(guessMode)) {
            return GameState.GUESS_ACCOUNT_BY_SPEAKER_STATE;
        } else if (GuessMode.GUESS_SPEAKER_BY_ACCOUNT_MODE.equals(guessMode)) {
            return GameState.GUESS_SPEAKER_BY_ACCOUNT_STATE;
        } else if (GuessMode.GUESS_TAG_CLOUD_BY_SPEAKER_MODE.equals(guessMode)) {
            return GameState.GUESS_TAG_CLOUD_BY_SPEAKER_STATE;
        } else if (GuessMode.GUESS_SPEAKER_BY_TAG_CLOUD_MODE.equals(guessMode)) {
            return GameState.GUESS_SPEAKER_BY_TAG_CLOUD_STATE;
        } else {
            throw new IllegalArgumentException(String.format(UNKNOWN_GUESS_MODE_TEXT, guessMode));
        }
    }

    @Override
    public GameState getState(HttpSession httpSession) {
        return stateDao.getGameState(httpSession);
    }

    @Override
    public void setState(GameState state, HttpSession httpSession) {
        stateDao.setGameState(state, httpSession);
    }

    @Override
    public QuestionAnswersSet getQuestionAnswersSet(HttpSession httpSession) {
        return stateDao.getQuestionAnswersSet(httpSession);
    }

    QuestionAnswersSet createQuestionAnswersSet(StartParameters startParameters) {
        // Find unique questions by ids
        List<Question> uniqueQuestions = questionDao.getQuestionByIds(startParameters.eventTypeIds(), startParameters.eventIds(), startParameters.guessMode());

        // Fill question and answers list
        List<QuestionAnswers> questionAnswersList = new ArrayList<>();
        if (uniqueQuestions.size() >= QuestionAnswersSet.QUESTION_ANSWERS_LIST_SIZE) {
            // Shuffle questions
            List<Question> shuffledQuestions = new ArrayList<>(uniqueQuestions);
            Collections.shuffle(shuffledQuestions);

            // Select first "quantity" elements
            List<Question> selectedShuffledQuestions = shuffledQuestions.subList(
                    0,
                    Math.min(startParameters.quantity(), shuffledQuestions.size()));

            Map<Long, Answer> answerCache = new HashMap<>();

            // Create question/answers list
            for (Question question : selectedShuffledQuestions) {
                List<Answer> correctAnswers = new ArrayList<>(getCorrectAnswers(question, startParameters.guessMode(), answerCache));
                Collections.shuffle(correctAnswers);

                // Correct answers size must be < QUESTION_ANSWERS_LIST_SIZE
                correctAnswers = correctAnswers.subList(
                        0,
                        Math.min(QuestionAnswersSet.QUESTION_ANSWERS_LIST_SIZE - 1, correctAnswers.size()));
                List<Answer> shuffledAllAvailableAnswersWithoutCorrectAnswers = new ArrayList<>(getAllAvailableAnswers(
                        shuffledQuestions, question, correctAnswers, startParameters.guessMode(), answerCache));

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
        if (startParameters.eventTypeIds().size() == 1) {
            var eventType = eventTypeDao.getEventTypeById(startParameters.eventTypeIds().get(0));
            Event event;

            if (eventType.isEventTypeConference() &&
                    (startParameters.eventIds().size() == 1) &&
                    ((event = eventDao.getEventById(startParameters.eventIds().get(0))) != null)) {
                name = event.getName();
            } else {
                name = eventType.getName();
            }

            logoFileName = eventType.getLogoFileName();
        } else {
            final var SELECTED_EVENT_TYPES = "selectedEventTypes";

            name = Arrays.asList(
                    new LocaleItem(Language.ENGLISH.getCode(), String.format(
                            LocalizationUtils.getResourceString(SELECTED_EVENT_TYPES, Language.ENGLISH),
                            startParameters.eventTypeIds().size())),
                    new LocaleItem(Language.RUSSIAN.getCode(), String.format(
                            LocalizationUtils.getResourceString(SELECTED_EVENT_TYPES, Language.RUSSIAN),
                            startParameters.eventTypeIds().size())));
            logoFileName = null;
        }

        return new QuestionAnswersSet(name, logoFileName, questionAnswersList);
    }

    List<Answer> getCorrectAnswers(Question question, GuessMode guessMode, Map<Long, Answer> answerCache) {
        if (GuessMode.GUESS_NAME_BY_PHOTO_MODE.equals(guessMode) ||
                GuessMode.GUESS_PHOTO_BY_NAME_MODE.equals(guessMode) ||
                GuessMode.GUESS_ACCOUNT_BY_SPEAKER_MODE.equals(guessMode) ||
                GuessMode.GUESS_SPEAKER_BY_ACCOUNT_MODE.equals(guessMode)) {
            var speaker = ((SpeakerQuestion) question).getSpeaker();
            var speakerAnswer = (SpeakerAnswer) answerCache.computeIfAbsent(
                    speaker.getId(),
                    k -> new SpeakerAnswer(speaker));

            return Collections.singletonList(speakerAnswer);
        } else if (GuessMode.GUESS_TALK_BY_SPEAKER_MODE.equals(guessMode)) {
            var talk = ((TalkQuestion) question).getTalk();
            var talkAnswer = (TalkAnswer) answerCache.computeIfAbsent(
                    talk.getId(),
                    k -> new TalkAnswer(talk));

            return Collections.singletonList(talkAnswer);
        } else if (GuessMode.GUESS_SPEAKER_BY_TALK_MODE.equals(guessMode)) {
            return ((TalkQuestion) question).getSpeakers().stream()
                    .map(s -> answerCache.computeIfAbsent(
                            s.getId(),
                            k -> new SpeakerAnswer(s)))
                    .toList();
        } else if (GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode)) {
            return ((CompanyBySpeakerQuestion) question).getCompanies().stream()
                    .map(c -> answerCache.computeIfAbsent(
                            c.getId(),
                            k -> new CompanyAnswer(c)))
                    .toList();
        } else if (GuessMode.GUESS_SPEAKER_BY_COMPANY_MODE.equals(guessMode)) {
            return ((SpeakerByCompanyQuestion) question).getSpeakers().stream()
                    .map(s -> answerCache.computeIfAbsent(
                            s.getId(),
                            k -> new SpeakerAnswer(s)))
                    .toList();
        } else if (GuessMode.GUESS_TAG_CLOUD_BY_SPEAKER_MODE.equals(guessMode)) {
            var speaker = ((TagCloudQuestion) question).getSpeaker();
            var tagCloudAnswer = (TagCloudAnswer) answerCache.computeIfAbsent(
                    speaker.getId(),
                    k -> new TagCloudAnswer(
                            speaker,
                            ((TagCloudQuestion) question).getLanguageWordFrequenciesMap())
            );

            return Collections.singletonList(tagCloudAnswer);
        } else if (GuessMode.GUESS_SPEAKER_BY_TAG_CLOUD_MODE.equals(guessMode)) {
            var speaker = ((TagCloudQuestion) question).getSpeaker();
            var speakerAnswer = (SpeakerAnswer) answerCache.computeIfAbsent(
                    speaker.getId(),
                    k -> new SpeakerAnswer(speaker));

            return Collections.singletonList(speakerAnswer);
        } else {
            throw new IllegalArgumentException(String.format(UNKNOWN_GUESS_MODE_TEXT, guessMode));
        }
    }

    List<Answer> getAllAvailableAnswers(List<Question> questions, Question question, List<Answer> correctAnswers,
                                        GuessMode guessMode, Map<Long, Answer> answerCache) {
        if (GuessMode.GUESS_NAME_BY_PHOTO_MODE.equals(guessMode) ||
                GuessMode.GUESS_PHOTO_BY_NAME_MODE.equals(guessMode) ||
                GuessMode.GUESS_ACCOUNT_BY_SPEAKER_MODE.equals(guessMode) ||
                GuessMode.GUESS_SPEAKER_BY_ACCOUNT_MODE.equals(guessMode)) {
            return questions.stream()
                    .map(q -> {
                                var speaker = ((SpeakerQuestion) q).getSpeaker();

                                return answerCache.computeIfAbsent(
                                        speaker.getId(),
                                        k -> new SpeakerAnswer(speaker));
                            }
                    )
                    .toList();
        } else if (GuessMode.GUESS_TALK_BY_SPEAKER_MODE.equals(guessMode)) {
            List<Speaker> questionSpeakers = ((TalkQuestion) question).getSpeakers();
            var correctAnswerTalk = ((TalkAnswer) correctAnswers.get(0)).getTalk();

            return questions.stream()
                    .map(q -> ((TalkQuestion) q).getTalk())
                    .filter(t -> (t.getId() == correctAnswerTalk.getId()) || !t.getSpeakers().containsAll(questionSpeakers))
                    .map(t -> answerCache.computeIfAbsent(
                            t.getId(),
                            k -> new TalkAnswer(t)))
                    .toList();
        } else if (GuessMode.GUESS_SPEAKER_BY_TALK_MODE.equals(guessMode)) {
            Set<Speaker> questionSpeakers = new HashSet<>(((TalkQuestion) question).getSpeakers());
            Set<Speaker> correctAnswerSpeakers = correctAnswers.stream()
                    .map(a -> ((SpeakerAnswer) a).getSpeaker())
                    .collect(Collectors.toSet());

            return questions.stream()
                    .map(q -> ((TalkQuestion) q).getTalk().getSpeakers())
                    .flatMap(Collection::stream)
                    .distinct()
                    .filter(s -> (correctAnswerSpeakers.contains(s) || !questionSpeakers.contains(s)))
                    .map(s -> answerCache.computeIfAbsent(
                            s.getId(),
                            k -> new SpeakerAnswer(s)))
                    .toList();
        } else if (GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode)) {
            Set<Company> questionCompanies = new HashSet<>(((CompanyBySpeakerQuestion) question).getCompanies());
            Set<Company> correctAnswerCompanies = correctAnswers.stream()
                    .map(a -> ((CompanyAnswer) a).getCompany())
                    .collect(Collectors.toSet());

            return questions.stream()
                    .map(q -> ((CompanyBySpeakerQuestion) q).getCompanies())
                    .flatMap(Collection::stream)
                    .distinct()
                    .filter(c -> (correctAnswerCompanies.contains(c) || !questionCompanies.contains(c)))
                    .map(c -> answerCache.computeIfAbsent(
                            c.getId(),
                            k -> new CompanyAnswer(c)))
                    .toList();
        } else if (GuessMode.GUESS_SPEAKER_BY_COMPANY_MODE.equals(guessMode)) {
            var company = ((SpeakerByCompanyQuestion) question).getCompany();
            Set<Speaker> correctAnswerSpeakers = correctAnswers.stream()
                    .map(a -> ((SpeakerAnswer) a).getSpeaker())
                    .collect(Collectors.toSet());

            return questions.stream()
                    .map(q -> ((SpeakerByCompanyQuestion) q).getSpeakers())
                    .flatMap(Collection::stream)
                    .distinct()
                    .filter(s -> (correctAnswerSpeakers.contains(s) || !s.getCompanies().contains(company)))
                    .map(s -> answerCache.computeIfAbsent(
                            s.getId(),
                            k -> new SpeakerAnswer(s)))
                    .toList();
        } else if (GuessMode.GUESS_TAG_CLOUD_BY_SPEAKER_MODE.equals(guessMode)) {
            return questions.stream()
                    .map(q -> {
                        var speaker = ((TagCloudQuestion) q).getSpeaker();

                        return answerCache.computeIfAbsent(
                                speaker.getId(),
                                k -> new TagCloudAnswer(
                                        speaker,
                                        ((TagCloudQuestion) q).getLanguageWordFrequenciesMap())
                        );
                    })
                    .toList();
        } else if (GuessMode.GUESS_SPEAKER_BY_TAG_CLOUD_MODE.equals(guessMode)) {
            return questions.stream()
                    .map(q -> {
                        var speaker = ((TagCloudQuestion) q).getSpeaker();

                        return answerCache.computeIfAbsent(
                                speaker.getId(),
                                k -> new SpeakerAnswer(speaker));
                    })
                    .toList();
        } else {
            throw new IllegalArgumentException(String.format(UNKNOWN_GUESS_MODE_TEXT, guessMode));
        }
    }
}
