package guess.service;

import guess.dao.AnswerDao;
import guess.dao.StateDao;
import guess.domain.GuessMode;
import guess.domain.Quadruple;
import guess.domain.StartParameters;
import guess.domain.answer.*;
import guess.domain.question.Question;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.QuestionAnswersSet;
import guess.domain.source.Speaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("AnswerServiceImpl class tests")
class AnswerServiceImplTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("setAnswer method tests")
    class SetAnswerTest {
        private Stream<Arguments> data() {
            AnswerSet answerSet0 = new AnswerSet(
                    List.of(0L),
                    new ArrayList<>(List.of(1L, 0L)),
                    false
            );
            AnswerSet answerSet1 = new AnswerSet(
                    List.of(0L),
                    new ArrayList<>(List.of(1L)),
                    false
            );
            AnswerSet answerSet2 = new AnswerSet(
                    List.of(0L),
                    new ArrayList<>(),
                    false
            );

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Answer answer0 = new SpeakerAnswer(speaker0);

            QuestionAnswers questionAnswers = new QuestionAnswers(
                    null,
                    List.of(answer0),
                    null
            );

            QuestionAnswersSet questionAnswersSet0 = new QuestionAnswersSet(
                    Collections.emptyList(),
                    "",
                    List.of(questionAnswers)
            );

            return Stream.of(
                    arguments(0, 0, List.of(answerSet0), null),
                    arguments(0, 2, List.of(answerSet0), null),
                    arguments(0, 1, List.of(answerSet1), null),
                    arguments(0, 2, List.of(answerSet1), null),
                    arguments(0, 0, List.of(answerSet2), null),
                    arguments(0, 0, Collections.emptyList(), null),
                    arguments(0, 0, Collections.emptyList(), questionAnswersSet0)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void setAnswer(int questionIndex, long answerId, List<AnswerSet> answerSets, QuestionAnswersSet questionAnswersSet) {
            AnswerDao answerDao = Mockito.mock(AnswerDao.class);
            StateDao stateDao = Mockito.mock(StateDao.class);
            AnswerService answerService = new AnswerServiceImpl(answerDao, stateDao);
            HttpSession httpSession = new MockHttpSession();

            Mockito.when(answerDao.getAnswerSets(Mockito.any())).thenReturn(answerSets);
            Mockito.when(stateDao.getQuestionAnswersSet(Mockito.any())).thenReturn(questionAnswersSet);

            assertDoesNotThrow(() -> answerService.setAnswer(questionIndex, answerId, httpSession));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isSuccess method tests")
    class IsSuccessTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(Collections.emptyList(), Collections.emptyList(), true),
                    arguments(List.of(0), Collections.emptyList(), false),
                    arguments(Collections.emptyList(), List.of(0), false),
                    arguments(List.of(0), List.of(0), true),
                    arguments(List.of(0, 1), List.of(0), false),
                    arguments(List.of(0), List.of(0, 1), false),
                    arguments(List.of(0, 1), List.of(0, 1), true),
                    arguments(List.of(0, 1), List.of(1, 0), true),
                    arguments(List.of(1, 0), List.of(0, 1), true)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void isSuccess(List<Long> correctAnswerIds, List<Long> yourAnswerIds, boolean expected) {
            assertEquals(expected, AnswerServiceImpl.isSuccess(correctAnswerIds, yourAnswerIds));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getCurrentQuestionIndex method tests")
    class GetCurrentQuestionIndexTest {
        private Stream<Arguments> data() {
            AnswerSet answerSet0 = new AnswerSet(
                    List.of(0L),
                    List.of(1L),
                    false
            );

            AnswerSet answerSet1 = new AnswerSet(
                    List.of(0L),
                    List.of(0L),
                    false
            );

            AnswerSet answerSet2 = new AnswerSet(
                    List.of(0L),
                    List.of(1L),
                    true
            );

            AnswerSet answerSet3 = new AnswerSet(
                    List.of(0L),
                    List.of(0L),
                    true
            );

            return Stream.of(
                    arguments(Collections.emptyList(), 0),
                    arguments(List.of(answerSet0), 0),
                    arguments(List.of(answerSet1), 1),
                    arguments(List.of(answerSet2), 1),
                    arguments(List.of(answerSet3), 1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void isSuccess(List<AnswerSet> answerSets, int expected) {
            AnswerDao answerDao = Mockito.mock(AnswerDao.class);
            StateDao stateDao = Mockito.mock(StateDao.class);
            AnswerService answerService = new AnswerServiceImpl(answerDao, stateDao);
            HttpSession httpSession = new MockHttpSession();

            Mockito.when(answerDao.getAnswerSets(Mockito.any())).thenReturn(answerSets);

            assertEquals(expected, answerService.getCurrentQuestionIndex(httpSession));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getCorrectAnswerIds method tests")
    class GetCorrectAnswerIdsTest {
        private Stream<Arguments> data() {
            AnswerSet answerSet0 = new AnswerSet(
                    List.of(0L),
                    List.of(1L),
                    false
            );

            return Stream.of(
                    arguments(0, List.of(answerSet0), List.of(0L)),
                    arguments(1, List.of(answerSet0), Collections.emptyList())
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getCorrectAnswerIds(int questionIndex, List<AnswerSet> answerSets, List<Long> expected) {
            AnswerDao answerDao = Mockito.mock(AnswerDao.class);
            StateDao stateDao = Mockito.mock(StateDao.class);
            AnswerService answerService = new AnswerServiceImpl(answerDao, stateDao);
            HttpSession httpSession = new MockHttpSession();

            Mockito.when(answerDao.getAnswerSets(Mockito.any())).thenReturn(answerSets);

            assertEquals(expected, answerService.getCorrectAnswerIds(questionIndex, httpSession));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getYourAnswerIds method tests")
    class GetYourAnswerIdsTest {
        private Stream<Arguments> data() {
            AnswerSet answerSet0 = new AnswerSet(
                    List.of(0L),
                    List.of(1L),
                    false
            );

            return Stream.of(
                    arguments(0, List.of(answerSet0), List.of(1L)),
                    arguments(1, List.of(answerSet0), Collections.emptyList())
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getYourAnswerIds(int questionIndex, List<AnswerSet> answerSets, List<Long> expected) {
            AnswerDao answerDao = Mockito.mock(AnswerDao.class);
            StateDao stateDao = Mockito.mock(StateDao.class);
            AnswerService answerService = new AnswerServiceImpl(answerDao, stateDao);
            HttpSession httpSession = new MockHttpSession();

            Mockito.when(answerDao.getAnswerSets(Mockito.any())).thenReturn(answerSets);

            assertEquals(expected, answerService.getYourAnswerIds(questionIndex, httpSession));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getResult method tests")
    class GetResultTest {
        private Stream<Arguments> data() {
            AnswerSet answerSet0 = new AnswerSet(
                    List.of(0L),
                    List.of(1L),
                    false
            );

            AnswerSet answerSet1 = new AnswerSet(
                    List.of(0L),
                    List.of(0L),
                    true
            );

            StartParameters startParameters0 = new StartParameters(
                    List.of(0L),
                    List.of(0L),
                    GuessMode.GUESS_PHOTO_BY_NAME_MODE,
                    7
            );

            Question question0 = new Question() {
                @Override
                public long getId() {
                    return 0;
                }

                @Override
                public void setId(long id) {
                    // Nothing
                }
            };

            Question question1 = new Question() {
                @Override
                public long getId() {
                    return 1;
                }

                @Override
                public void setId(long id) {
                    // Nothing
                }
            };

            Answer answer0 = new Answer() {
                @Override
                public long getId() {
                    return 0;
                }

                @Override
                public void setId(long id) {
                    // Nothing
                }
            };

            Answer answer1 = new Answer() {
                @Override
                public long getId() {
                    return 1;
                }

                @Override
                public void setId(long id) {
                    // Nothing
                }
            };

            Answer answer2 = new Answer() {
                @Override
                public long getId() {
                    return 2;
                }

                @Override
                public void setId(long id) {
                    // Nothing
                }
            };

            Answer answer3 = new Answer() {
                @Override
                public long getId() {
                    return 3;
                }

                @Override
                public void setId(long id) {
                    // Nothing
                }
            };

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

            Result result0 = new Result(
                    1,
                    1,
                    0,
                    0.5F,
                    0.5F,
                    0,
                    GuessMode.GUESS_NAME_BY_PHOTO_MODE
            );

            Result result1 = new Result(
                    1,
                    1,
                    0,
                    0.5F,
                    0.5F,
                    0,
                    GuessMode.GUESS_PHOTO_BY_NAME_MODE
            );

            Result result2 = new Result(
                    1,
                    1,
                    -2,
                    0,
                    0,
                    0,
                    GuessMode.GUESS_PHOTO_BY_NAME_MODE
            );

            return Stream.of(
                    arguments(List.of(answerSet0, answerSet1), null, questionAnswersSet0, result0),
                    arguments(List.of(answerSet0, answerSet1), startParameters0, questionAnswersSet0, result1),
                    arguments(List.of(answerSet0, answerSet1), startParameters0, null, result2)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getResult(List<AnswerSet> answerSets, StartParameters startParameters, QuestionAnswersSet questionAnswersSet,
                       Result expected) {
            AnswerDao answerDao = Mockito.mock(AnswerDao.class);
            StateDao stateDao = Mockito.mock(StateDao.class);
            AnswerService answerService = new AnswerServiceImpl(answerDao, stateDao);
            HttpSession httpSession = new MockHttpSession();

            Mockito.when(answerDao.getAnswerSets(Mockito.any())).thenReturn(answerSets);
            Mockito.when(stateDao.getStartParameters(Mockito.any())).thenReturn(startParameters);
            Mockito.when(stateDao.getQuestionAnswersSet(Mockito.any())).thenReturn(questionAnswersSet);

            assertEquals(expected, answerService.getResult(httpSession));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getErrorDetailsList method tests")
    class GetErrorDetailsListTest {
        private Stream<Arguments> data() {
            AnswerSet answerSet0 = new AnswerSet(
                    List.of(0L),
                    List.of(1L),
                    false
            );

            AnswerSet answerSet1 = new AnswerSet(
                    List.of(0L),
                    List.of(0L),
                    true
            );

            AnswerSet answerSet2 = new AnswerSet(
                    List.of(0L),
                    Collections.emptyList(),
                    false
            );

            Question question0 = new Question() {
                @Override
                public long getId() {
                    return 0;
                }

                @Override
                public void setId(long id) {
                    // Nothing
                }
            };

            Question question1 = new Question() {
                @Override
                public long getId() {
                    return 1;
                }

                @Override
                public void setId(long id) {
                    // Nothing
                }
            };

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);

            Speaker speaker3 = new Speaker();
            speaker3.setId(3);

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

            ErrorDetails errorDetails0 = new ErrorDetails(
                    question0,
                    List.of(answer0),
                    List.of(answer0, answer1, answer2, answer3),
                    List.of(answer1)
            );

            return Stream.of(
                    arguments(Collections.emptyList(), null, Collections.emptyList()),
                    arguments(List.of(answerSet0, answerSet1, answerSet2), questionAnswersSet0, List.of(errorDetails0))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getErrorDetailsList(List<AnswerSet> answerSets, QuestionAnswersSet questionAnswersSet, List<ErrorDetails> expected) {
            AnswerDao answerDao = Mockito.mock(AnswerDao.class);
            StateDao stateDao = Mockito.mock(StateDao.class);
            AnswerService answerService = new AnswerServiceImpl(answerDao, stateDao);
            HttpSession httpSession = new MockHttpSession();

            Mockito.when(answerDao.getAnswerSets(Mockito.any())).thenReturn(answerSets);
            Mockito.when(stateDao.getQuestionAnswersSet(Mockito.any())).thenReturn(questionAnswersSet);

            assertEquals(expected, answerService.getErrorDetailsList(httpSession));
        }
    }
}
