package guess.dao;

import guess.domain.answer.AnswerSet;
import guess.util.HttpSessionUtils;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AnswerDaoImpl class tests")
class AnswerDaoImplTest {
    private final static AnswerDao answerDao = new AnswerDaoImpl();

    @Test
    void getAnswerSets(@Mocked HttpSessionUtils mock) {
        HttpSession httpSession = new MockHttpSession();

        answerDao.getAnswerSets(httpSession);

        new Verifications() {{
            HttpSession capturedHttpSession;

            HttpSessionUtils.getAnswerSets(capturedHttpSession = withCapture());
            times = 1;

            assertEquals(httpSession, capturedHttpSession);
        }};
    }

    @Test
    void clearAnswerSets(@Mocked HttpSessionUtils mock) {
        HttpSession httpSession = new MockHttpSession();

        answerDao.clearAnswerSets(httpSession);

        new Verifications() {{
            HttpSession capturedHttpSession;

            HttpSessionUtils.clearAnswerSets(capturedHttpSession = withCapture());
            times = 1;

            assertEquals(httpSession, capturedHttpSession);
        }};
    }

    @Test
    void addAnswerSet(@Mocked HttpSessionUtils mock) {
        HttpSession httpSession = new MockHttpSession();
        AnswerSet answerSet = new AnswerSet(
                List.of(0L),
                List.of(1L, 0L),
                false);

        answerDao.addAnswerSet(answerSet, httpSession);

        new Verifications() {{
            AnswerSet capturedAnswerSet;
            HttpSession capturedHttpSession;

            HttpSessionUtils.addAnswerSet(capturedAnswerSet = withCapture(), capturedHttpSession = withCapture());
            times = 1;

            assertEquals(answerSet, capturedAnswerSet);
            assertEquals(httpSession, capturedHttpSession);
        }};
    }
}
