package guess.controller;

import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.answer.ErrorDetails;
import guess.domain.answer.Result;
import guess.service.AnswerService;
import guess.service.LocaleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AnswerController class tests")
@WebMvcTest(AnswerController.class)
class AnswerControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private LocaleService localeService;

    @Test
    void addAnswer() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        mvc.perform(post("/api/answer/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .param("questionIndex", "0")
                .param("answerId", "1")
                .session(httpSession))
                .andExpect(status().isOk());
        Mockito.verify(answerService, VerificationModeFactory.times(1)).setAnswer(0, 1, httpSession);
        Mockito.reset(localeService);
    }

    @Test
    void getResult() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        Result result = new Result(
                42,
                0,
                0,
                100,
                0,
                0,
                GuessMode.GUESS_NAME_BY_PHOTO_MODE);
        List<ErrorDetails> errorDetailsList = Collections.emptyList();

        given(answerService.getResult(httpSession)).willReturn(result);
        given(answerService.getErrorDetailsList(httpSession)).willReturn(errorDetailsList);
        given(localeService.getLanguage(httpSession)).willReturn(Language.ENGLISH);

        mvc.perform(get("/api/answer/result")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correctAnswers", is(42)));

        Mockito.verify(answerService, VerificationModeFactory.times(1)).getResult(httpSession);
        Mockito.verify(answerService, VerificationModeFactory.times(1)).getErrorDetailsList(httpSession);
        Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);

        Mockito.reset(answerService);
        Mockito.reset(localeService);
    }
}
