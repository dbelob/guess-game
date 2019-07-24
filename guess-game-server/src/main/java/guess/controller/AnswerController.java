package guess.controller;

import guess.domain.ErrorDetails;
import guess.domain.Result;
import guess.dto.ErrorDetailsDto;
import guess.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Answer controller.
 */
@Controller
@RequestMapping("/api/answer")
public class AnswerController {
    private AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/answers")
    @ResponseStatus(HttpStatus.OK)
    public void addAnswer(@RequestParam int questionIndex, @RequestParam long answerId) {
        answerService.setAnswer(questionIndex, answerId);
    }

    @GetMapping("/result")
    @ResponseBody
    public Result getResult() {
        return answerService.getResult();
    }

    @GetMapping("/errors")
    @ResponseBody
    public List<ErrorDetailsDto> getErrors() {
        List<ErrorDetails> errorDetailsList = answerService.getErrorDetailsList();

        return ErrorDetailsDto.convertToDto(errorDetailsList);
    }
}
