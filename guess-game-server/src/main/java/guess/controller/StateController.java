package guess.controller;

import guess.domain.Language;
import guess.domain.State;
import guess.domain.question.QuestionAnswers;
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
    private final StateService stateService;
    private final AnswerService answerService;
    private final LocaleService localeService;

    @Autowired
    public StateController(StateService stateService, AnswerService answerService, LocaleService localeService) {
        this.stateService = stateService;
        this.answerService = answerService;
        this.localeService = localeService;
    }

    @PostMapping("/parameters")
    @ResponseStatus(HttpStatus.OK)
    public void setStartParameters(@RequestBody StartParametersDto startParameters, HttpSession httpSession) {
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

    <T> T getDto(HttpSession httpSession, DtoFunction<T> dtoFunction) {
        int currentQuestionIndex = answerService.getCurrentQuestionIndex(httpSession);
        QuestionAnswersSet questionAnswersSet = stateService.getQuestionAnswersSet(httpSession);
        List<Long> correctAnswerIds = answerService.getCorrectAnswerIds(currentQuestionIndex, httpSession);
        List<Long> yourAnswerIds = answerService.getYourAnswerIds(currentQuestionIndex, httpSession);

        if ((questionAnswersSet != null) && (currentQuestionIndex < questionAnswersSet.getQuestionAnswersList().size())) {
            QuestionAnswers questionAnswers = questionAnswersSet.getQuestionAnswersList().get(currentQuestionIndex);
            Language language = localeService.getLanguage(httpSession);
            QuestionAnswersSourceDto sourceDto = new QuestionAnswersSourceDto(
                    LocalizationUtils.getString(questionAnswersSet.getName(), language),
                    currentQuestionIndex,
                    questionAnswersSet.getQuestionAnswersList().size(),
                    questionAnswersSet.getLogoFileName(),
                    correctAnswerIds,
                    yourAnswerIds
            );

            return dtoFunction.apply(
                    sourceDto,
                    questionAnswers,
                    language);
        } else {
            return null;
        }
    }

    @GetMapping("/photo-names")
    @ResponseBody
    public PhotoNamesDto getPhotoNames(HttpSession httpSession) {
        return getDto(httpSession, PhotoNamesDto::convertToDto);
    }

    @GetMapping("/name-photos")
    @ResponseBody
    public NamePhotosDto getNamePhotos(HttpSession httpSession) {
        return getDto(httpSession, NamePhotosDto::convertToDto);
    }

    @GetMapping("/speaker-talks")
    @ResponseBody
    public SpeakersTalksDto getSpeakerTalks(HttpSession httpSession) {
        return getDto(httpSession, SpeakersTalksDto::convertToDto);
    }

    @GetMapping("/talk-speakers")
    @ResponseBody
    public TalkSpeakersDto getTalkSpeakers(HttpSession httpSession) {
        return getDto(httpSession, TalkSpeakersDto::convertToDto);
    }

    @GetMapping("/speaker-companies")
    @ResponseBody
    public SpeakerCompaniesDto getSpeakerCompanies(HttpSession httpSession) {
        return getDto(httpSession, SpeakerCompaniesDto::convertToDto);
    }

    @GetMapping("/company-speakers")
    @ResponseBody
    public CompanySpeakersDto getCompanySpeakers(HttpSession httpSession) {
        return getDto(httpSession, CompanySpeakersDto::convertToDto);
    }

    @GetMapping("/speaker-accounts")
    @ResponseBody
    public SpeakerAccountsDto getSpeakerAccounts(HttpSession httpSession) {
        return getDto(httpSession, SpeakerAccountsDto::convertToDto);
    }

    @GetMapping("/account-speakers")
    @ResponseBody
    public AccountSpeakersDto getAccountSpeakers(HttpSession httpSession) {
        return getDto(httpSession, AccountSpeakersDto::convertToDto);
    }
}
