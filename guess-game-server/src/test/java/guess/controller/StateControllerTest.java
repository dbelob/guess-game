package guess.controller;

import guess.domain.*;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.QuestionAnswersSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import guess.dto.guess.DtoFunction;
import guess.dto.guess.PhotoNamesDto;
import guess.dto.guess.QuestionAnswersSourceDto;
import guess.service.AnswerService;
import guess.service.LocaleService;
import guess.service.StateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("StateController class tests")
@WebMvcTest(StateController.class)
class StateControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private StateService stateService;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private LocaleService localeService;

    @Autowired
    private StateController stateController;

    @Test
    void setStartParameters() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        StartParameters startParameters = new StartParameters(
                List.of(0L),
                List.of(0L, 1L),
                GuessMode.GUESS_PHOTO_BY_NAME_MODE,
                42);

        mvc.perform(post("/api/state/parameters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(startParameters))
                .session(httpSession))
                .andExpect(status().isOk());
        Mockito.verify(stateService, VerificationModeFactory.times(1)).setStartParameters(startParameters, httpSession);
    }

    @Test
    void getState() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        mvc.perform(get("/api/state/state")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk());
        Mockito.verify(stateService, VerificationModeFactory.times(1)).getState(httpSession);
    }

    @Test
    void setState() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        State state = State.START_STATE;

        mvc.perform(put("/api/state/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content(state.toString())
                .session(httpSession))
                .andExpect(status().isOk());
        Mockito.verify(stateService, VerificationModeFactory.times(1)).setState(state, httpSession);
    }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getDto method tests")
    class GetDtoTest {
        private Stream<Arguments> data() {
            final Language LANGUAGE = Language.ENGLISH;
            final String NAME0 = "Name0";

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);

            Speaker speaker3 = new Speaker();
            speaker3.setId(3);

            SpeakerQuestion speakerQuestion0 = new SpeakerQuestion(speaker0);

            SpeakerAnswer speakerAnswer0 = new SpeakerAnswer(speaker0);
            SpeakerAnswer speakerAnswer1 = new SpeakerAnswer(speaker1);
            SpeakerAnswer speakerAnswer2 = new SpeakerAnswer(speaker2);
            SpeakerAnswer speakerAnswer3 = new SpeakerAnswer(speaker3);

            QuestionAnswers questionAnswers0 = new QuestionAnswers(
                    speakerQuestion0,
                    List.of(speakerAnswer0),
                    new Quadruple<>(speakerAnswer0, speakerAnswer1, speakerAnswer2, speakerAnswer3));

            QuestionAnswersSet questionAnswersSet0 = new QuestionAnswersSet(
                    List.of(new LocaleItem(LANGUAGE.getCode(), NAME0)),
                    null,
                    List.of(questionAnswers0));

            PhotoNamesDto photoNamesDto0 = new PhotoNamesDto(
                    new QuestionAnswersSourceDto(
                            NAME0,
                            0,
                            0,
                            null,
                            Collections.emptyList(),
                            Collections.emptyList()),
                    new Quadruple<>(0L, 1L, 2L, 3L),
                    null,
                    new Quadruple<>("Name0", "Name1", "Name2", "Name3")
            );

            DtoFunction<PhotoNamesDto> dtoFunction = PhotoNamesDto::convertToDto;

            return Stream.of(
                    arguments(0, null, null, null, null),
                    arguments(0, questionAnswersSet0, LANGUAGE, dtoFunction, photoNamesDto0),
                    arguments(1, questionAnswersSet0, LANGUAGE, dtoFunction, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getDto(int currentQuestionIndex, QuestionAnswersSet questionAnswersSet, Language language,
                    DtoFunction<PhotoNamesDto> dtoFunction, PhotoNamesDto expected) {
            MockHttpSession httpSession = new MockHttpSession();

            given(answerService.getCurrentQuestionIndex(httpSession)).willReturn(currentQuestionIndex);
            given(stateService.getQuestionAnswersSet(httpSession)).willReturn(questionAnswersSet);
            given(answerService.getCorrectAnswerIds(currentQuestionIndex, httpSession)).willReturn(List.of(0L, 1L));
            given(answerService.getYourAnswerIds(currentQuestionIndex, httpSession)).willReturn(List.of(0L));
            given(localeService.getLanguage(httpSession)).willReturn(language);

            PhotoNamesDto actual = stateController.getDto(httpSession, dtoFunction);

            Mockito.verify(answerService, VerificationModeFactory.times(1)).getCurrentQuestionIndex(httpSession);
            Mockito.verify(stateService, VerificationModeFactory.times(1)).getQuestionAnswersSet(httpSession);
            Mockito.verify(answerService, VerificationModeFactory.times(1)).getCorrectAnswerIds(currentQuestionIndex, httpSession);
            Mockito.verify(answerService, VerificationModeFactory.times(1)).getYourAnswerIds(currentQuestionIndex, httpSession);

            if (expected != null) {
                Mockito.verify(localeService, VerificationModeFactory.times(1)).getLanguage(httpSession);

                assertEquals(expected.getPhotoFileName(), actual.getPhotoFileName());
            } else {
                Mockito.verify(localeService, VerificationModeFactory.times(0)).getLanguage(httpSession);

                assertNull(actual);
            }

            Mockito.reset(stateService, answerService, localeService);
        }
    }

    @Test
    void getPhotoNames() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        mvc.perform(get("/api/state/photo-names")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk());
    }

    @Test
    void getNamePhotos() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        mvc.perform(get("/api/state/name-photos")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk());
    }

    @Test
    void getSpeakerTalks() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        mvc.perform(get("/api/state/speaker-talks")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk());
    }

    @Test
    void getTalkSpeakers() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        mvc.perform(get("/api/state/talk-speakers")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk());
    }

    @Test
    void getSpeakerAccounts() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        mvc.perform(get("/api/state/speaker-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk());
    }

    @Test
    void getAccountSpeakers() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        mvc.perform(get("/api/state/account-speakers")
                .contentType(MediaType.APPLICATION_JSON)
                .session(httpSession))
                .andExpect(status().isOk());
    }
}
