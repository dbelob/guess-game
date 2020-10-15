package guess.service;

import guess.dao.AnswerDao;
import guess.dao.StateDao;
import guess.domain.answer.Answer;
import guess.domain.answer.AnswerSet;
import guess.domain.answer.SpeakerAnswer;
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
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false
            );
            AnswerSet answerSet1 = new AnswerSet(
                    new ArrayList<>(List.of(0L)),
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
                    arguments(0, 0, List.of(answerSet1), null),
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
}
