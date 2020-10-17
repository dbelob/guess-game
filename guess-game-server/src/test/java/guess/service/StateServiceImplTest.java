package guess.service;

import guess.dao.*;
import guess.domain.GuessMode;
import guess.domain.Quadruple;
import guess.domain.StartParameters;
import guess.domain.answer.Answer;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.Question;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.QuestionAnswersSet;
import guess.domain.question.SpeakerQuestion;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("AnswerServiceImpl class tests")
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
}
