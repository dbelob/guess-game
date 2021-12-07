package guess.controller;

import guess.domain.GameState;
import guess.dto.guess.*;
import guess.dto.start.StartParametersDto;
import guess.service.AnswerService;
import guess.service.LocaleService;
import guess.service.StateService;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * State controller.
 */
@RestController
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

    @DeleteMapping("/parameters")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStartParameters(HttpSession httpSession) {
        stateService.deleteStartParameters(httpSession);
    }

    @GetMapping("/state")
    public GameState getState(HttpSession httpSession) {
        return stateService.getState(httpSession);
    }

    @PutMapping("/state")
    @ResponseStatus(HttpStatus.OK)
    public void setState(@RequestBody String state, HttpSession httpSession) {
        stateService.setState(GameState.valueOf(state), httpSession);
    }

    <T> T getDto(HttpSession httpSession, DtoFunction<T> dtoFunction) {
        int currentQuestionIndex = answerService.getCurrentQuestionIndex(httpSession);
        var questionAnswersSet = stateService.getQuestionAnswersSet(httpSession);
        List<Long> correctAnswerIds = answerService.getCorrectAnswerIds(currentQuestionIndex, httpSession);
        List<Long> yourAnswerIds = answerService.getYourAnswerIds(currentQuestionIndex, httpSession);

        if ((questionAnswersSet != null) && (currentQuestionIndex < questionAnswersSet.questionAnswersList().size())) {
            var questionAnswers = questionAnswersSet.questionAnswersList().get(currentQuestionIndex);
            var language = localeService.getLanguage(httpSession);
            var sourceDto = new QuestionAnswersSourceDto(
                    LocalizationUtils.getString(questionAnswersSet.name(), language),
                    currentQuestionIndex,
                    questionAnswersSet.questionAnswersList().size(),
                    questionAnswersSet.logoFileName(),
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
    public PhotoNamesDto getPhotoNames(HttpSession httpSession) {
        return getDto(httpSession, PhotoNamesDto::convertToDto);
    }

    @GetMapping("/name-photos")
    public NamePhotosDto getNamePhotos(HttpSession httpSession) {
        return getDto(httpSession, NamePhotosDto::convertToDto);
    }

    @GetMapping("/speaker-talks")
    public SpeakersTalksDto getSpeakerTalks(HttpSession httpSession) {
        return getDto(httpSession, SpeakersTalksDto::convertToDto);
    }

    @GetMapping("/talk-speakers")
    public TalkSpeakersDto getTalkSpeakers(HttpSession httpSession) {
        return getDto(httpSession, TalkSpeakersDto::convertToDto);
    }

    @GetMapping("/speaker-companies")
    public SpeakerCompaniesDto getSpeakerCompanies(HttpSession httpSession) {
        return getDto(httpSession, SpeakerCompaniesDto::convertToDto);
    }

    @GetMapping("/company-speakers")
    public CompanySpeakersDto getCompanySpeakers(HttpSession httpSession) {
        return getDto(httpSession, CompanySpeakersDto::convertToDto);
    }

    @GetMapping("/speaker-accounts")
    public SpeakerAccountsDto getSpeakerAccounts(HttpSession httpSession) {
        return getDto(httpSession, SpeakerAccountsDto::convertToDto);
    }

    @GetMapping("/account-speakers")
    public AccountSpeakersDto getAccountSpeakers(HttpSession httpSession) {
        return getDto(httpSession, AccountSpeakersDto::convertToDto);
    }

    @GetMapping("/speaker-tag-clouds")
    public SpeakerTagCloudsDto getSpeakerTagClouds(HttpSession httpSession) {
        return getDto(httpSession, SpeakerTagCloudsDto::convertToDto);
    }

    @GetMapping("/tag-cloud-speakers")
    public TagCloudSpeakersDto getTagCloudSpeakers(HttpSession httpSession) {
        return getDto(httpSession, TagCloudSpeakersDto::convertToDto);
    }
}
