package guess.service;

import guess.dao.*;
import guess.domain.*;
import guess.domain.answer.Answer;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.answer.TalkAnswer;
import guess.domain.question.*;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("StateServiceImpl class tests")
class StateServiceImplTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("setStartParameters method tests")
    class SetStartParametersTest {
        private Stream<Arguments> data() {
            StartParameters startParameters0 = new StartParameters(
                    List.of(0L),
                    List.of(0L),
                    GuessMode.GUESS_PHOTO_BY_NAME_MODE,
                    42
            );

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);

            Speaker speaker3 = new Speaker();
            speaker3.setId(3);

            Question question0 = new SpeakerQuestion(speaker0);
            Question question1 = new SpeakerQuestion(speaker1);

            Answer answer0 = new SpeakerAnswer(speaker0);
            Answer answer1 = new SpeakerAnswer(speaker1);
            Answer answer2 = new SpeakerAnswer(speaker2);
            Answer answer3 = new SpeakerAnswer(speaker3);

            QuestionAnswers questionAnswers0 = new QuestionAnswers(
                    question0,
                    List.of(answer0),
                    new Quadruple<>(answer0, answer1, answer2, answer3)
            );

            QuestionAnswers questionAnswers1 = new QuestionAnswers(
                    question1,
                    List.of(answer1),
                    new Quadruple<>(answer0, answer1, answer2, answer3)
            );

            QuestionAnswersSet questionAnswersSet0 = new QuestionAnswersSet(
                    Collections.emptyList(),
                    "",
                    List.of(questionAnswers0, questionAnswers1)
            );

            QuestionAnswersSet questionAnswersSet1 = new QuestionAnswersSet(
                    Collections.emptyList(),
                    "",
                    Collections.emptyList()
            );

            return Stream.of(
                    arguments(startParameters0, questionAnswersSet0),
                    arguments(startParameters0, questionAnswersSet1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void setStartParameters(StartParameters startParameters, QuestionAnswersSet questionAnswersSet) {
            StateDao stateDao = Mockito.mock(StateDao.class);
            QuestionDao questionDao = Mockito.mock(QuestionDao.class);
            AnswerDao answerDao = Mockito.mock(AnswerDao.class);
            EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
            EventDao eventDao = Mockito.mock(EventDao.class);
            StateServiceImpl stateService = Mockito.mock(StateServiceImpl.class, Mockito.withSettings().useConstructor(stateDao, questionDao, answerDao, eventTypeDao, eventDao));
            HttpSession httpSession = new MockHttpSession();

            Mockito.when(stateService.createQuestionAnswersSet(Mockito.any())).thenReturn(questionAnswersSet);
            Mockito.doCallRealMethod().when(stateService).setStartParameters(Mockito.any(StartParameters.class), Mockito.any(HttpSession.class));

            assertDoesNotThrow(() -> stateService.setStartParameters(startParameters, httpSession));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getStateByGuessMode method tests")
    class GetStateByGuessModeTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(GuessMode.GUESS_NAME_BY_PHOTO_MODE, null, State.GUESS_NAME_BY_PHOTO_STATE),
                    arguments(GuessMode.GUESS_PHOTO_BY_NAME_MODE, null, State.GUESS_PHOTO_BY_NAME_STATE),
                    arguments(GuessMode.GUESS_TALK_BY_SPEAKER_MODE, null, State.GUESS_TALK_BY_SPEAKER_STATE),
                    arguments(GuessMode.GUESS_SPEAKER_BY_TALK_MODE, null, State.GUESS_SPEAKER_BY_TALK_STATE),
                    arguments(GuessMode.GUESS_ACCOUNT_BY_SPEAKER_MODE, null, State.GUESS_ACCOUNT_BY_SPEAKER_STATE),
                    arguments(GuessMode.GUESS_SPEAKER_BY_ACCOUNT_MODE, null, State.GUESS_SPEAKER_BY_ACCOUNT_STATE),
                    arguments(null, IllegalArgumentException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getStateByGuessMode(GuessMode guessMode, Class<? extends Throwable> expectedException, State expectedValue) {
            StateServiceImpl stateService = Mockito.mock(StateServiceImpl.class);

            Mockito.when(stateService.getStateByGuessMode(Mockito.any())).thenCallRealMethod();

            if (expectedException == null) {
                assertEquals(expectedValue, stateService.getStateByGuessMode(guessMode));
            } else {
                assertThrows(expectedException, () -> stateService.getStateByGuessMode(guessMode));
            }
        }
    }

    @Test
    void getState() {
        StateDao stateDao = Mockito.mock(StateDao.class);
        QuestionDao questionDao = Mockito.mock(QuestionDao.class);
        AnswerDao answerDao = Mockito.mock(AnswerDao.class);
        EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
        EventDao eventDao = Mockito.mock(EventDao.class);
        StateService stateService = new StateServiceImpl(stateDao, questionDao, answerDao, eventTypeDao, eventDao);
        HttpSession httpSession = new MockHttpSession();

        stateService.getState(httpSession);
        Mockito.verify(stateDao, VerificationModeFactory.times(1)).getState(httpSession);
        Mockito.verifyNoMoreInteractions(stateDao);
    }

    @Test
    void setState() {
        StateDao stateDao = Mockito.mock(StateDao.class);
        QuestionDao questionDao = Mockito.mock(QuestionDao.class);
        AnswerDao answerDao = Mockito.mock(AnswerDao.class);
        EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
        EventDao eventDao = Mockito.mock(EventDao.class);
        StateService stateService = new StateServiceImpl(stateDao, questionDao, answerDao, eventTypeDao, eventDao);
        HttpSession httpSession = new MockHttpSession();

        stateService.setState(State.START_STATE, httpSession);
        Mockito.verify(stateDao, VerificationModeFactory.times(1)).setState(State.START_STATE, httpSession);
        Mockito.verifyNoMoreInteractions(stateDao);
    }

    @Test
    void getQuestionAnswersSet() {
        StateDao stateDao = Mockito.mock(StateDao.class);
        QuestionDao questionDao = Mockito.mock(QuestionDao.class);
        AnswerDao answerDao = Mockito.mock(AnswerDao.class);
        EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
        EventDao eventDao = Mockito.mock(EventDao.class);
        StateService stateService = new StateServiceImpl(stateDao, questionDao, answerDao, eventTypeDao, eventDao);
        HttpSession httpSession = new MockHttpSession();

        stateService.getQuestionAnswersSet(httpSession);
        Mockito.verify(stateDao, VerificationModeFactory.times(1)).getQuestionAnswersSet(httpSession);
        Mockito.verifyNoMoreInteractions(stateDao);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("createQuestionAnswersSet method tests")
    class CreateQuestionAnswersSetTest {
        private Stream<Arguments> data() {
            StartParameters startParameters0 = new StartParameters(
                    List.of(0L),
                    List.of(0L),
                    GuessMode.GUESS_PHOTO_BY_NAME_MODE,
                    42
            );

            StartParameters startParameters1 = new StartParameters(
                    List.of(0L),
                    List.of(0L, 1L),
                    GuessMode.GUESS_PHOTO_BY_NAME_MODE,
                    42
            );

            StartParameters startParameters2 = new StartParameters(
                    List.of(0L, 1L),
                    List.of(0L),
                    GuessMode.GUESS_PHOTO_BY_NAME_MODE,
                    42
            );

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);

            Speaker speaker3 = new Speaker();
            speaker3.setId(3);

            Question question0 = new SpeakerQuestion(speaker0);
            Question question1 = new SpeakerQuestion(speaker1);
            Question question2 = new SpeakerQuestion(speaker2);
            Question question3 = new SpeakerQuestion(speaker3);

            Answer answer0 = new SpeakerAnswer(speaker0);
            Answer answer1 = new SpeakerAnswer(speaker1);
            Answer answer2 = new SpeakerAnswer(speaker2);
            Answer answer3 = new SpeakerAnswer(speaker3);

            EventType eventType0 = new EventType();
            eventType0.setConference(Conference.JPOINT);

            EventType eventType1 = new EventType();

            Event event0 = new Event();

            List<Arguments> argumentsList = new ArrayList<>();

            for (StartParameters startParameters : List.of(startParameters0, startParameters1)) {
                for (EventType eventType : List.of(eventType0, eventType1)) {
                    for (Event event : Arrays.asList(event0, null)) {
                        argumentsList.add(arguments(
                                startParameters,
                                List.of(question0, question1, question2, question3),
                                List.of(answer0),
                                new ArrayList<>(List.of(answer0, answer1, answer2, answer3)),
                                eventType,
                                event
                        ));
                    }
                }
            }

            argumentsList.add(arguments(
                    startParameters2,
                    List.of(question0, question1, question2),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    null,
                    null
            ));

            return Stream.of(argumentsList.toArray(new Arguments[0]));
        }

        @ParameterizedTest
        @MethodSource("data")
        void createQuestionAnswersSet(StartParameters startParameters, List<Question> uniqueQuestions, List<Answer> correctAnswers,
                                      List<Answer> allAvailableAnswers, EventType eventType, Event event) {
            StateDao stateDao = Mockito.mock(StateDao.class);
            QuestionDao questionDao = Mockito.mock(QuestionDao.class);
            AnswerDao answerDao = Mockito.mock(AnswerDao.class);
            EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
            EventDao eventDao = Mockito.mock(EventDao.class);
            StateServiceImpl stateService = Mockito.mock(StateServiceImpl.class, Mockito.withSettings().useConstructor(stateDao, questionDao, answerDao, eventTypeDao, eventDao));

            Mockito.when(questionDao.getQuestionByIds(Mockito.anyList(), Mockito.anyList(), Mockito.any())).thenReturn(uniqueQuestions);
            Mockito.when(eventTypeDao.getEventTypeById(Mockito.anyLong())).thenReturn(eventType);
            Mockito.when(eventDao.getEventById(Mockito.anyLong())).thenReturn(event);

            Mockito.doCallRealMethod().when(stateService).createQuestionAnswersSet(Mockito.any(StartParameters.class));
            Mockito.when(stateService.getCorrectAnswers(Mockito.any(), Mockito.any())).thenReturn(correctAnswers);
            Mockito.when(stateService.getAllAvailableAnswers(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(allAvailableAnswers);

            assertDoesNotThrow(() -> stateService.createQuestionAnswersSet(startParameters));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getCorrectAnswers method tests")
    class GetCorrectAnswersTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Talk talk0 = new Talk();
            talk0.setId(0);

            Question question0 = new SpeakerQuestion(speaker0);
            Question question1 = new TalkQuestion(List.of(speaker0, speaker1), talk0);

            return Stream.of(
                    arguments(question0, GuessMode.GUESS_NAME_BY_PHOTO_MODE, null, List.of(new SpeakerAnswer((speaker0)))),
                    arguments(question0, GuessMode.GUESS_PHOTO_BY_NAME_MODE, null, List.of(new SpeakerAnswer((speaker0)))),
                    arguments(question0, GuessMode.GUESS_ACCOUNT_BY_SPEAKER_MODE, null, List.of(new SpeakerAnswer((speaker0)))),
                    arguments(question0, GuessMode.GUESS_SPEAKER_BY_ACCOUNT_MODE, null, List.of(new SpeakerAnswer((speaker0)))),
                    arguments(question1, GuessMode.GUESS_TALK_BY_SPEAKER_MODE, null, List.of(new TalkAnswer(talk0))),
                    arguments(question1, GuessMode.GUESS_SPEAKER_BY_TALK_MODE, null, List.of(new SpeakerAnswer((speaker0)), new SpeakerAnswer((speaker1)))),
                    arguments(null, null, IllegalArgumentException.class, null),
                    arguments(question0, null, IllegalArgumentException.class, null),
                    arguments(question1, null, IllegalArgumentException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getCorrectAnswers(Question question, GuessMode guessMode, Class<? extends Throwable> expectedException, List<Answer> expectedValue) {
            StateServiceImpl stateService = Mockito.mock(StateServiceImpl.class);

            Mockito.when(stateService.getCorrectAnswers(Mockito.any(), Mockito.any())).thenCallRealMethod();

            if (expectedException == null) {
                assertEquals(expectedValue, stateService.getCorrectAnswers(question, guessMode));
            } else {
                assertThrows(expectedException, () -> stateService.getCorrectAnswers(question, guessMode));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getAllAvailableAnswers method tests")
    class GetAllAvailableAnswersTest {
        private Stream<Arguments> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Talk talk0 = new Talk();
            talk0.setId(0);
            talk0.setSpeakerIds(List.of(0L));
            talk0.setSpeakers(List.of(speaker0));

            Talk talk1 = new Talk();
            talk1.setId(1);
            talk1.setSpeakerIds(List.of(1L));
            talk1.setSpeakers(List.of(speaker1));

            Talk talk2 = new Talk();
            talk2.setId(2);
            talk2.setSpeakerIds(List.of(0L));
            talk2.setSpeakers(List.of(speaker0));

            Question question0 = new SpeakerQuestion(speaker0);
            Question question1 = new TalkQuestion(List.of(speaker0, speaker1), talk0);

            Answer answer0 = new TalkAnswer(talk0);
            Answer answer1 = new TalkAnswer(talk1);
            Answer answer2 = new TalkAnswer(talk2);

            return Stream.of(
                    arguments(List.of(question0), null, GuessMode.GUESS_NAME_BY_PHOTO_MODE, null, List.of(new SpeakerAnswer(speaker0))),
                    arguments(List.of(question0), null, GuessMode.GUESS_PHOTO_BY_NAME_MODE, null, List.of(new SpeakerAnswer(speaker0))),
                    arguments(List.of(question0), null, GuessMode.GUESS_ACCOUNT_BY_SPEAKER_MODE, null, List.of(new SpeakerAnswer(speaker0))),
                    arguments(List.of(question0), null, GuessMode.GUESS_SPEAKER_BY_ACCOUNT_MODE, null, List.of(new SpeakerAnswer(speaker0))),
                    arguments(List.of(question1), List.of(answer0), GuessMode.GUESS_TALK_BY_SPEAKER_MODE, null, List.of(new TalkAnswer(talk0))),
                    arguments(List.of(question1), List.of(answer1), GuessMode.GUESS_TALK_BY_SPEAKER_MODE, null, List.of(new TalkAnswer(talk0))),
                    arguments(List.of(question1), List.of(answer2), GuessMode.GUESS_TALK_BY_SPEAKER_MODE, null, Collections.emptyList()),
                    arguments(List.of(question1), null, GuessMode.GUESS_SPEAKER_BY_TALK_MODE, null, List.of(new SpeakerAnswer(speaker0))),
                    arguments(null, null, null, IllegalArgumentException.class, null),
                    arguments(List.of(question0), null, null, IllegalArgumentException.class, null),
                    arguments(List.of(question1), null, null, IllegalArgumentException.class, null),
                    arguments(List.of(question1), List.of(answer0), null, IllegalArgumentException.class, null),
                    arguments(List.of(question1), List.of(answer1), null, IllegalArgumentException.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getAllAvailableAnswers(List<Question> questions, List<Answer> correctAnswers, GuessMode guessMode,
                                    Class<? extends Throwable> expectedException, List<Answer> expectedValue) {
            StateServiceImpl stateService = Mockito.mock(StateServiceImpl.class);

            Mockito.when(stateService.getAllAvailableAnswers(Mockito.any(), Mockito.any(), Mockito.any())).thenCallRealMethod();

            if (expectedException == null) {
                assertEquals(expectedValue, stateService.getAllAvailableAnswers(questions, correctAnswers, guessMode));
            } else {
                assertThrows(expectedException, () -> stateService.getAllAvailableAnswers(questions, correctAnswers, guessMode));
            }
        }
    }
}
