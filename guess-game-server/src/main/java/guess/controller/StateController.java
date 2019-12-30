package guess.controller;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.Language;
import guess.domain.State;
import guess.domain.question.QuestionAnswers2;
import guess.domain.question.QuestionAnswersSet;
import guess.dto.guess.*;
import guess.dto.start.StartParametersDto;
import guess.service.AnswerService;
import guess.service.LocaleService;
import guess.service.StateService;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * State controller.
 */
@Controller
@RequestMapping("/api/state")
public class StateController {
    private StateService stateService;
    private AnswerService answerService;
    private LocaleService localeService;

    @Autowired
    public StateController(StateService stateService, AnswerService answerService, LocaleService localeService) {
        this.stateService = stateService;
        this.answerService = answerService;
        this.localeService = localeService;
    }

    @PostMapping("/parameters")
    @ResponseStatus(HttpStatus.OK)
    public void setStartParameters(@RequestBody StartParametersDto startParameters, HttpSession httpSession) throws QuestionSetNotExistsException {
        stateService.setStartParameters(StartParametersDto.convertFromDto(startParameters), httpSession);
    }

    @GetMapping("/state")
    @ResponseBody
    public State getState(HttpSession httpSession) {
        return stateService.getState(httpSession);
    }

    @PutMapping("/state")
    @ResponseStatus(HttpStatus.OK)
    public void setState(@RequestBody String state, HttpSession httpSession) {
        stateService.setState(State.valueOf(state), httpSession);
    }

    private <T> T getDto(HttpSession httpSession, DtoFunction<T> dtoFunction) {
        int currentQuestionIndex = answerService.getCurrentQuestionIndex(httpSession);
        QuestionAnswersSet questionAnswersSet = stateService.getQuestionAnswersSet(httpSession);
        List<Long> wrongAnswerIds = answerService.getWrongAnswerIds(currentQuestionIndex, httpSession);

        if ((questionAnswersSet != null) && (currentQuestionIndex < questionAnswersSet.getQuestionAnswersList().size())) {
            QuestionAnswers2 questionAnswers = questionAnswersSet.getQuestionAnswersList().get(currentQuestionIndex);
            Language language = localeService.getLanguage(httpSession);

            return dtoFunction.apply(
                    LocalizationUtils.getString(questionAnswersSet.getName(), language),
                    currentQuestionIndex,
                    questionAnswersSet.getQuestionAnswersList().size(),
                    questionAnswersSet.getLogoFileName(),
                    questionAnswers,
                    wrongAnswerIds,
                    language);
        } else {
            return null;
        }
    }

    @GetMapping("/picture-names")
    @ResponseBody
    public PictureNamesDto getPictureNames(HttpSession httpSession) {
        return getDto(httpSession, PictureNamesDto::convertToDto);
    }

    @GetMapping("/name-pictures")
    @ResponseBody
    public NamePicturesDto getNamePictures(HttpSession httpSession) {
        return getDto(httpSession, NamePicturesDto::convertToDto);
    }

    @GetMapping("/speaker-talks")
    @ResponseBody
    public SpeakerTalksDto getSpeakerTalks(HttpSession httpSession) {
        return getDto(httpSession, SpeakerTalksDto::convertToDto);
    }

    @GetMapping("/talk-speakers")
    @ResponseBody
    public TalkSpeakersDto getTalkSpeakers(HttpSession httpSession) {
        return getDto(httpSession, TalkSpeakersDto::convertToDto);
    }
}
