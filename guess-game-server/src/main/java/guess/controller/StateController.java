package guess.controller;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.QuestionAnswers;
import guess.domain.QuestionAnswersSet;
import guess.domain.State;
import guess.dto.NamePicturesDto;
import guess.dto.PictureNamesDto;
import guess.dto.StartParametersDto;
import guess.service.AnswerService;
import guess.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * State controller.
 */
@Controller
@RequestMapping("/api/state")
public class StateController {
    private StateService stateService;
    private AnswerService answerService;

    @Autowired
    public StateController(StateService stateService, AnswerService answerService) {
        this.stateService = stateService;
        this.answerService = answerService;
    }

    @PostMapping("/parameters")
    @ResponseStatus(HttpStatus.OK)
    public void setStartParameters(@RequestBody StartParametersDto startParameters) throws QuestionSetNotExistsException {
        stateService.setStartParameters(StartParametersDto.convertFromDto(startParameters));
    }

    @GetMapping("/state")
    @ResponseBody
    public State getState() {
        return stateService.getState();
    }

    @PutMapping("/state")
    @ResponseStatus(HttpStatus.OK)
    public void setState(@RequestBody String state) {
        stateService.setState(State.valueOf(state));
    }

    @GetMapping("/picture-names")
    @ResponseBody
    public PictureNamesDto getPictureNames() {
        int currentQuestionIndex = answerService.getCurrentQuestionIndex();
        QuestionAnswersSet questionAnswersSet = stateService.getQuestionAnswersSet();
        List<Long> wrongAnswerIds = answerService.getWrongAnswerIds(currentQuestionIndex);

        if ((questionAnswersSet != null) && (currentQuestionIndex < questionAnswersSet.getQuestionAnswersList().size())) {
            QuestionAnswers questionAnswers = questionAnswersSet.getQuestionAnswersList().get(currentQuestionIndex);

            return PictureNamesDto.convertToDto(
                    questionAnswersSet.getName(),
                    currentQuestionIndex,
                    questionAnswersSet.getQuestionAnswersList().size(),
                    questionAnswersSet.getDirectoryName(),
                    questionAnswersSet.getLogoFileName(),
                    questionAnswers,
                    wrongAnswerIds);
        } else {
            return null;
        }
    }

    @GetMapping("/name-pictures")
    @ResponseBody
    public NamePicturesDto getNamePictures() {
        int currentQuestionIndex = answerService.getCurrentQuestionIndex();
        QuestionAnswersSet questionAnswersSet = stateService.getQuestionAnswersSet();
        List<Long> wrongAnswerIds = answerService.getWrongAnswerIds(currentQuestionIndex);

        if ((questionAnswersSet != null) && (currentQuestionIndex < questionAnswersSet.getQuestionAnswersList().size())) {
            QuestionAnswers questionAnswers = questionAnswersSet.getQuestionAnswersList().get(currentQuestionIndex);

            return NamePicturesDto.convertToDto(
                    questionAnswersSet.getName(),
                    currentQuestionIndex,
                    questionAnswersSet.getQuestionAnswersList().size(),
                    questionAnswersSet.getDirectoryName(),
                    questionAnswersSet.getLogoFileName(),
                    questionAnswers,
                    wrongAnswerIds);
        } else {
            return null;
        }
    }
}
