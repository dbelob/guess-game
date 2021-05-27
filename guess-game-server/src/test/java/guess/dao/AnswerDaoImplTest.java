package guess.dao;

import guess.domain.answer.AnswerSet;
import guess.util.HttpSessionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;
import java.util.List;

@DisplayName("AnswerDaoImpl class tests")
class AnswerDaoImplTest {
    private final static AnswerDao answerDao = new AnswerDaoImpl();

    @Test
    void getAnswerSets() {
        try (MockedStatic<HttpSessionUtils> mockedStatic = Mockito.mockStatic(HttpSessionUtils.class)) {
            HttpSession httpSession = new MockHttpSession();

            answerDao.getAnswerSets(httpSession);

            mockedStatic.verify(Mockito.times(1), () -> HttpSessionUtils.getAnswerSets(httpSession));
        }
    }

    @Test
    void clearAnswerSets() {
        try (MockedStatic<HttpSessionUtils> mockedStatic = Mockito.mockStatic(HttpSessionUtils.class)) {
            HttpSession httpSession = new MockHttpSession();

            answerDao.clearAnswerSets(httpSession);

            mockedStatic.verify(Mockito.times(1), () -> HttpSessionUtils.clearAnswerSets(httpSession));
        }
    }

    @Test
    void addAnswerSet() {
        try (MockedStatic<HttpSessionUtils> mockedStatic = Mockito.mockStatic(HttpSessionUtils.class)) {
            HttpSession httpSession = new MockHttpSession();
            AnswerSet answerSet = new AnswerSet(
                    List.of(0L),
                    List.of(1L, 0L),
                    false);

            answerDao.addAnswerSet(answerSet, httpSession);

            mockedStatic.verify(Mockito.times(1), () -> HttpSessionUtils.addAnswerSet(answerSet, httpSession));
        }
    }
}
