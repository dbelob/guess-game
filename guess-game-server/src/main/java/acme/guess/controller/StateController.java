package acme.guess.controller;

import acme.guess.dao.exception.QuestionSetNotExistsException;
import acme.guess.domain.AnswerSet;
import acme.guess.domain.QuestionAnswers;
import acme.guess.domain.QuestionAnswersSet;
import acme.guess.domain.State;
import acme.guess.dto.NamePicturesDto;
import acme.guess.dto.PictureNamesDto;
import acme.guess.dto.StartParametersDto;
import acme.guess.service.AnswerService;
import acme.guess.service.StateService;
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
        QuestionAnswersSet questionAnswersSet = stateService.getQuestionAnswersSet();
        List<AnswerSet> answerSets = answerService.getAnswerSets();
        QuestionAnswers questionAnswers = stateService.getQuestionAnswers();

        if ((questionAnswersSet != null) && (answerSets != null) && (questionAnswers != null)) {
            return PictureNamesDto.convertToDto(
                    questionAnswersSet.getName(),
                    answerSets.size() + 1,
                    questionAnswersSet.getQuestionAnswersList().size(),
                    questionAnswersSet.getDirectoryName(),
                    questionAnswers);
        } else {
            return null;
        }
    }

    @GetMapping("/name-pictures")
    @ResponseBody
    public NamePicturesDto getNamePictures() {
        QuestionAnswers questionAnswers = stateService.getQuestionAnswers();

        //TODO: implement
        return null;
    }
}
