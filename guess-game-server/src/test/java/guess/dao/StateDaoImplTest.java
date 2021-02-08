package guess.dao;

import guess.domain.GameState;
import guess.domain.GuessMode;
import guess.domain.Quadruple;
import guess.domain.StartParameters;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.QuestionAnswersSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import guess.util.HttpSessionUtils;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("StateDaoImpl class tests")
class StateDaoImplTest {
    private final static StateDao stateDao = new StateDaoImpl();

    @Test
    void getGameState(@Mocked HttpSessionUtils mock) {
        HttpSession httpSession = new MockHttpSession();

        stateDao.getGameState(httpSession);

        new Verifications() {{
            HttpSession capturedHttpSession;

            HttpSessionUtils.getGameState(capturedHttpSession = withCapture());
            times = 1;

            assertEquals(httpSession, capturedHttpSession);
        }};
    }

    @Test
    void setGameState(@Mocked HttpSessionUtils mock) {
        GameState state = GameState.GUESS_NAME_BY_PHOTO_STATE;
        HttpSession httpSession = new MockHttpSession();

        stateDao.setGameState(state, httpSession);

        new Verifications() {{
            GameState capturedState;
            HttpSession capturedHttpSession;

            HttpSessionUtils.setGameState(capturedState = withCapture(), capturedHttpSession = withCapture());
            times = 1;

            assertEquals(state, capturedState);
            assertEquals(httpSession, capturedHttpSession);
        }};
    }

    @Test
    void getStartParameters(@Mocked HttpSessionUtils mock) {
        HttpSession httpSession = new MockHttpSession();

        stateDao.getStartParameters(httpSession);

        new Verifications() {{
            HttpSession capturedHttpSession;

            HttpSessionUtils.getStartParameters(capturedHttpSession = withCapture());
            times = 1;

            assertEquals(httpSession, capturedHttpSession);
        }};
    }

    @Test
    void setStartParameters(@Mocked HttpSessionUtils mock) {
        StartParameters startParameters = new StartParameters(
                List.of(0L),
                List.of(0L, 1L),
                GuessMode.GUESS_NAME_BY_PHOTO_MODE,
                42);
        HttpSession httpSession = new MockHttpSession();

        stateDao.setStartParameters(startParameters, httpSession);

        new Verifications() {{
            StartParameters capturedStartParameters;
            HttpSession capturedHttpSession;

            HttpSessionUtils.setStartParameters(capturedStartParameters = withCapture(), capturedHttpSession = withCapture());
            times = 1;

            assertEquals(startParameters, capturedStartParameters);
            assertEquals(httpSession, capturedHttpSession);
        }};
    }

    @Test
    void getQuestionAnswersSet(@Mocked HttpSessionUtils mock) {
        HttpSession httpSession = new MockHttpSession();

        stateDao.getQuestionAnswersSet(httpSession);

        new Verifications() {{
            HttpSession capturedHttpSession;

            HttpSessionUtils.getQuestionAnswersSet(capturedHttpSession = withCapture());
            times = 1;

            assertEquals(httpSession, capturedHttpSession);
        }};
    }

    @Test
    void setQuestionAnswersSet(@Mocked HttpSessionUtils mock) {
        Speaker speaker0 = new Speaker();
        speaker0.setId(0);

        Speaker speaker1 = new Speaker();
        speaker0.setId(1);

        Speaker speaker2 = new Speaker();
        speaker0.setId(2);

        Speaker speaker3 = new Speaker();
        speaker0.setId(3);

        SpeakerAnswer speakerAnswer0 = new SpeakerAnswer(speaker0);
        SpeakerAnswer speakerAnswer1 = new SpeakerAnswer(speaker1);
        SpeakerAnswer speakerAnswer2 = new SpeakerAnswer(speaker2);
        SpeakerAnswer speakerAnswer3 = new SpeakerAnswer(speaker3);

        QuestionAnswersSet questionAnswersSet = new QuestionAnswersSet(
                List.of(new LocaleItem("en", "Name0")),
                "logoFileName0",
                List.of(new QuestionAnswers(
                        new SpeakerQuestion(speaker0),
                        List.of(speakerAnswer0),
                        new Quadruple<>(speakerAnswer0, speakerAnswer1, speakerAnswer2, speakerAnswer3)
                ))
        );
        HttpSession httpSession = new MockHttpSession();

        stateDao.setQuestionAnswersSet(questionAnswersSet, httpSession);

        new Verifications() {{
            QuestionAnswersSet capturedQuestionAnswersSet;
            HttpSession capturedHttpSession;

            HttpSessionUtils.setQuestionAnswersSet(capturedQuestionAnswersSet = withCapture(), capturedHttpSession = withCapture());
            times = 1;

            assertEquals(questionAnswersSet, capturedQuestionAnswersSet);
            assertEquals(httpSession, capturedHttpSession);
        }};
    }
}
